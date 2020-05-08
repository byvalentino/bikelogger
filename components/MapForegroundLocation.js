import React, { useState } from 'react';
import { View, Text, Button, StyleSheet, Dimensions, } from 'react-native';
import MapView from "react-native-maps";
import * as Location from "expo-location";
import * as Permissions from "expo-permissions";

const INIT_REGION = {
    latitude: 31.728371,
    longitude: 35.040161,
    latitudeDelta: 1,
    longitudeDelta: 1,
}
// map and foregroundLocation using watchPositionAsync
export default function MapForegroundLocation(initRegion) {
    const [myState, setMyState] = useState(
        {
            locationStatus: false,
            error: '',
            watchPositionObject: null,
            location: null,
            region: INIT_REGION
        }
    );

    const startWatchPositionAsync = async () => {
        // watchPositionAsync Return Lat & Long on Position Change
        const watchPositionObject = await Location.watchPositionAsync(
            {
                enableHighAccuracy: true,
                distanceInterval: 1,
                timeInterval: 5000
            },
            newLocation => {
                let { coords, timestamp } = newLocation;
                //console.log(coords);
                let date = new Date(timestamp);
                console.log(date.toLocaleString());
                //TOD - write to local DB - update route
                let region = {
                    latitude: coords.latitude,
                    longitude: coords.longitude,
                    latitudeDelta: 0.005,
                    longitudeDelta: 0.005
                };
                setMyState(prev => ({ ...prev, location: newLocation, region: region, }));
            },
            error => console.log(error)
        );
        return watchPositionObject;
    };

    const startGetLocationAsync = async () => {
        // Asking for device location permission
        const { status } = await Location.requestPermissionsAsync()
        // Permissions.askAsync(Permissions.LOCATION);
        if (status === "granted") {
            const positionObject = await startWatchPositionAsync();
            // console.log("start locating")
            setMyState(prev => ({ ...prev, watchPositionObject: positionObject, locationStatus: true }));
        } else {
            setMyState(prev => ({ ...prev, error: "Locations services needed", watchPositionObject: null, locationStatus: false }));
        }
    }
    const stopGetLocationAsync = async () => {
        if (myState.watchPositionObject !== null && myState.watchPositionObject !== undefined) {
            myState.watchPositionObject.remove();
            // console.log("stop locating")
        }
        setMyState(prev => ({ ...prev, watchPositionObject: null, locationStatus: false }));
    }
    const setLocationStaus = () => {
        if (!myState.locationStatus)
            startGetLocationAsync();
        else
            stopGetLocationAsync();
    }
    const textButton = (myState.locationStatus) ? 'Stop Locating' : 'Start Locating';
    return (
        <View>
            <MapView
                initialRegion={myState.region}
                showsCompass={true}
                showsUserLocation={myState.locationStatus}
                rotateEnabled={true}
                style={styles.mapStyle}
            />
            <Text style={styles.text}>{JSON.stringify(myState.location)}</Text>
            <Button title={textButton} onPress={() => { setLocationStaus() }} />
        </View>
    );
}
const styles = StyleSheet.create({
    mapStyle: {
        width: '100%',
        height: '80%',
    },
    text: {
        paddingTop: 10,
    }
});