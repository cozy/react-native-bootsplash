package com.zoontek.rnbootsplash;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name = RNBootSplashModuleImpl.NAME)
public class RNBootSplashModule extends ReactContextBaseJavaModule {

  public RNBootSplashModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @NonNull
  @Override
  public String getName() {
    return RNBootSplashModuleImpl.NAME;
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public double getStatusBarHeight() {
    return RNBootSplashModuleImpl.getStatusBarHeight(getReactApplicationContext());
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public double getNavigationBarHeight() {
    return RNBootSplashModuleImpl.getNavigationBarHeight(getReactApplicationContext());
  }

  @ReactMethod
  public void hide(final Promise promise) {
    RNBootSplashModuleImpl.hide(getReactApplicationContext(), promise);
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public boolean isVisible() {
    return RNBootSplashModuleImpl.isVisible();
  }
}
