import React, { useState } from 'react';
import { View, Text, Button, StyleSheet, Dimensions, } from 'react-native';
import MapView from "react-native-maps";
import * as Location from "expo-location";
import * as Permissions from "expo-permissions";
import * as firebase from 'firebase'
import {addRoute} from '../services/FirestoreService';

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
            <View style={styles.buttonContainer}>
                <Button title={textButton} onPress={() => { setLocationStaus() }} />
                <Button title="Add Route" onPress={() => { sendRoute() }} />
            </View>
            
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
    },
    buttonContainer: {
        flexDirection: 'row',
        justifyContent: 'space-around',
    }
});

const sendRoute = () => {
    const  geojsonRoute = {
        type: "Feature",
        properties: {
            name: "Route1",
            startDate: firebase.firestore.Timestamp.fromDate(new Date("December 10, 1815")),
            times: [
                "Fri May  8 06:29:50 2020",
                "Fri May  8 06:29:55 2020"
            ]
        },
        geometry: {
            type: "LineString",
            coordinates: [
              "[100.0, 0.0]",
              "[101.0, 1.0]"  
            ]
        }
      };
    console.log(geojsonRoute);
    addRoute(geojsonRoute);
}