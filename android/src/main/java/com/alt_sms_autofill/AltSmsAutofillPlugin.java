package com.alt_sms_autofill;

import android.app.Activity;
import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;


/** AltSmsAutofillPlugin */
public class AltSmsAutofillPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {

  private MethodChannel channel;
  private Activity activity;
  private Result result;
  private MySMSReceiver mySMSReceiver;


  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "alt_sms_autofill");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull final Result result) {
    this.result = result;
    if (call.method.equals("listenForSms")) {
      startSmsRetriver();
    } else  if(call.method.equals("unregisterListener")){
      activity.unregisterReceiver(mySMSReceiver);
    }else{
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();

  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    activity = null;
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivity() {

  }

  private void startSmsRetriver() {
    final Task task = SmsRetriever.getClient(activity).startSmsRetriever();
    task.addOnSuccessListener(new OnSuccessListener() {
      @Override
      public void onSuccess(Object o) {
        Log.v("AltSmsAutoFillPlugin", "SmsRetriever onSuccess " + o);
      }
    });

    task.addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        Log.v("AltSmsAutoFillPlugin", "SmsRetriever onFailure " + e.getLocalizedMessage());
      }
    });

    mySMSReceiver = new MySMSReceiver();
    mySMSReceiver.bindListener(new SmsListener() {
      @Override
      public void messageReceived(String message) {
        result.success(message);
      }
    });
    activity.registerReceiver(mySMSReceiver, mySMSReceiver.doFilter());
  }
}

