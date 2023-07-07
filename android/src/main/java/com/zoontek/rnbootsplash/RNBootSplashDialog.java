package com.zoontek.rnbootsplash;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

public class RNBootSplashDialog extends Dialog {

  public RNBootSplashDialog(@NonNull Context context, @StyleRes int themeResId) {
    super(context, themeResId);
    setCancelable(false);
    setCanceledOnTouchOutside(false);
  }

  @Override
  public void onBackPressed() {
    // Prevent default behavior
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    final Window window = this.getWindow();

    if (window != null) {
      window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    super.onCreate(savedInstanceState);
  }
}
