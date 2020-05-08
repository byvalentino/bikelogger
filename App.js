import React, { useState, useEffect } from 'react';
import { StyleSheet, Text, View, Button, Dimensions } from 'react-native';
import * as FireBase from 'firebase';
import Constants from 'expo-constants';
import * as Location from 'expo-location';
import MapView from 'react-native-maps';

import Header from './components/Header';
import MapForegroundLocation from './components/MapForegroundLocation';
import {addUser} from './services/FirestoreService';

const INIT_REGION = {
  latitude: 31.728371,
  longitude: 35.040161,
  latitudeDelta: 1,
  longitudeDelta: 1,
}

export default function App() {
  const [locationStatus, setLocationStatus] = useState(false);
  const [textLocation, setTextLocation] = useState('');
  const [errorMsg, setErrorMsg] = useState(null);
  const [location, setLocation] = useState(null);
  const [region, setRegion] = useState(INIT_REGION);

  useEffect(() => {
    // addUser('david shalom', 'david.shalom@gmail.com');
  }, []);

  const textStatus = (locationStatus) ? 'Getting Location' : 'No Location';
  const textButton = (locationStatus) ? 'Stop Locating' : 'Start Locating';
  function getLoaction() {
    (async () => {
      let { status } = await Location.requestPermissionsAsync();
      if (status !== 'granted') {
        setErrorMsg('Permission to access location was denied');
      }
      let location = await Location.getCurrentPositionAsync({});
      setLocation(location);
      const newRegion = {
        latitude: location.coords.latitude,
        longitude: location.coords.longitude,
        latitudeDelta: 0.01,
        longitudeDelta: 0.01
      };
      setRegion(newRegion);
    })();
  }
  const onRegionChange = (region) => {
    setRegion(region);
  }
  const setMyLocation = () => {
    setLocationStatus(!locationStatus);
    getLoaction();
  }
  let text = 'Waiting..';
  if (errorMsg) {
    text = errorMsg;
  } else if (location) {
    text = JSON.stringify(location);
  }
  return (
    <View style={styles.container1}>
       {/* <View>
        <Header />
      </View> */}
      {/*<View>
        <Text key='status'>{textStatus}</Text>
        <Text key='location'>{text}</Text>
        <Button title={textButton}
          onPress={() => { setMyLocation() }} />
      </View> */}
        <Header/>
        <MapForegroundLocation />
      
        {/* <MapView
        region={region}
        onRegionChangeComplete={(region) => onRegionChange(region)}
        style={styles.mapStyle}
        /> */}
      
    </View>
  );
}
const styles = StyleSheet.create({
  container: {
    paddingTop: 30,
    flex: 1
  },
  container1:{
    width:'100%', 
    height:'100%', 
    paddingTop:30
  },
  mapStyle: {
    width: Dimensions.get('window').width,
    height: Dimensions.get('window').height * 0.7,
  },
  mapStyle1: {
    flex: 1
  },
  containerMap: {
    flex: 1,
    backgroundColor: "red"
  }
});
