import React from 'react';
import { View, Button, StyleSheet } from 'react-native';
import { defineTask } from "expo-task-manager";
import { inject, observer } from 'mobx-react';

import Colors from '../constants/colors';
import LocationView from './LocationView';
import MyMapView from './MyMapView';
import {startGetLocationAsync, stopGetLocationAsync} from '../services/BackgroundLocation';
import ConfigScreen from  '../screens/ConfigScreen';

import LocationTaskExecutor from '../services/taskLocation';

const LOCATION_TASK_NAME = "background-location-task";
defineTask(LOCATION_TASK_NAME, LocationTaskExecutor);

export interface Props {
    store?: any;
  }

// map and background Location 
const MapBackgroundLocation: React.FC<Props> = (props:Props) => {
    const { isTracking, setConfigModalVisible } = props.store;
    const setLocationStaus = () => {
        if (!isTracking)
            startGetLocationAsync();
        else
            stopGetLocationAsync();
    }
    const textButton = (isTracking) ? 'STOP TRACKING' : 'START TRACKING';
    return (
        <View>
            <MyMapView />
            <LocationView />
            <View style={styles.buttonContainer}>
                <View style={styles.button}>
                    <Button title={textButton} color={Colors.primary} onPress={() => { setLocationStaus() }} />
                </View>
                <View style={styles.button}>
                    <Button title='Config' color={Colors.primary} onPress={() => { setConfigModalVisible(true) }} />
                </View>
            </View>
            <ConfigScreen />
        </View>
    );
}
const styles = StyleSheet.create({
    buttonContainer: {
        flexDirection: 'row',
        justifyContent: 'space-around',
    },
    button: {
        width: 200,
    },
});

export default inject("store")(observer(MapBackgroundLocation));