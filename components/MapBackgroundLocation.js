import React from 'react';
import { View, Text, Button, StyleSheet, Dimensions, } from 'react-native';
import MapView from "react-native-maps";
import * as Location from "expo-location";
import * as Permissions from "expo-permissions";
import { defineTask } from "expo-task-manager";
import { inject, observer } from 'mobx-react';
import {toJS} from 'mobx';

import LocationView from './LocationView';
import MyMapView from './MyMapView';
import LocationTaskExecutor from '../services/TaskLocation'

const LOCATION_TASK_NAME = "background-location-task";
defineTask(LOCATION_TASK_NAME, LocationTaskExecutor);

// map and background Location 
function MapBackgroundLocation(props, initRegion) {
    const { isTracking, updateIsTracking, updatelocationData, updateStatusText, sendRoute, region } = props.store;
    const reactRegion = toJS(region);

    const startLocationTaskAsync = async () => {
        await Location.startLocationUpdatesAsync(LOCATION_TASK_NAME, {
            enableHighAccuracy: true,
            distanceInterval: 1,
            timeInterval: 5000,
            foregroundService: {
                notificationTitle: 'Bike Location',
                notificationBody: 'Enable this to make GPS alive'
            },
        });
    };

    const startGetLocationAsync = async () => {
        // Asking for device location permission
        const { status } = await Location.requestPermissionsAsync()
        if (status === "granted") {
            await startLocationTaskAsync();
            updateStatusText('Locating...');
            updateIsTracking(true);
            // console.log("start locating")
        } else {
            updateIsTracking(false);
            updateStatusText('Locations services needed');
        }
    }

    const stopGetLocationAsync = async () => {
        await Location.stopLocationUpdatesAsync(LOCATION_TASK_NAME);
        updateStatusText('Not Locating');
        updateIsTracking(false);
        sendRoute();
    }
    
    const setLocationStaus = () => {
        if (!isTracking)
            startGetLocationAsync();
        else
            stopGetLocationAsync();
    }
    const textButton = (isTracking) ? 'Stop Locating' : 'Start Locating';
    return (
        <View>
            <MyMapView />
            <LocationView />
            <View style={styles.buttonContainer}>
                <View style={styles.button}>
                    <Button title={textButton} onPress={() => { setLocationStaus() }} />
                </View>
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
    },
    button: {
        width: 200,
    },
});

export default inject("store")(observer(MapBackgroundLocation));