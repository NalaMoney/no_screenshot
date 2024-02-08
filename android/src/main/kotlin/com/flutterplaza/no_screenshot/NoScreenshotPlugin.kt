package com.flutterplaza.no_screenshot

import android.app.Activity
import android.app.Application
import android.os.Bundle

import android.content.Context
import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import android.view.WindowManager.LayoutParams;
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding


/** NoScreenshotPlugin */
class NoScreenshotPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    private lateinit var activity: Activity
    private var flags: Int = 0

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel =
            MethodChannel(flutterPluginBinding.binaryMessenger, "com.flutterplaza.no_screenshot")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext

        val app: Application? = context as? Application
        if (app != null) {
            // Successfully casted to Application
            app.registerActivityLifecycleCallbacks(SecureActivityLifecycleCallbacks())
        }
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) =
        if (call.method == "screenshotOff") {
            screenshotOff();
            result.success(true);
        } else if (call.method == "screenshotOn") {
            screenshotOn();
            result.success(true);
        } else if (call.method == "toggleScreenshot") {
            flags = activity.window.attributes.flags;
            if ((flags and LayoutParams.FLAG_SECURE) != 0) {
                screenshotOn();
            } else {
                screenshotOff();
            }
            result.success(true);
        } else {
            result.notImplemented()
        }

    private fun screenshotOff() {
        flags = LayoutParams.FLAG_SECURE;
        activity.window.addFlags(flags);
    }

    private fun screenshotOn() {
        activity.window.clearFlags(flags);
        flags = 0
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity;
    }

    override fun onDetachedFromActivityForConfigChanges() {}

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity;
    }

    override fun onDetachedFromActivity() {}

    inner class SecureActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            if ((flags and LayoutParams.FLAG_SECURE) != 0) {
                activity.window.addFlags(LayoutParams.FLAG_SECURE)
            }
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityResumed(activity: Activity) {
        }

        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityDestroyed(activity: Activity) {
        }
    }
}
   