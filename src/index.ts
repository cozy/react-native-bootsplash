import { NativeModules } from "react-native";

export type VisibilityStatus = "visible" | "hidden" | "transitioning";
export type Config = { fade?: boolean; bootsplashName?: string };
export type ResultStatus =
  | boolean
  | "activity_finishing"
  | "already_visible"
  | "already_hidden"
  | "shift_next";

const NativeModule: {
  show: (fade: boolean, bootsplashName: string) => Promise<ResultStatus>;
  hide: (fade: boolean, bootsplashName: string) => Promise<ResultStatus>;
  getVisibilityStatus: () => Promise<VisibilityStatus>;
} = NativeModules.RNBootSplash;

export function show(config: Config = {}): Promise<ResultStatus> {
  return NativeModule.show(
    { fade: false, ...config }.fade,
    config.bootsplashName ?? "global",
  ).then((result) => result);
}

export function hide(config: Config = {}): Promise<ResultStatus> {
  return NativeModule.hide(
    { fade: false, ...config }.fade,
    config.bootsplashName ?? "global",
  ).then((result) => result);
}

export function getVisibilityStatus(): Promise<VisibilityStatus> {
  return NativeModule.getVisibilityStatus();
}

export default { show, hide, getVisibilityStatus };
