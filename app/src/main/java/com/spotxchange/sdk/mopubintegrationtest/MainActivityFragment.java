//
//  Copyright (c) 2015 SpotXchange, Inc. All rights reserved.
//
package com.spotxchange.sdk.mopubintegrationtest;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ListView;

import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.spotxchange.sdk.mopubintegration.SpotXInterstitial;

public class MainActivityFragment extends ListFragment implements MoPubInterstitial.InterstitialAdListener {
    private final String TAG = MainActivityFragment.class.getSimpleName();

    static {
        WebView.setWebContentsDebuggingEnabled(true);
    }

    private MoPubAdUnitAdapter _adapter;
    private MoPubInterstitial _interstitial;

    public MainActivityFragment() {
        Log.i("AD", SpotXInterstitial.class.toString());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        _adapter = new MoPubAdUnitAdapter(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setAdapter(_adapter);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        destroy();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String adUnitId = _adapter.getItem(position);

        _interstitial = new MoPubInterstitial(getActivity(), adUnitId);
        _interstitial.setInterstitialAdListener(this);
        _interstitial.load();
    }


    @Override
    public void onInterstitialLoaded(MoPubInterstitial moPubInterstitial) {
        _interstitial.show();
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial moPubInterstitial, MoPubErrorCode moPubErrorCode) {
        destroy();
    }

    @Override
    public void onInterstitialShown(MoPubInterstitial moPubInterstitial) {

    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial moPubInterstitial) {

    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial moPubInterstitial) {
        destroy();
    }


    private void destroy() {
        if (_interstitial != null) {
            _interstitial.destroy();
            _interstitial = null;
        }
    }
}
