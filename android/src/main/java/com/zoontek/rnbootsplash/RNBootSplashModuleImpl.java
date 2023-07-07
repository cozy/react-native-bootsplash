package com.zoontek.rnbootsplash;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.window.SplashScreen;
import android.window.SplashScreenView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.common.ReactConstants;

import java.util.Timer;
import java.util.TimerTask;

public class RNBootSplashModuleImpl {

  public static final String NAME = "RNBootSplash";

  private static final RNBootSplashQueue<Promise> mPromiseQueue = new RNBootSplashQueue<>();
  private static boolean mShouldKeepOnScreen = true;

  @Nullable
  private static RNBootSplashDialog mDialog = null;

  protected static void init(@Nullable final Activity activity, @StyleRes int themeResId) {
    if (activity == null) {
      FLog.w(ReactConstants.TAG, NAME + ": Ignored initialization, current activity is null.");
      return;
    }

    // Apply postSplashScreenTheme
    TypedValue typedValue = new TypedValue();
    Resources.Theme currentTheme = activity.getTheme();

    if (currentTheme
      .resolveAttribute(R.attr.postSplashScreenTheme, typedValue, true)) {
      int finalThemeId = typedValue.resourceId;

      if (finalThemeId != 0) {
        activity.setTheme(finalThemeId);
      }
    }

    // Keep the splash screen on-screen until Dialog is shown
    final View contentView = activity.findViewById(android.R.id.content);

    contentView
      .getViewTreeObserver()
      .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
      @Override
      public boolean onPreDraw() {
        if (mShouldKeepOnScreen) {
          return false;
        }

        contentView
          .getViewTreeObserver()
          .removeOnPreDrawListener(this);

        return true;
      }
    });

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      // This is not called on Android 12 when activity is started using intent
      // (Android studio / CLI / notification / widget…)
      activity
        .getSplashScreen()
        .setOnExitAnimationListener(new SplashScreen.OnExitAnimationListener() {
          @Override
          public void onSplashScreenExit(@NonNull SplashScreenView view) {
            view.remove(); // Remove it immediately, without animation
          }
        });
    }

    mDialog = new RNBootSplashDialog(activity, themeResId);

    mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
      @Override
      public void onShow(DialogInterface dialog) {
        mShouldKeepOnScreen = false;
      }
    });

    UiThreadUtil.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        mDialog.show();
      }
    });
  }

  private static void clearPromiseQueue() {
    while (!mPromiseQueue.isEmpty()) {
      Promise promise = mPromiseQueue.shift();

      if (promise != null)
        promise.resolve(true);
    }
  }

  private static void hideAndClearPromiseQueue(final ReactApplicationContext reactContext) {
    UiThreadUtil.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        final Activity activity = reactContext.getCurrentActivity();

        if (mShouldKeepOnScreen ||  activity == null || activity.isFinishing()) {
          final Timer timer = new Timer();

          timer.schedule(new TimerTask() {
            @Override
            public void run() {
              timer.cancel();
              hideAndClearPromiseQueue(reactContext);
            }
          }, 100);
        } else if (mDialog == null) {
          clearPromiseQueue();
        } else {
          mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
              mDialog = null;
              clearPromiseQueue();
            }
          });

          mDialog.dismiss();
        }
      }
    });
  }

  public static void hide(final ReactApplicationContext reactContext,
                          final Promise promise) {
    mPromiseQueue.push(promise);
    hideAndClearPromiseQueue(reactContext);
  }

  public static boolean isVisible() {
    return mShouldKeepOnScreen || mDialog != null;
  }
}
