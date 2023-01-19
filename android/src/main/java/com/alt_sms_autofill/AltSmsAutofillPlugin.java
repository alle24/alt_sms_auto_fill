package com.alt_sms_autofill;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.lang.ref.WeakReference;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;


/** AltSmsAutofillPlugin */
public class AltSmsAutofillPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.RequestPermissionsResultListener{

  private MethodChannel channel;
  private Activity activity;
  private int myPermissionCode = 1;
  private boolean permissionGranted = false;
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

      }
    });

    task.addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {

      }
    });

    mySMSReceiver = new MySMSReceiver();
    mySMSReceiver.bindListener(new SmsListener() {
      @Override
      public void messageReceived(String message) {
        result.success(messages);
      }
    });
    activity.registerReceiver(mySMSReceiver, mySMSReceiver.doFilter());
  }
}

