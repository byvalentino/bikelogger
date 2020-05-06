import React, { useState } from 'react';
import {  View, Text, Button, StyleSheet, Dimensions, } from 'react-native';
import MapView from "react-native-maps";
import * as Location from "expo-location";
import * as Permissions from "expo-permissions";
import * as TaskManager from "expo-task-manager";

const INIT_REGION = {
    latitude: 31.728371,
    longitude: 35.040161,
    latitudeDelta: 1,
    longitudeDelta: 1,
}

const LOCATION_TASK_NAME = "background-location-task";
TaskManager.defineTask(LOCATION_TASK_NAME, async ({ data, error }) => {
    if (error) {
        console.log(error);
        return;
    }
    if (data) {
        const { locations } = data;
        let lat = locations[0].coords.latitude;
        let long = locations[0].coords.longitude;
    }
});

export default function MyMap(initRegion) {
    const [locationObj, setLocationObj] = useState(
        {
            grantedLocation: false,
            locationStatus: false, 
            error: '',
            location: null,
            region: INIT_REGION
        }
    );
   
    const getLocationAsync = async () => {
        await Location.startLocationUpdatesAsync(LOCATION_TASK_NAME, {
            enableHighAccuracy: true,
            distanceInterval: 1,
            timeInterval: 5000
        });
        // watchPositionAsync Return Lat & Long on Position Change
        this.location = await Location.watchPositionAsync(
            {
                enableHighAccuracy: true,
                distanceInterval: 1,
                timeInterval: 10000
            },
            newLocation => {
                let { coords } = newLocation;
                // console.log(coords);
                let region = {
                    latitude: coords.latitude,
                    longitude: coords.longitude,
                    latitudeDelta: 0.01,
                    longitudeDelta: 0.01
                };
                setLocationObj(prev => ({ ...prev, location: newLocation, region: region, }));
            },
            error => console.log(error)
        );
        return this.location;
    };

    const startServiceLocation = async () => {
        // Asking for device location permission
        const { status } = await Permissions.askAsync(Permissions.LOCATION);
        if (status === "granted") {
            setLocationObj(prev => ({ ...prev, grantedLocation: true, locationStatus: true}));
            getLocationAsync();
        } else {
            setLocationObj(prev => ({ ...prev, error: "Locations services needed", grantedLocation: false, locationStatus: false }));
        }
        //userId = (await AsyncStorage.getItem("userId")) || "none";
        //userName = (await AsyncStorage.getItem("userName")) || "none";
    }
    const endServiceLocation = async () => {
        try {
            const status = await Location.stopLocationUpdatesAsync(LOCATION_TASK_NAME);
            setLocationObj(prev => ({ ...prev, locationStatus: false}));
            if (status != undefined)
                console.log(status);
        } catch (error) {
            console.log(status);
        }
    }
    const setLocationStaus = () => {
        if (!locationObj.locationStatus)
            startServiceLocation();
        else
            endServiceLocation();    
      }
    const textButton = (locationObj.locationStatus) ? 'Stop Locating' : 'Start Locating';
    return (
        <View>
            <MapView
                initialRegion={locationObj.region}
                showsCompass={true}
                showsUserLocation={locationObj.grantedLocation}
                rotateEnabled={true}
                style={styles.mapStyle2}
            />
            <Text style={styles.text}>{JSON.stringify(locationObj.location)}</Text>
            <Button title={textButton} onPress={() => { setLocationStaus() }} />
        </View>
    );
}
const styles = StyleSheet.create({
    mapStyle: {
        width: Dimensions.get('window').width,
        height: Dimensions.get('window').height * 0.7,
    },
    mapStyle2: {
        width: '100%',
        height: '80%',
    },
    text: {
        paddingTop: 10,
    }
});