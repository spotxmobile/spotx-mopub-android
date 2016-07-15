package com.spotxchange.sdk.mopubintegration;

import com.mopub.mobileads.CustomEventRewardedVideo;

import android.app.Activity;
import android.content.Context;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


/**
 *
 */
public class SpotXRewardedVideo extends CustomEventRewardedVideo {

    /*
     * Constants inded for internal MoPub use. Do not modify.
     */
    public static final String SPOTX_AD_NETWORK_CONSTANT = "spotx";

    private @Nullable SpotXRewardedVideoListener _spotxListener;
    private @Nullable Context _ctx;
    private @Nullable SpotXAdGroup _adGroup;
    private boolean _initialized;
    private boolean _isAdAvailable;
    private String _rewardLabel;
    private int _rewardAmount;

    public SpotXRewardedVideo(){
        _initialized = false;
        _isAdAvailable = false;
        _spotxListener = new SpotXRewardedVideoListener();
        _rewardLabel = MoPubReward.NO_REWARD_LABEL;
        _rewardAmount = MoPubReward.NO_REWARD_AMOUNT;
    }

    /**
     * Called to when the custom event is no longer used.
     */
    @Override
    protected void onInvalidate(){
        _adGroup = null;
        _spotxListener = null;
        _isAdAvailable = false;
        _rewardLabel = MoPubReward.NO_REWARD_LABEL;
        _rewardAmount = MoPubReward.NO_REWARD_AMOUNT;
    }

    /**
     * Return the CustomEventRewardedVideoListener for the SpotX SDK
     */
    @Nullable
    @Override
    protected CustomEventRewardedVideoListener getVideoListenerForSdk() {
        return _spotxListener;
    }

    /**
     * Return the LifecycleListner for the SDK.  This listener is null.
     *
     * @return - null
     */
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
    @Override
    protected String getAdNetworkId(){
        return SPOTX_AD_NETWORK_CONSTANT;
    };

    /**
     * Sets up the 3rd party ads SDK if it needs configuration.
     *
     * @return true if the SDK performed initialization, false if the SDK was already initialized.
     */
    @Override
    protected  boolean checkAndInitializeSdk(Activity launcherActivity, Map<String, Object> localExtras,
            Map<String, String> serverExtras) throws Exception{

        synchronized (SpotXRewardedVideo.class){
            if(!_initialized){
                _ctx = launcherActivity;
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
     * @param activity the "main activity" of the app. Useful for initializing sdks.
     * @param localExtras
     * @param serverExtras
     */
    @Override
    protected void loadWithSdkInitialized(Activity activity, Map<String, Object> localExtras,
            Map<String, String> serverExtras) throws Exception {

        String adUnitId = (String) localExtras.get(DataKeys.AD_UNIT_ID_KEY);

        // Merge the local and server settings, then add the mediation settings
        Map<String, String> adSettings = Common.mergeSettings(localExtras, serverExtras);
        SpotXMediationSettings ms =
                MoPubRewardedVideoManager.getInstanceMediationSettings(SpotXMediationSettings.class, adUnitId);
        if(ms != null && ms.channel_id != null && !ms.channel_id.isEmpty()) {
            adSettings.put("channel_id", ms.channel_id);
        }

        // Load the video. Fail if there is no channel id.
        if(adSettings.containsKey("channel_id")) {
            SpotXAdBuilder sab = SpotX.newAdBuilder(adSettings.get("channel_id"));
            parseReward(adSettings);
            Common.insertParams(sab, adSettings);
            AsyncLoader al = new AsyncLoader(sab, new RewardedVideoCallback());
            al.execute();
        }
        else {
            fail(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        }
    }

    /**
     * Returns true if a video has an ad loaded, false otherwise
     *
     * @return boolean
     */
    @Override
    protected boolean hasVideoAvailable(){
        return _isAdAvailable;
    }

    /**
     * Displays an interstitial video
     */
    @Override
    protected void showVideo(){
        if(_ctx != null && _adGroup != null) {
            InterstitialPresentationController.show(_ctx, _adGroup);
        }
    }

    /**
     * Parse the reward amount and reward label from the settings object.
     * Publishers are allowed to to pass these params via the MoPub UI.
     *
     * @param settings - Settings passed via MoPub
     */
    private void parseReward(Map<String, String> settings) {
        if(settings.containsKey("reward_amount")){
            try {
                _rewardAmount = Integer.parseInt(settings.get("reward_amount"));
            }
            catch (NumberFormatException e) {}
        }
        if(settings.containsKey("reward_label")) {
            _rewardLabel = settings.get("reward_label");
        }
    }

    /**
     * Convenience method for a rewarded video load failure
     *
     * @param error - MoPubErrorCode to throw
     */
    private void fail(MoPubErrorCode error) {
        MoPubRewardedVideoManager.onRewardedVideoLoadFailure(SpotXRewardedVideo.class, SPOTX_AD_NETWORK_CONSTANT, error);
    }

    //MARK: SpotXMediationSettings

    /**
     * Settings that implementers can pass via SDK construction
     */
    public static class SpotXMediationSettings implements MediationSettings
    {
        private final String channel_id;

        public static class Builder {
            private String channel_id;

            public Builder withChannelId(final String channel_id) {
                this.channel_id = channel_id;
                return this;
            }

            public SpotXMediationSettings build() {
                return new SpotXMediationSettings(this);
            }
        }

        private SpotXMediationSettings(final Builder builder) {
            this.channel_id = builder.channel_id;
        }
    }

    //MARK: SpotXRewardedVideoListener

    /**
     * Used to listen to MoPub events and SpotX events. CustomEventRewardedVideoListener is an empty interface, so
     * only SpotX events are implemented.
     */
    private class SpotXRewardedVideoListener implements CustomEventRewardedVideoListener, SpotXAdGroup.Observer {

        @Override
        public void onGroupStart() {
            MoPubRewardedVideoManager.onRewardedVideoStarted(SpotXRewardedVideo.class, SPOTX_AD_NETWORK_CONSTANT);
        }

        @Override
        public void onGroupComplete() {
            MoPubRewardedVideoManager.onRewardedVideoCompleted(SpotXRewardedVideo.class, SPOTX_AD_NETWORK_CONSTANT,
                    MoPubReward.success(_rewardLabel, _rewardAmount));
            _isAdAvailable = false;
        }

        @Override
        public void onClick(SpotXAd spotXAd) {
            MoPubRewardedVideoManager.onRewardedVideoClicked(SpotXRewardedVideo.class, SPOTX_AD_NETWORK_CONSTANT);
        }

        @Override
        public void onError(SpotXAd spotXAd, Error error) {
            fail(MoPubErrorCode.UNSPECIFIED);
            _isAdAvailable = false;
        }

        @Override
        public void onStart(SpotXAd spotXAd) {}

        @Override
        public void onComplete(SpotXAd spotXAd) {}

        @Override
        public void onSkip(SpotXAd spotXAd) {}

        @Override
        public void onTimeUpdate(SpotXAd spotXAd, int i) {}
    }

    // MARK: RewardedVideoCallback

    /**
     * AsyncLoader callback used to handle successful or erroneous ad loads.
     */
    private class RewardedVideoCallback implements AsyncLoader.Callback{

        @Override
        public void adLoadingStarted() {}

        @Override
        public void adLoadingFinished(@Nullable SpotXAdGroup adGroup) {
            _adGroup = adGroup;
            if(_adGroup == null) {
                _isAdAvailable = false;
            }
            else if(_adGroup.isEmpty()){
                _isAdAvailable = false;
                fail(MoPubErrorCode.NETWORK_NO_FILL);
            }
            else {
                _isAdAvailable = true;
                if(_spotxListener != null) {
                    _adGroup.registerObserver(_spotxListener);
                }
                MoPubRewardedVideoManager.onRewardedVideoLoadSuccess(SpotXRewardedVideo.class, SPOTX_AD_NETWORK_CONSTANT);
            }
        }

        @Override
        public void adLoadingError(Exception e) {
            _isAdAvailable = false;
            if(e instanceof InterruptedException) {
                fail(MoPubErrorCode.CANCELLED);
            }
            else if(e instanceof ExecutionException) {
                fail(MoPubErrorCode.UNSPECIFIED);
            }
            else if(e instanceof TimeoutException) {
                fail(MoPubErrorCode.NETWORK_TIMEOUT);
            }
            else {
                fail(MoPubErrorCode.UNSPECIFIED);
            }
        }
    }
}