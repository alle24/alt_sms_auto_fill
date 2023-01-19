package com.alt_sms_autofill;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

public class MySMSReceiver extends BroadcastReceiver {
    private SmsListener listener;

    public void bindListener(SmsListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(SmsRetriever.SMS_RETRIEVED_ACTION == intent.getAction()){
            Bundle extras = intent.getExtras();

            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
            switch (status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    if (listener != null) {
                        listener.messageReceived((String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE));
                    }
                    break;
                case CommonStatusCodes.TIMEOUT:
                    break;
            }
        }
    }
    public IntentFilter doFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        return filter;
    }
}