package com.spotxchange.sdk.mopubintegration;

import com.spotxchange.v3.SpotXAdBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class Common
{
    public static Map<String, String> mergeSettings(Map<String,Object> localSettings, Map<String, String> defaultSettings) {

        if(localSettings == null && defaultSettings == null) {
            return new HashMap<String, String>();
        }
        else if(localSettings == null && defaultSettings != null) {
            return defaultSettings;
        }
        else if(localSettings != null && defaultSettings == null) {
            return convertStringObjectMapToStringStringMap(localSettings);
        }
        else {
            Map<String, String> settings = defaultSettings;
            settings.putAll(convertStringObjectMapToStringStringMap(localSettings));
            return settings;
        }
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



    /**
     * Parse any custom or SpotX ad parameters from the settings map and put them
     * to the ad builder.
     *
     * @param sab - SpotXAdBuilder
     * @param settings - Settings passed via MoPub
     */
    public static void insertParams(SpotXAdBuilder sab, Map<String, String> settings) {

        if(sab == null || settings == null) {
            return;
        }

        if(settings.containsKey("use_https")
                && (settings.get("use_https").equals("1") || settings.get("use_https").toLowerCase().equals("true"))) {
            sab.useHTTPS(true);
        }

        for(String k : settings.keySet()){
            if(k.startsWith("spotx_")){
                sab.param(k.replaceFirst("spotx_", ""), settings.get(k));
            }
            else if(k.startsWith("custom_")) {
                sab.custom(k.replaceFirst("custom_", ""), settings.get(k));
            }
        }
    }
}
