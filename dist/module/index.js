import { NativeModules } from "react-native";
const NativeModule = NativeModules.RNBootSplash;
export function show(config = {}) {
  var _config$bootsplashNam;

  return NativeModule.show({
    fade: false,
    ...config
  }.fade, (_config$bootsplashNam = config.bootsplashName) !== null && _config$bootsplashNam !== void 0 ? _config$bootsplashNam : "global").then(result => result);
}
export function hide(config = {}) {
  var _config$bootsplashNam2;

  return NativeModule.hide({
    fade: false,
    ...config
  }.fade, (_config$bootsplashNam2 = config.bootsplashName) !== null && _config$bootsplashNam2 !== void 0 ? _config$bootsplashNam2 : "global").then(result => result);
}
export function getVisibilityStatus() {
  return NativeModule.getVisibilityStatus();
}
export default {
  show,
  hide,
  getVisibilityStatus
};
//# sourceMappingURL=index.js.map