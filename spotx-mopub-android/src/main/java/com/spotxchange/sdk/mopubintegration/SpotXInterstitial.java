package com.spotxchange.sdk.mopubintegration;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.mopub.mobileads.CustomEventInterstitial;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.mopub.mobileads.MoPubErrorCode;
import com.spotxchange.v3.SpotX;
import com.spotxchange.v3.SpotXAd;
import com.spotxchange.v3.SpotXAdBuilder;
import com.spotxchange.v3.SpotXAdGroup;
import com.spotxchange.v3.view.InterstitialPresentationController;


public class SpotXInterstitial extends CustomEventInterstitial {

    private CustomEventInterstitialListener _customEventInterstitialListener;
    private Context _ctx;
    private Future<SpotXAdGroup> _adFuture;
    private SpotXAdGroup _adGroup;

    private final SpotXAdGroup.Observer _spotxAdListener = new SpotXAdGroup.Observer() {
        @Override
        public void onGroupStart() {
            if(_customEventInterstitialListener != null) {
                _customEventInterstitialListener.onInterstitialShown();
            }
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
            if(_customEventInterstitialListener != null) {
                //TODO: Infer and specify which kind of error this is based off event log tracking.
                _customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.UNSPECIFIED);
            }
        }

        @Override
        public void onGroupComplete() {
            if(_customEventInterstitialListener != null) {
                _customEventInterstitialListener.onInterstitialDismissed();
            }
        }

        @Override
        public void onTimeUpdate(SpotXAd spotXAd, int i) {

        }

        @Override
        public void onClick(SpotXAd spotXAd) {
            if(_customEventInterstitialListener != null) {
                _customEventInterstitialListener.onInterstitialClicked();
            }
        }
    };


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
        SpotXAdBuilder sab = SpotX.newAdBuilder(settings.get("channel_id"));
        _adFuture = sab.load();
        try {
            _adGroup = _adFuture.get(10000, TimeUnit.MILLISECONDS);
            _adGroup.registerObserver(_spotxAdListener);
            if(_customEventInterstitialListener != null) {
                _customEventInterstitialListener.onInterstitialLoaded();
            }
        }
        catch (Exception e) {
            if(_customEventInterstitialListener != null) {
                //TODO: Infer and specify which kind of error this is based off event log tracking.
                _customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.UNSPECIFIED);
            }
        }
    }

    @Override
    protected void showInterstitial() {
        if(_adGroup != null){
            InterstitialPresentationController.show(_ctx, _adGroup);
        }
    }

    @Override
    protected void onInvalidate() {
        _adGroup = null;
    }
}
