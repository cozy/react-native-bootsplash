import { NativeModules } from "react-native";

export type VisibilityStatus = "visible" | "hidden" | "transitioning";
export type Config = { fade?: boolean; bootsplashName?: string };

const NativeModule: {
  show: (fade: boolean, bootsplashName: string) => Promise<true>;
  hide: (fade: boolean, bootsplashName: string) => Promise<true>;
  getVisibilityStatus: () => Promise<VisibilityStatus>;
} = NativeModules.RNBootSplash;

export function show(config: Config = {}): Promise<void> {
  return NativeModule.show(
    { fade: false, ...config }.fade,
    config.bootsplashName ?? "global",
  ).then(() => {});
}

export function hide(config: Config = {}): Promise<void> {
  return NativeModule.hide(
    { fade: false, ...config }.fade,
    config.bootsplashName ?? "global",
  ).then(() => {});
}

export function getVisibilityStatus(): Promise<VisibilityStatus> {
  return NativeModule.getVisibilityStatus();
}

export default { show, hide, getVisibilityStatus };
