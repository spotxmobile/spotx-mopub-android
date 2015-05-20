package com.spotxchange.sdk.mopubintegration;

import android.content.Context;
import android.view.View;

import com.mopub.mobileads.CustomEventInterstitial;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import com.mopub.mobileads.MoPubErrorCode;
import com.spotxchange.integration.mraid.SpotXProperties;
import com.spotxchange.integration.mraid.SpotXView;
import com.spotxchange.integration.mraid.enumerations.PlacementType;
import com.spotxchange.integration.mraid.enumerations.VpaidEvent;
import com.spotxchange.integration.mraid.utils.VpaidEventListener;


public class SpotXInterstitial extends CustomEventInterstitial {
    public static final String CHANNEL_ID_KEY           = "channel_id";
    public static final String IAB_CATEGORY_KEY         = "iab_category";
    public static final String APP_STORE_URL_KEY        = "appstore_url";
    public static final String PLAY_STORE_URL_KEY       = "playstore_url";
    public static final String APP_DOMAIN_KEY           = "app_domain";
    public static final String PREFETCH_KEY             = "prefetch";
    public static final String AUTO_INIT_KEY            = "auto_init";
    public static final String IN_APP_BROWSER_KEY       = "in_app_browser";

    private SpotXView _adView;

    /**
     * Translates VPAIDEvents back to CustomEventInterstitialListener events
     */
    private class VpaidEventListenerAdapter implements VpaidEventListener
    {
        private CustomEventInterstitialListener _listener;

        public VpaidEventListenerAdapter(CustomEventInterstitialListener listener)
        {
            _listener = listener;
        }

        @Override
        public void onVpaidEvent(VpaidEvent event) {
            //TODO: VPAID -> onInterstitialLoaded
            //TODO: VPAID -> onInterstitialFailed
            //TODO: VPAID -> onInterstitialShown
            //TODO: VPAID -> onInterstitialClicked
            //TODO: VPAID -> onInterstitialDismissed

            //NOTE: onLeaveApplication not applicable here?
        }
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
        //TODO: Construct and pass AdSettings here
        _adView = new SpotXView(context, PlacementType.INTERSTITIAL);
        _adView.setVisibility(View.INVISIBLE);

        //TODO: Use generic AdListener instead
        _adView.setVpaidEventListener(new VpaidEventListenerAdapter(customEventInterstitialListener));

        Map<String,String> localExtrasAsStrings = convertStringObjectMapToStringStringMap(localExtras);

        SpotXProperties settings = new SpotXProperties(
            buildSettings(
                localExtrasAsStrings,
                serverExtras
                )
            );

        _adView.initialize(
            _adView.getId(),
            Integer.parseInt(serverExtras.get(CHANNEL_ID_KEY)),
            serverExtras.get(APP_DOMAIN_KEY),
            settings
            );
    }

    @Override
    protected void showInterstitial() {
        _adView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onInvalidate() {
        _adView = null;

    }

    private static Map<String,String> convertStringObjectMapToStringStringMap(Map<String, Object> map)
    {
        Map<String, String> newMap = new HashMap<String,String>();
        for (Map.Entry<String, Object> item : map.entrySet())
        {
            newMap.put(item.getKey(), item.getValue().toString());
        }

        return newMap;
    }

    /**
     * @return Default configuration for SpotXViews
     */
    public static Map<String, String> getDefaultSettings()
    {
        return (new SpotXProperties()).getPropertiesMap();
    }

    /**
     * Creates a SpotXProperties object using the passed settings maps
     * @param settings
     * @param defaults
     * @return
     */
    public static Map<String,String> buildSettings(Map<String,String> settings, Map<String, String> defaults)
    {
        Map<String,String> map = getDefaultSettings();
        map.putAll(defaults);
        map.putAll(settings);

        return map;
    }

}
