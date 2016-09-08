//
//  Copyright (c) 2015 SpotXchange, Inc. All rights reserved.
//
package com.spotxchange.sdk.mopubintegrationtest;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MoPubRewardedVideoAdapter extends ArrayAdapter<String> {

    private final String[] _adUnitIds = {
            "5f56564aa4b1487cabb9b59cc3134b93",
    };

    private final String[] _adUnitNames = {
            "85394 - Cattitude - Rewarded Video",
    };

    public MoPubRewardedVideoAdapter(Context context) {
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
