package com.spotxchange.sdk.mopubintegration;

import android.content.Context;
import android.support.annotation.Nullable;

import com.mopub.mobileads.CustomEventInterstitial;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.mopub.mobileads.MoPubErrorCode;
import com.spotxchange.v3.SpotX;
import com.spotxchange.v3.SpotXAd;
import com.spotxchange.v3.SpotXAdBuilder;
import com.spotxchange.v3.SpotXAdGroup;
import com.spotxchange.v3.view.InterstitialPresentationController;


public class SpotXInterstitial extends CustomEventInterstitial {

    private CustomEventInterstitialListener _customEventInterstitialListener;
    private Context _ctx;
    private SpotXAdGroup _adGroup;

    private final SpotXAdGroup.Observer _spotxAdListener = new SpotXAdGroup.Observer() {
        @Override
        public void onGroupStart() {
            if(_customEventInterstitialListener != null) {
                _customEventInterstitialListener.onInterstitialShown();
            }
        }

        @Override
        public void onClick(SpotXAd spotXAd) {
            if(_customEventInterstitialListener != null) {
                _customEventInterstitialListener.onInterstitialClicked();
            }
        }

        @Override
        public void onError(SpotXAd spotXAd, Error error) {
            fail(MoPubErrorCode.UNSPECIFIED.UNSPECIFIED);
        }

        @Override
        public void onGroupComplete() {
            if(_customEventInterstitialListener != null) {
                _customEventInterstitialListener.onInterstitialDismissed();
            }
        }

        @Override
        public void onStart(SpotXAd spotXAd) {}

        @Override
        public void onComplete(SpotXAd spotXAd) {}

        @Override
        public void onSkip(SpotXAd spotXAd) {}

        @Override
        public void onTimeUpdate(SpotXAd spotXAd, int i) {}
    };

    @Override
    protected void showInterstitial() {
        if(_adGroup != null && _ctx != null){
            InterstitialPresentationController.show(_ctx, _adGroup);
        }
    }

    @Override
    protected void onInvalidate() {
        _adGroup = null;
    }

    /**
     *
     * @param context
     * @param customEventInterstitialListener
     * @param localExtras
     * @param serverExtras
     * @throws java.lang.IllegalArgumentException if localExtras and serverExtras do not have the required parameters
     */
    @Override
    protected void loadInterstitial(
        Context context,
        CustomEventInterstitialListener customEventInterstitialListener,
        Map<String, Object> localExtras,
        Map<String, String> serverExtras
        )
    {
        _ctx = context;
        _customEventInterstitialListener = customEventInterstitialListener;
        Map<String, String> settings = Common.mergeSettings(localExtras, serverExtras);
        SpotX.initialize(_ctx);
        if(settings.containsKey("channel_id") && !settings.get("channel_id").isEmpty()) {
            SpotXAdBuilder sab = SpotX.newAdBuilder(settings.get("channel_id"));
            Common.insertParams(sab, settings);
            AsyncLoader al = new AsyncLoader(sab, new InterstitialVideoCallback());
            al.execute();
        }
        else {
            fail(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        }
    }

    private void fail(MoPubErrorCode error){
        if(_customEventInterstitialListener != null) {
            _customEventInterstitialListener.onInterstitialFailed(error);
        }
    }

    /**
     * AsyncLoader callback used to handle successful or erroneous ad loads.
     */
    private class InterstitialVideoCallback implements AsyncLoader.Callback {

        @Override
        public void adLoadingStarted() {}

        @Override
        public void adLoadingFinished(@Nullable SpotXAdGroup adGroup) {
            _adGroup = adGroup;
            if(_adGroup == null) {
                return;
            }
            else if(_adGroup.isEmpty()) {
                fail(MoPubErrorCode.NETWORK_NO_FILL);
            }
            else {
                _adGroup.registerObserver(_spotxAdListener);
                if(_customEventInterstitialListener != null) {
                    _customEventInterstitialListener.onInterstitialLoaded();
                }
            }
        }

        @Override
        public void adLoadingError(Exception e) {
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
