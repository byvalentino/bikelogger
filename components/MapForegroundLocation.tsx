import React, { useState } from 'react';
import { View, Text, Button, StyleSheet, Dimensions, } from 'react-native';
import MapView from "react-native-maps";
import * as Location from "expo-location";
import * as Permissions from "expo-permissions";
import { inject, observer } from 'mobx-react';
import { useNavigation } from '@react-navigation/native';
import LocationView from './LocationView';
import { log } from '../services/Logger';
import Colors from '../constants/colors';

const INIT_REGION = {
    latitude: 31.728371,
    longitude: 35.040161,
    latitudeDelta: 1,
    longitudeDelta: 1,
}
export interface Props {
    store?: any;
}
// map and foregroundLocation using watchPositionAsync
//function MapForegroundLocation(props,initRegion) {
const MapForegroundLocation: React.FC<Props> = (props: Props) => {
    const { trackingStore } = props.store;
    const { setLocationData, setStatusText, sendRoute } = trackingStore;
    const [myState, setMyState] = useState(
        {
            locationStatus: false,
            error: '',
            watchPositionObject: null,
            location: null,
            region: INIT_REGION
        }
    );
    const navigation = useNavigation();
    const buttonLoggerClick = () => {
        navigation.navigate('Logger');
    }
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
                //log(coords);
                //TOD - write to local DB - update route
                let region = {
                    latitude: coords.latitude,
                    longitude: coords.longitude,
                    latitudeDelta: 0.005,
                    longitudeDelta: 0.005
                };
                setLocationData(newLocation);
                setStatusText('locating...');
                setMyState((prev: any) => ({ ...prev, location: newLocation, region: region, }));
            }
        ).catch((error) => {
            log(error)
        });
    };

    const startGetLocationAsync = async () => {
        log("startGetLocationAsync - forground");
        // Asking for device location permission
        const { status } = await Location.requestPermissionsAsync()
        // Permissions.askAsync(Permissions.LOCATION);
        if (status === "granted") {
            const positionObject = await startWatchPositionAsync();
            log("start locating")
            setMyState((prev: any) => ({ ...prev, watchPositionObject: positionObject, locationStatus: true }));
        } else {
            setMyState(prev => ({ ...prev, error: "Locations services needed", watchPositionObject: null, locationStatus: false }));
        }
    }
    const stopGetLocationAsync = async () => {
        if (myState.watchPositionObject !== null && myState.watchPositionObject !== undefined) {
            //@ts-ignore
            myState.watchPositionObject.remove();
            log("stop locating")
        }
        setStatusText('not locating');
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
            <LocationView />
            <View style={styles.buttonContainer}>
                <Button title={textButton} onPress={() => { setLocationStaus() }} />
                <Button title="Add Route" onPress={() => { sendRoute() }} />
                <View style={styles.button2}>
                    <Button title='log' color={Colors.primary} onPress={buttonLoggerClick} />
                </View>
            </View>

        </View>
    );
}
const styles = StyleSheet.create({
    mapStyle: {
        width: '100%',
        height: '60%',
    },
    text: {
        paddingTop: 10,
    },
    buttonContainer: {
        flexDirection: 'row',
        justifyContent: 'space-around',
    },
    button2: {
        width: 70,
    },
});

export default inject("store")(observer(MapForegroundLocation));