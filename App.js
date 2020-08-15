import React, { useState, useEffect } from 'react';
import { StyleSheet, Text, View, Button, Dimensions } from 'react-native';
import AsyncStorage from '@react-native-community/async-storage';
import { I18nManager } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
// imports Provider and store
import { Provider } from 'mobx-react';
import 'mobx-react-lite/batchingForReactNative';
import { YellowBox } from 'react-native';

import Store from './stores/Store';
import Router from './Router';

// ignore firebase Setting a timer for a long period of time 
// See https://github.com/facebook/react-native/issues/12981 for more info
YellowBox.ignoreWarnings(['Setting a timer']);
// prevent Right-To-Left layout of english UI in RTL devices
I18nManager.allowRTL(false);

export default function App() {
  return (
    <Provider store={Store}>
      <Router />
    </Provider>
  );
}
