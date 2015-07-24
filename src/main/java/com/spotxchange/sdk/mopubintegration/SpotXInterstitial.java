package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.mopub.mobileads.CustomEventInterstitial;

import java.util.HashMap;
import java.util.Map;

import com.mopub.mobileads.MoPubErrorCode;
import com.spotxchange.sdk.android.SpotxAdListener;
import com.spotxchange.sdk.android.SpotxAdSettings;
import com.spotxchange.sdk.android.SpotxAdView;


public class SpotXInterstitial extends CustomEventInterstitial {
    public static final String CHANNEL_ID_KEY           = "channel_id";
    public static final String IAB_CATEGORY_KEY         = "iab_category";
    public static final String APP_STORE_URL_KEY        = "appstore_url";
    public static final String PLAY_STORE_URL_KEY       = "playstore_url";
    public static final String APP_DOMAIN_KEY           = "app_domain";
    public static final String PREFETCH_KEY             = "prefetch";
    public static final String AUTO_INIT_KEY            = "auto_init";
    public static final String IN_APP_BROWSER_KEY       = "in_app_browser";

    private SpotxAdView _adView;

    private CustomEventInterstitialListener _customEventInterstitialListener;

    private final SpotxAdListener _spotxAdListener = new SpotxAdListener() {
        @Override
        public void adLoaded() {
            if(_customEventInterstitialListener != null) {
                _customEventInterstitialListener.onInterstitialLoaded();
            }
        }

        @Override
        public void adStarted() {
            if(_customEventInterstitialListener != null) {
                _customEventInterstitialListener.onInterstitialShown();
            }
        }

        @Override
        public void adCompleted() {
            if(_customEventInterstitialListener != null) {
                _customEventInterstitialListener.onInterstitialDismissed();
            }
        }

        @Override
        public void adError() {
            if(_customEventInterstitialListener != null) {
                //TODO: Infer and specify which kind of error this is based off event log tracking.
                _customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.UNSPECIFIED);
            }
        }

        @Override
        public void adExpired() {
            if(_customEventInterstitialListener != null) {
                //TODO: Infer and specify which kind of error this is based off event log tracking.
                _customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.UNSPECIFIED);
            }
        }

        @Override
        public void adClicked() {
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
        _customEventInterstitialListener = customEventInterstitialListener;
        SpotxAdSettings adSettings = constructAdSettings(localExtras, serverExtras);
        _adView = new SpotxAdView(context, adSettings);
        _adView.setVisibility(View.INVISIBLE);
        _adView.setAdListener(_spotxAdListener);
        ((Activity)context).addContentView(_adView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        _adView.init();
    }

    @Override
    protected void showInterstitial() {
        _adView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onInvalidate() {
        _adView = null;
    }

    public static SpotxAdSettings constructAdSettings(Map<String,Object> localSettings, Map<String, String> defaultSettings){
        Map<String,String> localSettingsAsStrings = convertStringObjectMapToStringStringMap(localSettings);
        String channel = (localSettingsAsStrings.containsKey(CHANNEL_ID_KEY)) ? localSettingsAsStrings.get(CHANNEL_ID_KEY) : defaultSettings.get(CHANNEL_ID_KEY);
        String appDomain = (localSettingsAsStrings.containsKey(APP_DOMAIN_KEY)) ? localSettingsAsStrings.get(APP_DOMAIN_KEY) : defaultSettings.get(APP_DOMAIN_KEY);

        SpotxAdSettings adSettings = new SpotxAdSettings(Integer.valueOf(channel), appDomain, "interstitial");

        if(localSettingsAsStrings.containsKey(APP_STORE_URL_KEY) || localSettingsAsStrings.containsKey(PLAY_STORE_URL_KEY)) {
            String storeUrl = localSettings.containsKey(APP_STORE_URL_KEY) ? localSettingsAsStrings.get(APP_STORE_URL_KEY) : localSettingsAsStrings.get(PLAY_STORE_URL_KEY);
            adSettings.setAppStoreUrl(storeUrl);
        }
        if(localSettingsAsStrings.containsKey(IAB_CATEGORY_KEY)) {
            adSettings.setIabCategory(localSettingsAsStrings.get(IAB_CATEGORY_KEY));
        }
        if(localSettingsAsStrings.containsKey(AUTO_INIT_KEY)) {
            boolean autoInit = (localSettingsAsStrings.get(AUTO_INIT_KEY).equals("true")) ? true : false;
            adSettings.setAutoInit(autoInit);
        }
        if(localSettingsAsStrings.containsKey(PREFETCH_KEY)){
            boolean prefetch = (localSettingsAsStrings.get(PREFETCH_KEY).equals("true")) ? true : false;
            adSettings.setPrefetch(prefetch);
        }
        if(localSettingsAsStrings.containsKey(IN_APP_BROWSER_KEY)){
            boolean shouldUse = (localSettingsAsStrings.get(IN_APP_BROWSER_KEY).equals("true")) ? true : false;
            adSettings.setShouldUseInternalBrowser(shouldUse);
        }

        return adSettings;
    }

    public static Map<String,String> convertStringObjectMapToStringStringMap(Map<String, Object> map) {
        Map<String, String> newMap = new HashMap<String,String>();
        for (Map.Entry<String, Object> item : map.entrySet()) {
            if (item.getValue() == null) {
                newMap.put(item.getKey(), null);
            }
            else {
                newMap.put(item.getKey(), item.getValue().toString());
            }
        }
        return newMap;
    }

}
