package com.spotxchange.sdk.mopubintegration;

import com.spotxchange.integration.mraid.utils.Constants;
import com.spotxchange.sdk.mopubintegration.SpotXInterstitial;

import org.junit.Test;
import junit.framework.Assert;
import java.util.HashMap;
import java.util.Map;

public class SpotXInterstitialTest {
    @Test
    public void testMapConversionSupportsLiterals() {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("int", -1337);
        map.put("string", "foo.bar");
        map.put("boolean", true);
        map.put("null", null);

        Map<String, String> convertedMap = SpotXInterstitial.convertStringObjectMapToStringStringMap(map);

        Assert.assertEquals(true, Boolean.parseBoolean(convertedMap.get("boolean")));
        Assert.assertEquals(-1337, Integer.parseInt(convertedMap.get("int")));
        Assert.assertEquals("foo.bar", convertedMap.get("string"));
        Assert.assertEquals(null, convertedMap.get("null"));
    }

    public void testBuildSettingsWithGoodSettings() {
        Map<String, String> appSettings = new HashMap<String, String>();
        Map<String, String> defaults = new HashMap<String, String>();

        appSettings.put(SpotXInterstitial.CHANNEL_ID_KEY, "-1337");

        defaults.put(Constants.APP_DOMAIN, "com.spotxchange.unittest");

        Map<String, String> convertedMap = SpotXInterstitial.buildSettings(
            appSettings,
            defaults
            );

        Assert.assertEquals(SpotXInterstitial.CHANNEL_ID_KEY, "-1337");
        Assert.assertEquals(Constants.APP_DOMAIN, "com.spotxchange.unittest");
        Assert.assertEquals(Constants.USE_CUSTOM_CLOSE, Constants.DEFAULT_VALUE_USE_CUSTOM_CLOSE);
    }
}

