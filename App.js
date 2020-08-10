import React, { useState, useEffect } from 'react';
import { StyleSheet, Text, View, Button, Dimensions } from 'react-native';
import { I18nManager} from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';

// imports Provider and store
import { Provider } from 'mobx-react';
import 'mobx-react-lite/batchingForReactNative';

import store from './stores/Store';
import Header from './components/Header';
import MainScreen from './screens/MainScreen';
import LoginScreen from './screens/LoginScreen';
//import AccelerometerScreen from './screens/AccelerometerScreen';
//import MapForegroundLocation from './components/MapForegroundLocation'
import MapBackgroundLocation from './components/MapBackgroundLocation'
// ignore firebase Setting a timer for a long period of time 
// See https://github.com/facebook/react-native/issues/12981 for more info
import { YellowBox } from 'react-native';
YellowBox.ignoreWarnings(['Setting a timer']);

const Stack = createStackNavigator();
I18nManager.allowRTL(false);

export default function App() {
  return (
    <Provider store={store}>
      <NavigationContainer>
        <Stack.Navigator initialRouteName="Login">
              <Stack.Screen name="Login" component={LoginScreen} />
              <Stack.Screen name="Bike Tracker" component={MainScreen} />
              {/* <Stack.Screen name="AccelerometerScreen" component={AccelerometerScreen} /> */}
        </Stack.Navigator>
     </NavigationContainer>
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
