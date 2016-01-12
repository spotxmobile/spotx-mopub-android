package com.spotxchange.sdk.mopubintegration;

import com.mopub.mobileads.CustomEventRewardedVideo;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.mopub.common.DataKeys;
import com.mopub.common.LifecycleListener;
import com.mopub.common.BaseLifecycleListener;
import com.mopub.common.MediationSettings;
import com.mopub.common.MoPubReward;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubRewardedVideoManager;

import java.util.*;

import com.spotxchange.sdk.android.SpotxAdListener;
import com.spotxchange.sdk.android.SpotxAdSettings;
import com.spotxchange.sdk.android.SpotxAdView;

/**
 *
 */
public class SpotxRewardedVideo extends CustomEventRewardedVideo
{

    /*
     * Constants inded for internal MoPub use. Do not modify.
     */
    public static final String SPOTX_AD_NETWORK_CONSTANT = "spotx";

    private SpotxAdView _oSpotxAdView;
    private  SpotxRewardedVideoListener _oSpotxListener;
    private boolean _bInitialized;
    private String _sAdUnitId;
    private boolean _bIsAdAvailable;

    private static final LifecycleListener _oLifecycleListener = new BaseLifecycleListener(){
        @Override
        public void onPause(@NonNull final Activity activity){
            super.onResume(activity);
        }

        @Override
        public void onResume(@NonNull final Activity activity){
            super.onResume(activity);
        }
    };

    public SpotxRewardedVideo(){
        _oSpotxAdView = null;
        _bIsAdAvailable = false;
        _oSpotxListener = new SpotxRewardedVideoListener();
        _bInitialized = false;
        _sAdUnitId = null;
    }

    @Nullable
    @Override
    protected CustomEventRewardedVideoListener getVideoListenerForSdk() {
        return _oSpotxListener;
    }

    /**
     * Provides a {@link LifecycleListener} if the custom event's ad network wishes to be notified of
     * activity lifecycle events in the application.
     *
     * @return a LifecycleListener. May be null.
     */
    @Nullable
    // @VisibleForTesting
    protected LifecycleListener getLifecycleListener(){
        return _oLifecycleListener;
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
        _bIsAdAvailable = false;
    }

    /**
     * Sets up the 3rd party ads SDK if it needs configuration. Extenders should use this
     * to do any static initialization the first time this method is run by any class instance.
     * From then on, the SDK should be reused without initialization.
     *
     * @return true if the SDK performed initialization, false if the SDK was already initialized.
     */
    protected  boolean checkAndInitializeSdk(
            @NonNull Activity launcherActivity,
            @NonNull Map<String, Object> localExtras,
            @NonNull Map<String, String> serverExtras)
            throws Exception{

        synchronized (SpotxRewardedVideo.class){
            if(!_bInitialized){
                SpotxAdSettings adSettings = Common.constructAdSettings(
                        localExtras,
                        serverExtras,
                        false);

                _oSpotxAdView = new SpotxAdView(launcherActivity, adSettings);
                _bInitialized = true;
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
    protected  void loadWithSdkInitialized(
            @NonNull Activity activity,
            @NonNull Map<String, Object> localExtras,
            @NonNull Map<String, String> serverExtras)
            throws Exception{

        SpotxAdSettings adSettings = Common.constructAdSettings(localExtras, serverExtras, false);
        _oSpotxAdView.setAdSettings(adSettings);
        _oSpotxAdView.setAdListener(_oSpotxListener);
        _oSpotxAdView.setVisibility(View.INVISIBLE);

        activity.addContentView(
                _oSpotxAdView,
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

        _oSpotxAdView.init();

        Object adUnitObject = localExtras.get(DataKeys.AD_UNIT_ID_KEY);
        if(adUnitObject instanceof String){
            this._sAdUnitId = (String) adUnitObject;
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
        return _bIsAdAvailable;
    }

    /**
     * Implementers should now play the rewarded video for this custom event.
     */
    protected void showVideo(){
        _oSpotxAdView.setVisibility(View.VISIBLE);
    }

    private class SpotxRewardedVideoListener
            implements CustomEventRewardedVideoListener, SpotxAdListener {

        @Override
        public void adLoaded() {
            MoPubRewardedVideoManager.onRewardedVideoLoadSuccess(
                    SpotxRewardedVideo.class,
                    SPOTX_AD_NETWORK_CONSTANT);
            _bIsAdAvailable = true;
        }

        @Override
        public void adStarted() {
            MoPubRewardedVideoManager.onRewardedVideoStarted(
                    SpotxRewardedVideo.class,
                    SPOTX_AD_NETWORK_CONSTANT);
        }

        @Override
        public void adCompleted() {
            _oSpotxAdView.setVisibility(View.INVISIBLE);
            MoPubRewardedVideoManager.onRewardedVideoCompleted(
                    SpotxRewardedVideo.class,
                    SPOTX_AD_NETWORK_CONSTANT,
                    MoPubReward.success(MoPubReward.NO_REWARD_LABEL, MoPubReward.NO_REWARD_AMOUNT));
        }

        @Override
        public void adError() {
            MoPubRewardedVideoManager.onRewardedVideoLoadFailure(
                    SpotxRewardedVideo.class,
                    SPOTX_AD_NETWORK_CONSTANT,
                    MoPubErrorCode.UNSPECIFIED);
        }

        @Override
        public void adExpired() {
            MoPubRewardedVideoManager.onRewardedVideoLoadFailure(
                    SpotxRewardedVideo.class,
                    SPOTX_AD_NETWORK_CONSTANT,
                    MoPubErrorCode.UNSPECIFIED);
        }

        @Override
        public void adClicked() {
            MoPubRewardedVideoManager.onRewardedVideoClicked(
                    SpotxRewardedVideo.class,
                    SPOTX_AD_NETWORK_CONSTANT);
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