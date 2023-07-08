import type { TurboModule } from "react-native";
import { TurboModuleRegistry } from "react-native";

export interface Spec extends TurboModule {
  getStatusBarHeight(): number;
  getNavigationBarHeight(): number;
  hide(): Promise<void>;
  isVisible(): boolean;
}

export default TurboModuleRegistry.getEnforcing<Spec>("RNBootSplash");
