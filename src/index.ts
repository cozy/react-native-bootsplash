import { useMemo } from "react";
import { ImageStyle, Platform, StyleSheet, ViewStyle } from "react-native";
import NativeModule from "./NativeRNBootSplash";

export type HideConfig = {
  fade?: boolean;
};

export type Styles = {
  container: ViewStyle;
  logo: ImageStyle;
};

export type Manifest = {
  logoHeight: number;
  logoWidth: number;
  backgroundColor: string;
};

export type UseStylesConfig = {
  manifest: Manifest;
  statusBarTranslucent?: boolean;
  navigationBarTranslucent?: boolean;
};

export function hide(config: HideConfig = {}): Promise<void> {
  const { fade = false } = config;
  return NativeModule.hide(fade).then(() => {});
}

export function isVisible(): Promise<boolean> {
  return NativeModule.isVisible();
}

export function useStyles(config: UseStylesConfig) {
  const {
    manifest: { logoHeight, logoWidth, backgroundColor },
    statusBarTranslucent = false,
    navigationBarTranslucent = false,
  } = config;

  return useMemo<Styles>(() => {
    const container: ViewStyle = {
      ...StyleSheet.absoluteFillObject,
      flex: 1,
      backgroundColor,
      alignItems: "center",
      justifyContent: "center",
    };

    const logo: ImageStyle = {
      height: logoHeight,
      width: logoWidth,
    };

    if (Platform.OS !== "android") {
      return { container, logo };
    }

    const { statusBarHeight, navigationBarHeight } =
      NativeModule.getConstants();

    return {
      container: {
        ...container,
        marginTop: statusBarTranslucent ? 0 : -statusBarHeight,
        marginBottom: navigationBarTranslucent ? 0 : -navigationBarHeight,
      },
      logo,
    };
  }, [
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
  useStyles,
};
