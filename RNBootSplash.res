@module("react-native-bootsplash") @scope("default")
external hide: unit => Js.Promise.t<unit> = "hide"

@module("react-native-bootsplash") @scope("default")
external visible: unit => boolean = "visible"
/*
## Usage

```rescript
 RNBootSplash.hide()->ignore
```

Or

 ```rescript
RNBootSplash.hide()->Js.Promise.then_(() => {
  Js.log("RN BootSplash: fading is over")
  Js.Promise.resolve()
}, _)->Js.Promise.catch(error => {
  Js.log(("RN BootSplash: cannot hide splash", error))
  Js.Promise.resolve()
}, _)->ignore
```
*/
