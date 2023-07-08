import { useEffect, useState } from "react";
import { Animated, Dimensions } from "react-native";
import BootSplash from "react-native-bootsplash";

const logo = require("../assets/bootsplash_logo.png");
const manifest = require("../assets/bootsplash_manifest.json");

type Props = {
  onHide: () => void;
};

export const AnimatedBootSplash = ({ onHide }: Props) => {
  const [logoLoaded, setLogoLoaded] = useState(false);
  const [opacity] = useState(new Animated.Value(1));
  const [translateY] = useState(new Animated.Value(0));

  const styles = BootSplash.useStyles({
    manifest,
    statusBarTranslucent: false,
    navigationBarTranslucent: false,
  });

  useEffect(() => {
    if (!logoLoaded) {
      return;
    }

    BootSplash.hide().then(() => {
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
  }, [logoLoaded]);

  return (
    <Animated.View style={[styles.container, { opacity }]}>
      <Animated.Image
        fadeDuration={0}
        resizeMode="contain"
        source={logo}
        onLoadEnd={() => setLogoLoaded(true)}
        style={[styles.logo, { transform: [{ translateY }] }]}
      />
    </Animated.View>
  );
};
