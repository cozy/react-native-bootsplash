import NativeModule from "./NativeRNBootSplash";

export type Config = { fade?: boolean };

export function hide(config: Config = {}): Promise<void> {
  const { fade = false } = config;
  return NativeModule.hide(fade).then(() => {});
}

export function isVisible(): Promise<boolean> {
  return NativeModule.isVisible();
}

export default { hide, isVisible };
