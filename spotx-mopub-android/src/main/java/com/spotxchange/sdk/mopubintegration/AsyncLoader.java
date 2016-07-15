package com.spotxchange.sdk.mopubintegration;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.spotxchange.v3.SpotXAdBuilder;
import com.spotxchange.v3.SpotXAdGroup;

import java.util.concurrent.TimeUnit;


public class AsyncLoader  extends AsyncTask<Void, Void, SpotXAdGroup> {
    public interface Callback {
        void adLoadingStarted();
        void adLoadingFinished(@Nullable SpotXAdGroup adGroup);
        void adLoadingError(Exception e);
    }

    private final SpotXAdBuilder _builder;
    private final Callback _callback;


    public AsyncLoader(SpotXAdBuilder builder, @NonNull Callback callback) {
        _builder = builder;
        _callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        _callback.adLoadingStarted();
    }

    @Override
    protected void onPostExecute(SpotXAdGroup adGroup) {
        super.onPostExecute(adGroup);
        _callback.adLoadingFinished(adGroup);
    }

    @Override
    protected SpotXAdGroup doInBackground(Void... params) {
        try {
            return _builder.load().get(10, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            _callback.adLoadingError(e);
            return null;
        }
    }
}
