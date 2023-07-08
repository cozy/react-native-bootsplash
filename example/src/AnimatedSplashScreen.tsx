import { useEffect, useState } from "react";
import { Animated, Dimensions } from "react-native";
import BootSplash from "react-native-bootsplash";

const logo = require("../assets/bootsplash_logo.png");
const manifest = require("../assets/bootsplash_manifest.json");

type Props = {
  onHide: () => void;
};

export const AnimatedBootSplash = ({ onHide }: Props) => {
  const [opacity] = useState(new Animated.Value(1));
  const [translateY] = useState(new Animated.Value(0));

  const [logoReady, setLogoReady] = useState(false);
  const [layoutReady, setLayoutReady] = useState(false);
  const ready = logoReady && layoutReady;

  const styles = BootSplash.useStyles({
    manifest,
    statusBarTranslucent: false,
    navigationBarTranslucent: false,
  });

  useEffect(() => {
    if (!ready) {
      return;
    }

    BootSplash.hide({
      fade: false,
    }).then(() => {
      Animated.stagger(250, [
        Animated.spring(translateY, {
          useNativeDriver: true,
          toValue: -50,
        }),
        Animated.spring(translateY, {
          useNativeDriver: true,
          toValue: Dimensions.get("window").height,
        }),
      ]).start();

      Animated.timing(opacity, {
        useNativeDriver: true,
        toValue: 0,
        duration: 150,
        delay: 350,
      }).start(() => onHide());
    });
  }, [ready]);

  return (
    <Animated.View
      onLayout={() => setLayoutReady(true)}
      style={[styles.container, { opacity }]}
    >
      <Animated.Image
        fadeDuration={0}
        resizeMode="contain"
        source={logo}
        onLoadEnd={() => setLogoReady(true)}
        style={[styles.logo, { transform: [{ translateY }] }]}
      />
    </Animated.View>
  );
};
