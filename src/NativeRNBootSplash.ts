import type { TurboModule } from "react-native";
import { TurboModuleRegistry } from "react-native";

export interface Spec extends TurboModule {
  hide(): Promise<void>;
  isVisible(): boolean;
}

export default TurboModuleRegistry.getEnforcing<Spec>("RNBootSplash");
