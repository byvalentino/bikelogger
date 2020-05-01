import React, { useState } from 'react';
import { StyleSheet, Dimensions } from 'react-native';
import MapView from 'react-native-maps';

const INIT_REGION = {
    latitude: 31.728371,
    longitude: 35.040161,
    latitudeDelta: 1,
    longitudeDelta: 1,
}

export default function MyMap(initRegion) {
    const [region, setRegion] = useState(INIT_REGION);
    const onRegionChange = (region) => {
        setRegion(region);
    }
    return (
        <MapView
            style={styles.mapStyle}
            region={region}
            onRegionChangeComplete={(region) => onRegionChange(region)}
        />
    );
}
const styles = StyleSheet.create({
    mapStyle: {
      width: Dimensions.get('window').width,
      height: Dimensions.get('window').height * 0.7,
    },
  });