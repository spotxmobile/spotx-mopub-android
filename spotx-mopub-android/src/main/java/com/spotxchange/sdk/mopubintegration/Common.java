package com.spotxchange.sdk.mopubintegration;

import java.util.HashMap;
import java.util.Map;

public class Common
{
    public static final String CHANNEL_ID_KEY           = "channel_id";
    public static final String IAB_CATEGORY_KEY         = "iab_category";
    public static final String APP_STORE_URL_KEY        = "appstore_url";
    public static final String PLAY_STORE_URL_KEY       = "playstore_url";
    public static final String APP_DOMAIN_KEY           = "app_domain";
    public static final String PREFETCH_KEY             = "prefetch";
    public static final String AUTO_INIT_KEY            = "auto_init";
    public static final String IN_APP_BROWSER_KEY       = "in_app_browser";
    public static final String SECURE_CONNECTION_KEY    = "use_https";

    public static Map<String, String> mergeSettings(Map<String,Object> localSettings, Map<String, String> defaultSettings) {
        Map<String,String> settings = defaultSettings;
        settings.putAll(
                convertStringObjectMapToStringStringMap(localSettings)
        );
        return settings;
    }

    private static Map<String,String> convertStringObjectMapToStringStringMap(Map<String, Object> map) {
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
