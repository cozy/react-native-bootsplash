# 🚀 react-native-bootsplash

Show a splash screen during app startup. Hide it when you are ready.<br>
**For migration from the v3, check the [`MIGRATION.md` guide](https://github.com/zoontek/react-native-bootsplash/blob/master/MIGRATION.md).**

[![mit licence](https://img.shields.io/dub/l/vibe-d.svg?style=for-the-badge)](https://github.com/zoontek/react-native-bootsplash/blob/main/LICENSE)
[![npm version](https://img.shields.io/npm/v/react-native-bootsplash?style=for-the-badge)](https://www.npmjs.org/package/react-native-bootsplash)
[![npm downloads](https://img.shields.io/npm/dt/react-native-bootsplash.svg?label=downloads&style=for-the-badge)](https://www.npmjs.org/package/react-native-bootsplash)
<br />
[![platform - android](https://img.shields.io/badge/platform-Android-3ddc84.svg?logo=android&style=for-the-badge)](https://www.android.com)
[![platform - ios](https://img.shields.io/badge/platform-iOS-000.svg?logo=apple&style=for-the-badge)](https://developer.apple.com/ios)

<p>
  <img height="520" width="256" src="https://raw.githubusercontent.com/zoontek/react-native-bootsplash/HEAD/docs/ios_demo.gif?raw=true" alt="iOS demo"></img>
  <img height="500" width="259" src="https://raw.githubusercontent.com/zoontek/react-native-bootsplash/HEAD/docs/android_demo.gif?raw=true" alt="android demo"></img>
</p>

## Support

This library follows the React Native [releases support policy](https://github.com/reactwg/react-native-releases#releases-support-policy).<br>
It is supporting the **latest version**, and the **two previous minor series**.

## Installation

```bash
$ npm install --save react-native-bootsplash
# --- or ---
$ yarn add react-native-bootsplash
```

_⚠️  Don't forget going into the `ios` directory to execute a `pod install`._

## 🆘 Manual linking

Because this package targets recent React Native versions, you probably don't need to link it manually. But if you have a special case, follow these additional instructions:

<details>
  <summary><b>👀 See manual linking instructions</b></summary>

### iOS

Add this line to your `ios/Podfile` file, then run `pod install`.

```bash
target 'YourAwesomeProject' do
  # …
  pod 'RNBootSplash', :path => '../node_modules/react-native-bootsplash'
end
```

### Android

1. Add the following lines to `android/settings.gradle`:

```gradle
include ':react-native-bootsplash'
project(':react-native-bootsplash').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-bootsplash/android')
```

2. Add the implementation line to the dependencies in `android/app/build.gradle`:

```gradle
dependencies {
  // ...
  implementation project(':react-native-bootsplash')
}
```

3. Add the import and link the package in `MainApplication.java`:

```java
import com.zoontek.rnbootsplash.RNBootSplashPackage; // ⬅️ add the RNBootSplashPackage import

public class MainApplication extends Application implements ReactApplication {

  // …

  @Override
  protected List<ReactPackage> getPackages() {
    @SuppressWarnings("UnnecessaryLocalVariable")
    List<ReactPackage> packages = new PackageList(this).getPackages();
    // …
    packages.add(new RNBootSplashPackage());
    return packages;
  }

  // …
}
```

</details>

## Setup

### Assets generation

In order to speed up the setup, we provide a **CLI** to generate assets, create the Android Drawable XML file and the iOS Storyboard file automatically ✨.

```bash
$ npx react-native generate-bootsplash --help
# --- or ---
$ yarn react-native generate-bootsplash --help
```

The command can take multiple arguments:

```bash
yarn react-native generate-bootsplash <logoPath>

Generate a launch screen using an original logo file (PNG or SVG)

Options:
  --background-color <color>  color used as launch screen background (in hexadecimal format) (default: "#fff")
  --logo-width <width>        logo width at @1x (in dp - we recommend approximately ~100) (default: 100)
  --assets-path [path]        path to your static assets directory (useful to require the logo file in JS)
  --flavor <flavor>           [android only] flavor build variant (outputs in an android resource directory other than "main")
  --platforms <platforms>     platforms to generate assets for (comma separated) (default: "android,ios")
  -h, --help                  output usage information
```

#### Full command usage example

```bash
yarn react-native generate-bootsplash assets/bootsplash_logo_original.png \
  --background-color=F5FCFF \
  --logo-width=100 \
  --assets-path=assets \
  --flavor=main \
  --platforms=android,ios
```

![](https://raw.githubusercontent.com/zoontek/react-native-bootsplash/master/docs/cli_tool.png?raw=true)

This tool relies on the naming conventions that are used in the `/example` project and will therefore create the following files:

```bash
# Only if --assets-path was specified
assets/bootsplash_logo.png
assets/bootsplash_logo@1,5x.png
assets/bootsplash_logo@2x.png
assets/bootsplash_logo@3x.png
assets/bootsplash_logo@4x.png

android/app/src/main/res/values/colors.xml (creation and edition)
android/app/src/main/res/mipmap-hdpi/bootsplash_logo.png
android/app/src/main/res/mipmap-mdpi/bootsplash_logo.png
android/app/src/main/res/mipmap-xhdpi/bootsplash_logo.png
android/app/src/main/res/mipmap-xxhdpi/bootsplash_logo.png
android/app/src/main/res/mipmap-xxxhdpi/bootsplash_logo.png

ios/YourProjectName/BootSplash.storyboard
ios/YourProjectName/Images.xcassets/BootSplashLogo.imageset/bootsplash_logo.png
ios/YourProjectName/Images.xcassets/BootSplashLogo.imageset/bootsplash_logo@2x.png
ios/YourProjectName/Images.xcassets/BootSplashLogo.imageset/bootsplash_logo@3x.png
```

### iOS

_ℹ️ For `react-native` < `0.71` setup, follow the [`v4.4.0 README.md`](https://github.com/zoontek/react-native-bootsplash/blob/4.4.0/README.md)._

---

Edit the `ios/YourProjectName/AppDelegate.mm` file:

```obj-c
#import "AppDelegate.h"
#import "RNBootSplash.h" // ⬅️ add the header import

// …

@implementation AppDelegate

// …

// ⬇️ Add this before file @end
- (UIView *)createRootViewWithBridge:(RCTBridge *)bridge
                          moduleName:(NSString *)moduleName
                           initProps:(NSDictionary *)initProps {
  UIView *rootView = [super createRootViewWithBridge:bridge
                                          moduleName:moduleName
                                           initProps:initProps];

  [RNBootSplash initWithStoryboard:@"BootSplash" rootView:rootView]; // ⬅️ initialize the splash screen

  return rootView;
}

@end
```

Set the `BootSplash.storyboard` as launch screen file:

| Drag and drop the file                                                                                  | Create folder reference                                                                                 | Set as Launch Screen File                                                                               |
| ------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------- |
| ![](https://raw.githubusercontent.com/zoontek/react-native-bootsplash/master/docs/xcode-1.png?raw=true) | ![](https://raw.githubusercontent.com/zoontek/react-native-bootsplash/master/docs/xcode-2.png?raw=true) | ![](https://raw.githubusercontent.com/zoontek/react-native-bootsplash/master/docs/xcode-3.png?raw=true) |

### Android

1. Edit your `android/app/src/main/res/values/styles.xml` file:

```xml
<resources>

  <style name="AppTheme" parent="Theme.AppCompat.DayNight.NoActionBar">
      <!-- Your base theme customization -->
  </style>

  <!-- BootTheme should inherit from Theme.SplashScreen -->
  <style name="BootTheme" parent="Theme.SplashScreen">
    <item name="windowSplashScreenBackground">@color/bootsplash_background</item>
    <item name="windowSplashScreenAnimatedIcon">@mipmap/bootsplash_logo</item>
    <item name="postSplashScreenTheme">@style/AppTheme</item>
  </style>

</resources>
```

2. Edit your `android/app/src/main/AndroidManifest.xml` file:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
  package="com.rnbootsplashexample">

  <!-- … -->

  <application
    android:name=".MainApplication"
    android:label="@string/app_name"
    android:icon="@mipmap/ic_launcher"
    android:roundIcon="@mipmap/ic_launcher_round"
    android:allowBackup="false"
    android:theme="@style/BootTheme"> <!-- Replace @style/AppTheme with @style/BootTheme -->
    <!-- … -->
  </application>
</manifest>

```

3. Finally edit your `android/app/src/main/java/com/yourprojectname/MainActivity.java` file:

```java
// …

// add these required imports:
import android.os.Bundle;
import com.zoontek.rnbootsplash.RNBootSplash;

public class MainActivity extends ReactActivity {

  // …

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    BootSplash.init(this,R.style.BootTheme); // ⬅️ initialize the splash screen
    super.onCreate(savedInstanceState); // or super.onCreate(null) with react-native-screens
  }
}
```

## API

### hide()

#### Method type

```ts
type hide = () => Promise<void>;
```

#### Usage

```js
import BootSplash from "react-native-bootsplash";

BootSplash.hide();
```

### isVisible()

#### Method type

```ts
type isVisible = () => boolean;
```

#### Usage

```js
import BootSplash from "react-native-bootsplash";

console.log(BootSplash.isVisible());
```

## Real world example

```js
import React, { useEffect } from "react";
import { Text } from "react-native";
import BootSplash from "react-native-bootsplash";

function App() {
  useEffect(() => {
    const init = async () => {
      // …do multiple sync or async tasks
    };

    init().finally(async () => {
      await BootSplash.hide();
      console.log("BootSplash has been hidden successfully");
    });
  }, []);

  return <Text>My awesome app</Text>;
}
```

**🤙 A more complex example is available in the [`/example` folder](example).**

## With React Navigation

If you are using React Navigation, you can hide the splash screen once the navigation container and all children have finished mounting by using the `onReady` function.

```js
import React from "react";
import { NavigationContainer } from "@react-navigation/native";
import BootSplash from "react-native-bootsplash";

function App() {
  return (
    <NavigationContainer onReady={() => BootSplash.hide()}>
      {/* content */}
    </NavigationContainer>
  );
}
```

## With react-native-bars

In order to keep fully transparent status and navigation bars on Android once the splash screen is hidden (and control them), this library play nicely with [react-native-bars](https://github.com/zoontek/react-native-bars). Check its [README](https://github.com/zoontek/react-native-bars/blob/main/README.md#with-react-native-bootsplash) for more informations.

## Testing with Jest

Testing code which uses this library requires some setup since we need to mock the native methods.

To add the mocks, create a file _jest/setup.js_ (or any other file name) containing the following code:

```js
jest.mock("react-native-bootsplash", () => {
  return {
    hide: jest.fn().mockResolvedValueOnce(),
    visible: jest.fn().mockReturnValue(false),
  };
});
```

After that, we need to add the setup file in the jest config. You can add it under [setupFiles](https://jestjs.io/docs/en/configuration.html#setupfiles-array) option in your jest config file:

```json
{
  "setupFiles": ["<rootDir>/jest/setup.js"]
}
```

## Sponsors

This module is provided **as is**, I work on it in my free time.

If you or your company uses it in a production app, consider sponsoring this project 💰. You also can contact me for **premium** enterprise support: help with issues, prioritize bugfixes, feature requests, etc.

<a href="https://github.com/sponsors/zoontek"><img align="center" alt="Sponsors list" src="https://raw.githubusercontent.com/zoontek/sponsors/main/sponsorkit/sponsors.svg"></a>
