# Migration from v4

## What's new

- The removal of [AndroidX splashscreen library](https://developer.android.com/jetpack/androidx/releases/core#core-splashscreen-1.0.0) requirement
- The drop of react-native < 0.70, iOS < 12.4
- `getVisibilityStatus` now returns `Promise<"hidden" | "visible">` (`transitioning` status has been removed)

## Code modifications

For `android/app/build.gradle`:

```diff
// …

dependencies {
  // The version of react-native is set by the React Native Gradle Plugin
  implementation("com.facebook.react:react-android")
- implementation("androidx.core:core-splashscreen:1.0.0")
```
