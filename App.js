import React from 'react';
import { StatusBar } from 'expo-status-bar';
import { I18nManager, LogBox } from 'react-native';
// imports Provider and store
import { Provider } from 'mobx-react';
import 'mobx-react-lite/batchingForReactNative';
//import { YellowBox } from 'react-native';

import Store from './stores/Store';
import Router from './Router';

// ignore firebase Setting a timer for a long period of time 
// See https://github.com/facebook/react-native/issues/12981 for more info
//YellowBox.ignoreWarnings(['Setting a timer']); //change on expo sdk 39 
LogBox.ignoreLogs(['Setting a timer']);

// prevent Right-To-Left layout of english UI in RTL devices
I18nManager.allowRTL(false);

export default function App() {
  return (
    <Provider store={Store}>
      <Router />
      <StatusBar style="light" />
    </Provider>
  );
}
