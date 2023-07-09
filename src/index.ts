import { useCallback, useEffect, useMemo, useRef } from "react";
import {
  ImageProps,
  ImageRequireSource,
  Platform,
  StyleSheet,
  ViewProps,
  ViewStyle,
} from "react-native";
import NativeModule from "./NativeRNBootSplash";

export type HideConfig = {
  fade?: boolean;
};

export type Manifest = {
  logo: {
    height: number;
    width: number;
  };
  // branding?: {
  //   height: number;
  //   width: number;
  // };
  light: { backgroundColor: string };
  dark?: { backgroundColor: string };
};

export type UseHideAnimationConfig = {
  manifest: Manifest;
  logo: ImageRequireSource;
  // branding?: ImageRequireSource;

  animate: () => void;

  statusBarTranslucent?: boolean;
  navigationBarTranslucent?: boolean;
};

export type UseHideAnimation = {
  container: ViewProps;
  logo: ImageProps;
  // branding?: ImageProps;
};

export function hide(config: HideConfig = {}): Promise<void> {
  const { fade = false } = config;
  return NativeModule.hide(fade).then(() => {});
}

export function isVisible(): Promise<boolean> {
  return NativeModule.isVisible();
}

export function useHideAnimation(config: UseHideAnimationConfig) {
  const {
    manifest,
    logo: logoSrc,
    // branding: brandingSrc,

    animate,

    statusBarTranslucent = false,
    navigationBarTranslucent = false,
  } = config;

  const animateFn = useRef(animate);
  const layoutReady = useRef(false);
  const logoReady = useRef(false);
  const animateHasBeenCalled = useRef(false);

  useEffect(() => {
    animateFn.current = animate;
  });

  const maybeRunAnimate = useCallback(() => {
    if (
      layoutReady.current &&
      logoReady.current &&
      !animateHasBeenCalled.current
    ) {
      animateHasBeenCalled.current = true;

      hide({ fade: false })
        .then(() => animateFn.current())
        .catch(() => {});
    }
  }, []);

  const { height: logoHeight, width: logoWidth } = manifest.logo;
  const { backgroundColor } = manifest.light;

  return useMemo<UseHideAnimation>(() => {
    const containerStyle: ViewStyle = {
      ...StyleSheet.absoluteFillObject,
      alignItems: "center",
      flex: 1,
      justifyContent: "center",
      backgroundColor,
    };

    const container: ViewProps = {
      onLayout: () => {
        layoutReady.current = true;
        maybeRunAnimate();
      },
      style: containerStyle,
    };

    const logo: ImageProps = {
      fadeDuration: 0,
      resizeMode: "contain",
      source: logoSrc,
      onLoadEnd: () => {
        logoReady.current = true;
        maybeRunAnimate();
      },
      style: {
        height: logoHeight,
        width: logoWidth,
      },
    };

    if (Platform.OS !== "android") {
      return { container, logo };
    }

    const { statusBarHeight, navigationBarHeight } =
      NativeModule.getConstants();

    return {
      container: {
        ...container,
        style: {
          ...containerStyle,
          marginTop: statusBarTranslucent ? undefined : -statusBarHeight,
          marginBottom: navigationBarTranslucent
            ? undefined
            : -navigationBarHeight,
        },
      },
      logo,
    };
  }, [
    maybeRunAnimate,
    logoSrc,
    logoHeight,
    logoWidth,
    backgroundColor,
    statusBarTranslucent,
    navigationBarTranslucent,
  ]);
}

export default {
  hide,
  isVisible,
  useHideAnimation,
};
