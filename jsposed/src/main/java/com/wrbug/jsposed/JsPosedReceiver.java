package com.wrbug.jsposed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.mozilla.javascript.Function;

public class JsPosedReceiver extends BroadcastReceiver {
    private Callback mCallback;

    public JsPosedReceiver(Callback callback) {
        mCallback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mCallback != null) {
            mCallback.onReceive(context, intent);
        }
    }


    public abstract static class Callback {
        protected Function mFunction;

        public Callback(Function function) {
            mFunction = function;
        }

        public abstract void onReceive(Context context, Intent intent);
    }
}
