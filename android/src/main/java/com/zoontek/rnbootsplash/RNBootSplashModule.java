package com.zoontek.rnbootsplash;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.PromiseImpl;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

@ReactModule(name = RNBootSplashModule.MODULE_NAME)
public class RNBootSplashModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

  public static final String MODULE_NAME = "RNBootSplash";
  private static final int ANIMATION_DURATION = 220;
  private static final String TAG = "🌊 RNBootSplash";

  private enum Status {
    VISIBLE,
    HIDDEN,
    TRANSITIONING_TO_VISIBLE,
    TRANSITIONING_TO_HIDDEN
  }

  private static int mDrawableResId = -1;
  private static final ArrayList<RNBootSplashTask> mTaskQueue = new ArrayList<>();
  private static final ArrayList<String> mBootsplashNames = new ArrayList<>();
  private static Status mStatus = Status.HIDDEN;
  private static boolean mIsAppInBackground = false;

  static {
    mBootsplashNames.add("global");
  }

  public RNBootSplashModule(ReactApplicationContext reactContext) {
    super(reactContext);
    reactContext.addLifecycleEventListener(this);
  }

  @Override
  public String getName() {
    return MODULE_NAME;
  }

  private static LinearLayout getLayout(@NonNull Activity activity, LayoutParams params) {
    LinearLayout layout = new LinearLayout(activity);
    View view = new View(activity);

    view.setBackgroundResource(mDrawableResId);
    layout.setId(R.id.bootsplash_layout_id);
    layout.setLayoutTransition(null);
    layout.setOrientation(LinearLayout.VERTICAL);
    layout.addView(view, params);

    return layout;
  }

  protected static void init(final @DrawableRes int drawableResId, final Activity activity) {
    UiThreadUtil.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (activity == null
          || activity.isFinishing()
          || activity.findViewById(R.id.bootsplash_layout_id) != null) {
          return;
        }

        mDrawableResId = drawableResId;
        mStatus = Status.VISIBLE;

        LayoutParams params = new LayoutParams(
          LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        activity.addContentView(getLayout(activity, params), params);
      }
    });
  }

  @Override
  public void onHostDestroy() {
    mIsAppInBackground = true;
  }

  @Override
  public void onHostPause() {
    Promise promise = new PromiseImpl(null, null);
    mTaskQueue.add(new RNBootSplashTask(RNBootSplashTask.Type.SHOW, false, "secure_background", promise));
    shiftNextTask();
  }

  @Override
  public void onHostResume() {
    mIsAppInBackground = false;
    shiftNextTask();
  }

  private void shiftNextTask() {
    boolean shouldSkipTick = mDrawableResId == -1
      || mStatus == Status.TRANSITIONING_TO_VISIBLE
      || mStatus == Status.TRANSITIONING_TO_HIDDEN
      || mIsAppInBackground
      || mTaskQueue.isEmpty();

    if (shouldSkipTick) return;

    RNBootSplashTask task = mTaskQueue.remove(0);

    switch (task.getType()) {
      case SHOW:
        show(task);
        break;
      case HIDE:
        hide(task);
        break;
    }
  }

  private void waitAndShiftNextTask() {
    final Timer timer = new Timer();

    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        shiftNextTask();
        timer.cancel();
      }
    }, 250);
  }

  private void show(final RNBootSplashTask task) {
    UiThreadUtil.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Log.d(TAG, "run show " + task.getBootsplashName());
        addBootsplashName(task.getBootsplashName());
        final Activity activity = getReactApplicationContext().getCurrentActivity();
        final Promise promise = task.getPromise();

        if (activity == null || activity.isFinishing()) {
          promise.resolve("activity_finishing");
          waitAndShiftNextTask();
          return;
        }

        if (activity.findViewById(R.id.bootsplash_layout_id) != null) {
          promise.resolve("already_visible"); // splash screen is already visible
          shiftNextTask();
          return;
        }

        mStatus = Status.TRANSITIONING_TO_VISIBLE;

        LayoutParams params = new LayoutParams(
          LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        LinearLayout layout = getLayout(activity, params);

        if (task.getFade()) {
          layout.setAlpha(0.0f);
          activity.addContentView(layout, params);

          layout
            .animate()
            .setDuration(ANIMATION_DURATION)
            .alpha(1.0f)
            .setInterpolator(new DecelerateInterpolator())
            .setListener(new AnimatorListenerAdapter() {
              @Override
              public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mStatus = Status.VISIBLE;
                promise.resolve(true);
                shiftNextTask();
              }
            })
            .start();
        } else {
          activity.addContentView(layout, params);
          mStatus = Status.VISIBLE;
          promise.resolve(true);
          shiftNextTask();
        }
      }
    });
  }

  private void hide(final RNBootSplashTask task) {
    UiThreadUtil.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Log.d(TAG, "run hide " + task.getBootsplashName());
        removeBootsplashName(task.getBootsplashName());

        final Promise promise = task.getPromise();

        if (hasBootsplashToDisplay()) {
          promise.resolve("shift_next");
          waitAndShiftNextTask(); // splash screen should still be displayed for some BootsplashNames
          return;
        }

        final Activity activity = getReactApplicationContext().getCurrentActivity();

        if (activity == null || activity.isFinishing()) {
          promise.resolve("activity_finishing");
          waitAndShiftNextTask();
          return;
        }

        final LinearLayout layout = activity.findViewById(R.id.bootsplash_layout_id);

        if (layout == null) {
          promise.resolve("already_hidden"); // splash screen is already hidden
          shiftNextTask();
          return;
        }

        mStatus = Status.TRANSITIONING_TO_HIDDEN;

        final ViewGroup parent = (ViewGroup) layout.getParent();

        if (task.getFade()) {
          layout
            .animate()
            .setDuration(ANIMATION_DURATION)
            .alpha(0.0f)
            .setInterpolator(new AccelerateInterpolator())
            .setListener(new AnimatorListenerAdapter() {
              @Override
              public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                if (parent != null)
                  parent.removeView(layout);

                mStatus = Status.HIDDEN;
                promise.resolve(true);
                shiftNextTask();
              }
            }).start();
        } else {
          parent.removeView(layout);
          mStatus = Status.HIDDEN;
          promise.resolve(true);
          shiftNextTask();
        }
      }
    });
  }

  @ReactMethod
  public void show(final boolean fade, final String bootsplashName, final Promise promise) {
    if (mDrawableResId == -1) {
      promise.reject("uninitialized_module", "react-native-bootsplash has not been initialized");
    } else {
      Log.d(TAG, "show " + bootsplashName);
      mTaskQueue.add(new RNBootSplashTask(RNBootSplashTask.Type.SHOW, fade, bootsplashName, promise));
      shiftNextTask();
    }
  }

  @ReactMethod
  public void hide(final boolean fade, final String bootsplashName, final Promise promise) {
    if (mDrawableResId == -1) {
      promise.reject("uninitialized_module", "react-native-bootsplash has not been initialized");
    } else {
      Log.d(TAG, "hide " + bootsplashName);
      mTaskQueue.add(new RNBootSplashTask(RNBootSplashTask.Type.HIDE, fade, bootsplashName, promise));
      shiftNextTask();
    }
  }

  @ReactMethod
  public void getVisibilityStatus(final Promise promise) {
    switch (mStatus) {
      case VISIBLE:
        promise.resolve("visible");
        break;
      case HIDDEN:
        promise.resolve("hidden");
        break;
      case TRANSITIONING_TO_VISIBLE:
      case TRANSITIONING_TO_HIDDEN:
        promise.resolve("transitioning");
        break;
    }
  }

  protected void addBootsplashName(String name) {
    if (!mBootsplashNames.contains(name)) {
      mBootsplashNames.add(name);
    }
    Log.d(TAG, "mBootsplashNames = " + mBootsplashNames.toString());
  }

  protected void removeBootsplashName(String name) {
    if (mBootsplashNames.contains(name)) {
      mBootsplashNames.remove(name);
    }
    Log.d(TAG, "mBootsplashNames = " + mBootsplashNames.toString());
  }

  protected boolean hasBootsplashToDisplay() {
    return mBootsplashNames.size() != 0;
  }
}
