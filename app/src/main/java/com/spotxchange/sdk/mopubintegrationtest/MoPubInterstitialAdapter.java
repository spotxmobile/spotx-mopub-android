//
//  Copyright (c) 2015 SpotXchange, Inc. All rights reserved.
//
package com.spotxchange.sdk.mopubintegrationtest;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MoPubInterstitialAdapter extends ArrayAdapter<String> {

    private final String[] _adUnitIds = {
        "a12ce801e2b54fa0bb80b25e7562db13",
        "6a02852e88aa429baa0b78b08169a31e",
        "549619f76e5549a2816290e326e8c04d",
        "211e8efa22f94eb491769ea7d100d00e",
        "ed7e8d9c74714b7f93a70010fa67016d",
        "80ae1ed7f1844ddc9a937ae3f22d14a8",
    };

    private final String[] _adUnitNames = {
        "85394 - Cattitude",
        "93029 - Mixpo",
        "116219 - Telemetry",
        "103105 - Sizmek",
        "103316 - Innovid",
        "121277 - Snowmobile SSL",
    };

    public MoPubInterstitialAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_2, android.R.id.text1);
        super.addAll(_adUnitIds);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        TextView title = (TextView) view.findViewById(android.R.id.text1);
        title.setText(_adUnitNames[position]);

        TextView subtitle = (TextView) view.findViewById(android.R.id.text2);
        subtitle.setText(_adUnitIds[position]);

        return view;
    }
}
