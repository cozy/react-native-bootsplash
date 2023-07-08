type config = {fade: bool}

@module("react-native-bootsplash") @scope("default")
external hide: config => Js.Promise.t<unit> = "hide"

@module("react-native-bootsplash") @scope("default")
external isVisible: unit => Js.Promise.t<boolean> = "isVisible"

/*
## Usage

```rescript
 RNBootSplash.hide()->ignore
```

Or

 ```rescript
RNBootSplash.hide({fade: true})->Js.Promise.then_(() => {
  Js.log("RN BootSplash: fading is over")
  Js.Promise.resolve()
}, _)->Js.Promise.catch(error => {
  Js.log(("RN BootSplash: cannot hide splash", error))
  Js.Promise.resolve()
}, _)->ignore
```
*/
