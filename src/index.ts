import NativeModule from "./NativeRNBootSplash";

export function hide(): Promise<void> {
  return NativeModule.hide().then(() => {});
}
export function isVisible(): boolean {
  return NativeModule.isVisible();
}

export default { hide, isVisible };
