import React, { useState } from 'react';
import { View, Text, Button, StyleSheet, Dimensions, } from 'react-native';
import MapView from "react-native-maps";
import * as Location from "expo-location";
import * as Permissions from "expo-permissions";
import { inject, observer } from 'mobx-react';
import LocationView from './LocationView';

const INIT_REGION = {
    latitude: 31.728371,
    longitude: 35.040161,
    latitudeDelta: 1,
    longitudeDelta: 1,
}
// map and foregroundLocation using watchPositionAsync
function MapForegroundLocation(props,initRegion) {
    const { updatelocationData , updateText , sendRoute} = props.store;
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
                //TOD - write to local DB - update route
                let region = {
                    latitude: coords.latitude,
                    longitude: coords.longitude,
                    latitudeDelta: 0.005,
                    longitudeDelta: 0.005
                };
                updatelocationData(newLocation);
                updateText('locating...');
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
        updateText('not locating');
        setMyState(prev => ({ ...prev, watchPositionObject: null, locationStatus: false }));
        sendRoute();
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
            <LocationView location={myState.location}/>
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

export default inject("store")(observer(MapForegroundLocation));