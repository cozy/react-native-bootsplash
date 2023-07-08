package com.zoontek.rnbootsplash;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name = RNBootSplashModuleImpl.NAME)
public class RNBootSplashModule extends NativeRNBootSplashSpec {

  public RNBootSplashModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  @NonNull
  public String getName() {
    return RNBootSplashModuleImpl.NAME;
  }

  @Override
  public double getStatusBarHeight() {
    return RNBootSplashModuleImpl.getStatusBarHeight(getReactApplicationContext());
  }

  @Override
  public double getNavigationBarHeight() {
    return RNBootSplashModuleImpl.getNavigationBarHeight(getReactApplicationContext());
  }

  @Override
  public void hide(Promise promise) {
    RNBootSplashModuleImpl.hide(getReactApplicationContext(), promise);
  }

  @Override
  public boolean isVisible() {
    return RNBootSplashModuleImpl.isVisible();
  }
}
