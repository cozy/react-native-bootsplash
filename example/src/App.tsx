import * as React from "react";
import { StyleSheet, Text, View } from "react-native";
import { AnimatedBootSplash } from "./AnimatedSplashScreen";

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#F5FCFF",
  },
  text: {
    fontSize: 24,
    fontWeight: "700",
    margin: 20,
    lineHeight: 30,
    color: "#333",
    textAlign: "center",
  },
});

export const App = () => {
  const [visible, setVisible] = React.useState(true);

  return (
    <View style={styles.container}>
      <Text style={styles.text}>Hello, Dave.</Text>

      {visible && (
        <AnimatedBootSplash
          onHide={() => {
            setVisible(false);
          }}
        />
      )}
    </View>
  );
};
