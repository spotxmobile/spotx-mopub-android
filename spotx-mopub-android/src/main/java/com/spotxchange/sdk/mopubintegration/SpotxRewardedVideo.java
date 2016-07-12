package com.spotxchange.sdk.mopubintegration;

import com.mopub.mobileads.CustomEventRewardedVideo;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mopub.common.DataKeys;
import com.mopub.common.LifecycleListener;
import com.mopub.common.MediationSettings;
import com.mopub.common.MoPubReward;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubRewardedVideoManager;
import com.spotxchange.v3.SpotX;
import com.spotxchange.v3.SpotXAd;
import com.spotxchange.v3.SpotXAdBuilder;
import com.spotxchange.v3.SpotXAdGroup;
import com.spotxchange.v3.view.InterstitialPresentationController;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


/**
 *
 */
public class SpotxRewardedVideo extends CustomEventRewardedVideo {

    /*
     * Constants inded for internal MoPub use. Do not modify.
     */
    public static final String SPOTX_AD_NETWORK_CONSTANT = "spotx";

    private static SpotxRewardedVideoListener _spotxListener;
    private static boolean _initialized;
    private static boolean _isAdAvailable;
    private String _adUnitId;
    private Context _ctx;
    private Future<SpotXAdGroup> _adFuture;
    private SpotXAdGroup _adGroup;


    public SpotxRewardedVideo(){
        _initialized = false;
        _spotxListener = new SpotxRewardedVideoListener();
        _isAdAvailable = false;
        _adUnitId = null;
    }

    @Nullable
    @Override
    protected CustomEventRewardedVideoListener getVideoListenerForSdk() {
        return _spotxListener;
    }

    @Nullable
    @Override
    protected LifecycleListener getLifecycleListener() {
        return null;
    }

    /**
     * Called by the {@link MoPubRewardedVideoManager} after loading the custom event.
     * This should return the "ad unit id", "zone id" or similar identifier for the network.
     * May be empty if the network does not have anything more specific than an application ID.
     *
     * @return the id string for this ad unit with the ad network.
     */
    @NonNull
    protected String getAdNetworkId(){
        return SPOTX_AD_NETWORK_CONSTANT;
    };

    /**
     * Called to when the custom event is no longer used. Implementers should cancel any
     * pending requests. The initialized SDK may be reused by another CustomEvent instance
     * and should not be shut down or cleaned up.
     */
    protected void onInvalidate(){
        _adFuture = null;
        _adGroup = null;
        _spotxListener = null;
    }

    /**
     * Sets up the 3rd party ads SDK if it needs configuration. Extenders should use this
     * to do any static initialization the first time this method is run by any class instance.
     * From then on, the SDK should be reused without initialization.
     *
     * @return true if the SDK performed initialization, false if the SDK was already initialized.
     */
    @NonNull
    protected  boolean checkAndInitializeSdk(Activity launcherActivity, @NonNull Map<String, Object> localExtras,
            @NonNull Map<String, String> serverExtras) throws Exception{

        synchronized (SpotxRewardedVideo.class){

            if(!_initialized){
                _ctx = launcherActivity.getApplicationContext();
                SpotX.initialize(_ctx);
                _initialized = true;
                return true;
            }

            return false;
        }
    }

    /**
     * Runs the ad-loading logic for the 3rd party SDK. localExtras & serverExtras should together
     * contain all the data needed to load an ad.
     *
     * Implementers should also use this method (or checkAndInitializeSdk)
     * to register a listener for their SDK, wrap it in a
     * {@link com.mopub.mobileads.CustomEventRewardedVideo.CustomEventRewardedVideoListener}
     *
     * This method should not call any {@link MoPubRewardedVideoManager} event methods directly
     * (onAdLoadSuccess, etc). Instead the SDK delegate/listener should call these methods.
     *
     * @param activity the "main activity" of the app. Useful for initializing sdks.
     * @param localExtras
     * @param serverExtras
     */
    @NonNull
    protected void loadWithSdkInitialized(Activity activity, Map<String, Object> localExtras,
            Map<String, String> serverExtras) throws Exception{

        // Deconstruct the mediation setings
        _adUnitId = (String)localExtras.get(DataKeys.AD_UNIT_ID_KEY);
        SpotxMediationSettings ms = MoPubRewardedVideoManager.getInstanceMediationSettings(SpotxMediationSettings.class,
                _adUnitId);

        // Get and store the mediation settings channel id
        String channel_id = (ms != null && ms.channel_id != null) ? ms.channel_id : "";
        if(!channel_id.isEmpty()){
            localExtras.put(Common.CHANNEL_ID_KEY, channel_id);
        }

        // Merge all settings objects and get an AdBuilder
        Map<String, String> adSettings = Common.mergeSettings(localExtras, serverExtras);
        SpotXAdBuilder sab = SpotX.newAdBuilder(adSettings.get("channel_id"));

        // Load the video
        try {
            _adFuture = sab.load();
            _adGroup = _adFuture.get(10000, TimeUnit.MILLISECONDS);
            _adGroup.registerObserver(_spotxListener);
            MoPubRewardedVideoManager.onRewardedVideoLoadSuccess(SpotxRewardedVideo.class, SPOTX_AD_NETWORK_CONSTANT);
            _isAdAvailable = true;

        } catch(Exception e) {
            MoPubRewardedVideoManager.onRewardedVideoLoadFailure(SpotxRewardedVideo.class, SPOTX_AD_NETWORK_CONSTANT,
                    MoPubErrorCode.UNSPECIFIED);
            _isAdAvailable = false;
        }
    }

    /**
     * Implementers should query the 3rd party SDK for whether there is a video available for the
     * 3rd party SDK & ID represented by the custom event.
     *
     * @return true iff a video is available to play.
     */
    @Override
    protected boolean hasVideoAvailable(){
        return _isAdAvailable;
    }

    /**
     * Implementers should now play the rewarded video for this custom event.
     */
    protected void showVideo(){
        InterstitialPresentationController.show(_ctx, _adGroup);
    }

    private class SpotxRewardedVideoListener implements CustomEventRewardedVideoListener, SpotXAdGroup.Observer {

        @Override
        public void onGroupStart() {
            MoPubRewardedVideoManager.onRewardedVideoStarted(SpotxRewardedVideo.class, SPOTX_AD_NETWORK_CONSTANT);
        }

        @Override
        public void onStart(SpotXAd spotXAd) {

        }

        @Override
        public void onComplete(SpotXAd spotXAd) {

        }

        @Override
        public void onSkip(SpotXAd spotXAd) {

        }

        @Override
        public void onError(SpotXAd spotXAd, Error error) {
            MoPubRewardedVideoManager.onRewardedVideoLoadFailure(
                    SpotxRewardedVideo.class,
                    SPOTX_AD_NETWORK_CONSTANT,
                    MoPubErrorCode.UNSPECIFIED);
            _isAdAvailable = false;
        }

        @Override
        public void onGroupComplete() {
            MoPubRewardedVideoManager.onRewardedVideoCompleted(SpotxRewardedVideo.class, SPOTX_AD_NETWORK_CONSTANT,
                    MoPubReward.success(MoPubReward.NO_REWARD_LABEL, MoPubReward.NO_REWARD_AMOUNT));
            _isAdAvailable = false;
        }

        @Override
        public void onTimeUpdate(SpotXAd spotXAd, int i) {

        }

        @Override
        public void onClick(SpotXAd spotXAd) {
            MoPubRewardedVideoManager.onRewardedVideoClicked(SpotxRewardedVideo.class, SPOTX_AD_NETWORK_CONSTANT);
        }
    }

    public static class SpotxMediationSettings implements MediationSettings
    {
        @Nullable private final String channel_id;

        public static class Builder {
            @Nullable private String channel_id;

            public Builder withChannelId(@NonNull final String channel_id) {
                this.channel_id = channel_id;
                return this;
    }

            public SpotxMediationSettings build() {
                return new SpotxMediationSettings(this);
}
        }

        private SpotxMediationSettings(@NonNull final Builder builder) {
            this.channel_id = builder.channel_id;
        }
    }
}