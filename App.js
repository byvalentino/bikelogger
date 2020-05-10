import React, { useState, useEffect } from 'react';
import { StyleSheet, Text, View, Button, Dimensions } from 'react-native';
// imports Provider and store
import { Provider } from 'mobx-react';
import 'mobx-react-lite/batchingForReactNative';

import store from './stores/Store';
import Header from './components/Header';
//import MapForegroundLocation from './components/MapForegroundLocation'
import MapBackgroundLocation from './components/MapBackgroundLocation'
// ignore firebase Setting a timer for a long period of time 
// See https://github.com/facebook/react-native/issues/12981 for more info
import { YellowBox } from 'react-native';
YellowBox.ignoreWarnings(['Setting a timer']);

export default function App() {
  return (
    <Provider store={store}>
      <View style={styles.container1}>
        <Header />
        <MapBackgroundLocation />
      </View>
    </Provider>
  );
}

const styles = StyleSheet.create({
  container: {
    paddingTop: 30,
    flex: 1
  },
  container1: {
    width: '100%',
    height: '100%',
    paddingTop: 30
  },
});
