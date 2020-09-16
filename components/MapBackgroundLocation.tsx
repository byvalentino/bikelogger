import React from 'react';
import { View, Button, StyleSheet, TouchableHighlight } from 'react-native';
import { defineTask } from "expo-task-manager";
import { inject, observer } from 'mobx-react';
import { useNavigation } from '@react-navigation/native';

import {IStore} from '../stores/Store';
import LocationView from './LocationView';
import MyMapView from './MyMapView';
import AccelerometerScreen from '../screens/AccelerometerScreen';
import { startGetLocationAsync, stopGetLocationAsync } from '../services/BackgroundLocation';
import LocationTaskExecutor from '../services/taskLocation';
import Colors from '../constants/colors';
import { Ionicons } from '@expo/vector-icons';

const LOCATION_TASK_NAME = "background-location-task";
// define the task that will run when location is found
defineTask(LOCATION_TASK_NAME, LocationTaskExecutor);

export interface Props {
    store?: IStore;
}

// map and background Location 
const MapBackgroundLocation: React.FC<Props> = (props: Props) => {
    const {
        trackingStore, 
        uiStore,
        } = props.store!;
    const {isTracking} = trackingStore;
    const {setAccelerometerModalVisable} = uiStore;   
    const navigation = useNavigation();

    // const buttonConfigClick = () => {
    //     navigation.navigate('Config');
    // }
    // React.useLayoutEffect(() => {
    //     navigation.setOptions({
    //         headerRight: () => (
    //             <AntDesign.Button style={styles.buttonHead} name='setting' onPress={buttonConfigClick} />
    //         ),
    //     });
    // }, [navigation]);
    const buttonLoggerClick = () => {
        navigation.navigate('Logger');
    }
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
                {/* <View style={styles.button2}>
                    <Button title='Config' color={Colors.primary} onPress={() => { setConfigModalVisible(true) }} />
                </View> */}
                <View style={styles.button2}>
                    <Button title='Acc' color={Colors.primary} onPress={() => { setAccelerometerModalVisable(true) }} />
                </View>
                <View style={styles.button2}>
                    <Button title='log' color={Colors.primary} onPress={buttonLoggerClick} />
                </View>
            </View>
            {/* <ConfigModalScreen /> */}
            <AccelerometerScreen />
            <ConfigButton></ConfigButton>
        </View>
    );
}

const ConfigButton: React.FC<Props> = (props: Props) => {
    const navigation = useNavigation();
    const buttonConfigClick = () => {
        navigation.navigate('Settings');
    }
    React.useLayoutEffect(() => {
        navigation.setOptions({
            headerRight: () => (
                <View style={styles.buttonHeadView}>
                    <TouchableHighlight
                        onPress={buttonConfigClick}>
                        <View>
                            <Ionicons name="md-settings" size={24} color="#fff" />
                        </View>
                    </TouchableHighlight>
                </View>
            ),
        });
    }, [navigation]);
    return (
        null
    )
}

const styles = StyleSheet.create({
    buttonContainer: {
        flexDirection: 'row',
        justifyContent: 'space-around',
    },
    button: {
        width: 170,
    },
    button2: {
        width: 70,
    },
    buttonHead: {
        alignItems: "center",
    },
    buttonHeadView: {
        marginRight: 10,
    },
});

export default inject("store")(observer(MapBackgroundLocation));