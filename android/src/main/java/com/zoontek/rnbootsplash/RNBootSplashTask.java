package com.zoontek.rnbootsplash;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;

public class RNBootSplashTask {

  public enum Type {
    SHOW,
    HIDE
  }

  private final boolean mFade;
  private final String mBootsplashName;
  @NonNull private final Promise mPromise;
  @NonNull private final Type mType;

  public RNBootSplashTask(@NonNull Type type,
                          boolean fade,
                          String bootsplashName,
                          @NonNull Promise promise) {
    mType = type;
    mFade = fade;
    mBootsplashName = bootsplashName;
    mPromise = promise;
  }

  public boolean getFade() {
    return mFade;
  }

  public String getBootsplashName() {
    return mBootsplashName;
  }

  @NonNull
  public Type getType() {
    return mType;
  }

  @NonNull
  public Promise getPromise() {
    return mPromise;
  }
}
