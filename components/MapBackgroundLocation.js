import React, { useState } from 'react';
import { View, Text, Button, StyleSheet, Dimensions, } from 'react-native';
import MapView from "react-native-maps";
import * as Location from "expo-location";
import * as Permissions from "expo-permissions";
import * as TaskManager from "expo-task-manager";
import { inject, observer } from 'mobx-react';
import LocationView from './LocationView';
import LocationTaskExecutor from '../services/taskLocation'

const LOCATION_TASK_NAME = "background-location-task";
TaskManager.defineTask(LOCATION_TASK_NAME, LocationTaskExecutor);

const INIT_REGION = {
    latitude: 31.728371,
    longitude: 35.040161,
    latitudeDelta: 1,
    longitudeDelta: 1,
}
// map and background Location 
function MapBackgroundLocation(props, initRegion) {
    const { updatelocationData, updateText, sendRoute } = props.store;
    const [myState, setMyState] = useState(
        {
            locationStatus: false,
            error: '',
            location: null,
            region: INIT_REGION
        }
    );

    const startLocationTaskAsync = async () => {
        await Location.startLocationUpdatesAsync(LOCATION_TASK_NAME, {
            enableHighAccuracy: true,
            distanceInterval: 1,
            timeInterval: 5000,
            foregroundService: {
                notificationTitle: 'Bike Location',
                notificationBody: 'enable this to make GPS alive'
              }, 
        });
    };

    const startGetLocationAsync = async () => {
        // Asking for device location permission
        const { status } = await Location.requestPermissionsAsync()
        if (status === "granted") {
            await startLocationTaskAsync();
            // console.log("start locating")
            setMyState(prev => ({ ...prev, locationStatus: true }));
        } else {
            setMyState(prev => ({ ...prev, error: "Locations services needed", locationStatus: false }));
        }
    }
    const stopGetLocationAsync = async () => {
        await Location.stopLocationUpdatesAsync(LOCATION_TASK_NAME);
        updateText('not locating');
        setMyState(prev => ({ ...prev, locationStatus: false }));
        //sendRoute();
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
            <LocationView location={myState.location} />
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
        height: '70%',
    },
    text: {
        paddingTop: 10,
    },
    buttonContainer: {
        flexDirection: 'row',
        justifyContent: 'space-around',
    }
});

export default inject("store")(observer(MapBackgroundLocation));