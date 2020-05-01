import React, { useState } from 'react';
import { StyleSheet, Text, View, Button, Dimensions } from 'react-native';
import Constants from 'expo-constants';
import * as Location from 'expo-location';
import MapView from 'react-native-maps';

export default function App() {
  const [locationStatus, setLocationStatus] = useState(false);
  const [textLocation, setTextLocation] = useState('');
  const [errorMsg, setErrorMsg] = useState(null);
  const [location, setLocation] = useState(null);

  const textStatus = (locationStatus) ? 'Getting Location' : 'No Location';
  const textButton = (locationStatus) ? 'Stop Locating' : 'Start Locating';
  function getLoaction1() {
    if (Platform.OS === 'android' && !Constants.isDevice) {
      setErrorMsg(
        'Oops, this will not work on Sketch in an Android emulator. Try it on your device!'
      );
    } else {
      (async () => {
        let { status } = await Location.requestPermissionsAsync();
        if (status !== 'granted') {
          setErrorMsg('Permission to access location was denied');
        }
        let location = await Location.getCurrentPositionAsync({});
        setLocation(location);
      })();
    }
  }

  const setMyLocation = () => {
    setLocationStatus(!locationStatus);
    getLoaction1();
  }
  let text = 'Waiting..';
  if (errorMsg) {
    text = errorMsg;
  } else if (location) {
    text = JSON.stringify(location);
  }
  return (
    <View style={styles.container}>
      <Text key='status'>{textStatus}</Text>
      <Text key='location'>{text}</Text>
      <Button title={textButton}
        onPress={() => { setMyLocation() }} />
      <MapView style={styles.mapStyle} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
  mapStyle: {
    width: Dimensions.get('window').width,
    height: Dimensions.get('window').height *0.7,
  },
});
