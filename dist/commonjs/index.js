"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.show = show;
exports.hide = hide;
exports.getVisibilityStatus = getVisibilityStatus;
exports.default = void 0;

var _reactNative = require("react-native");

const NativeModule = _reactNative.NativeModules.RNBootSplash;

function show(config = {}) {
  var _config$bootsplashNam;

  return NativeModule.show({
    fade: false,
    ...config
  }.fade, (_config$bootsplashNam = config.bootsplashName) !== null && _config$bootsplashNam !== void 0 ? _config$bootsplashNam : "global").then(result => result);
}

function hide(config = {}) {
  var _config$bootsplashNam2;

  return NativeModule.hide({
    fade: false,
    ...config
  }.fade, (_config$bootsplashNam2 = config.bootsplashName) !== null && _config$bootsplashNam2 !== void 0 ? _config$bootsplashNam2 : "global").then(result => result);
}

function getVisibilityStatus() {
  return NativeModule.getVisibilityStatus();
}

var _default = {
  show,
  hide,
  getVisibilityStatus
};
exports.default = _default;
//# sourceMappingURL=index.js.map