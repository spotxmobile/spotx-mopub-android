//
//  Copyright (c) 2015 SpotXchange, Inc. All rights reserved.
//
package com.spotxchange.sdk.mopubintegrationtest;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.spotxchange.v3.adapters.mopub.SpotXInterstitial;
import com.spotxchange.v3.adapters.mopub.SpotXRewardedVideo;

import java.util.Set;


public class MainActivityFragment extends Fragment {
    private final String TAG = MainActivityFragment.class.getSimpleName();
    private Context _ctx;
    private Activity _act;

    static {
        WebView.setWebContentsDebuggingEnabled(true);
    }

    private MoPubInterstitialAdapter _interstitialAdapter;
    private MoPubRewardedVideoAdapter _rewardedVideoAdapter;
    private MoPubInterstitial _interstitial;

    public MainActivityFragment() {
        Log.i("AD", SpotXInterstitial.class.toString());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        _interstitialAdapter = new MoPubInterstitialAdapter(activity);
        _rewardedVideoAdapter = new MoPubRewardedVideoAdapter(activity);
        _act = activity;
        _ctx = _act.getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ListView interstitialListView = (ListView) view.findViewById(R.id.interstitial);
        interstitialListView.setAdapter(_interstitialAdapter);
        interstitialListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String adUnitId = _interstitialAdapter.getItem(position);

                _interstitial = new MoPubInterstitial(getActivity(), adUnitId);
                _interstitial.setInterstitialAdListener(new InterstitialListener());
                _interstitial.load();
            }
        });


        ListView rewardedVideoListView = (ListView) view.findViewById(R.id.rewardedvideo);
        rewardedVideoListView.setAdapter(_rewardedVideoAdapter);
        rewardedVideoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String adUnitId = _rewardedVideoAdapter.getItem(position);

                MoPub.initializeRewardedVideo(_act);
                MoPub.onCreate(_act);
                MoPub.setRewardedVideoListener(getRewardedVideoListener());
                MoPub.loadRewardedVideo(adUnitId, new SpotXRewardedVideo.SpotXMediationSettings.Builder()
                        .withChannelId("85394").build());
            }
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        destroy();
    }

    private void destroy() {
        if (_interstitial != null) {
            _interstitial.destroy();
            _interstitial = null;
        }
    }

    class InterstitialListener implements MoPubInterstitial.InterstitialAdListener
    {
        public InterstitialListener(){}


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
    }

    private MoPubRewardedVideoListener getRewardedVideoListener(){

        return new MoPubRewardedVideoListener() {

            @Override
            public void onRewardedVideoLoadSuccess(String adUnitId) {

                showToast("Video loaded.");
                MoPub.showRewardedVideo(adUnitId);
            }

            @Override
            public void onRewardedVideoLoadFailure(String adUnitId, MoPubErrorCode errorCode) {

                showToast("Video load failed: " + errorCode.toString());
            }

            @Override
            public void onRewardedVideoStarted(String adUnitId) {

            }

            @Override
            public void onRewardedVideoPlaybackError(String adUnitId, MoPubErrorCode errorCode) {

                showToast("Video playback Error.");
            }

            @Override
            public void onRewardedVideoClosed(String adUnitId) {

            }

            @Override
            public void onRewardedVideoCompleted(Set<String> adUnitIds, MoPubReward reward) {
                showToast("Video completed, you recieved " + reward.getAmount() + " " + reward.getLabel());
            }

            private void showToast(CharSequence text){

                Toast.makeText(_ctx, text, Toast.LENGTH_SHORT).show();
            }
        };
    }
}
