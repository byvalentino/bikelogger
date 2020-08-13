import React, { Component } from "react";
import { StyleSheet, View, Text, Dimensions } from "react-native";
import MapView from "react-native-maps";
import * as Location from "expo-location";
import * as Permissions from "expo-permissions";
import * as TaskManager from "expo-task-manager";
import {log} from '../services/Logger';

const LOCATION_TASK_NAME = "background-location-task";
TaskManager.defineTask(LOCATION_TASK_NAME, async ({ data, error }) => {
    if (error) {
        log(error);
        return;
    }
    if (data) {
        const { locations } = data;
        let lat = locations[0].coords.latitude;
        let long = locations[0].coords.longitude;
        //userId = (await AsyncStorage.getItem("userId")) || "none";

        // Storing Received Lat & Long to DB by logged In User Id
        // axios({
        //   method: "POST",
        //   url: "http://000.000.0.000/phpServer/ajax.php",
        //   data: {
        //     action: "saveLocation",
        //     userId: userId,
        //     lat,
        //     long
        //   }
        // });
        // log("Received new locations for user = ", userId, locations);
    }
});


export default class MyMap2 extends Component {
    constructor(props) {
        super(props);
        this.state = {
            region: null,
            error: '',
            grantedLocation: false,
            location: null,
        };
    }

    getLocationAsync = async () => {
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
                // log(coords);
                let region = {
                    latitude: coords.latitude,
                    longitude: coords.longitude,
                    latitudeDelta: 0.01,
                    longitudeDelta: 0.01
                };
                this.setState(() => ({ region: region, location: newLocation }));
            },
            error => log(error)
        );
        return this.location;
    };
    startServiceLocation = async() =>{
        // Asking for device location permission
        const { status } = await Permissions.askAsync(Permissions.LOCATION);
        if (status === "granted") {
            this.setState({ grantedLocation: true });
            this.getLocationAsync();
        } else {
            this.setState(() => ({ error: "Locations services needed", grantedLocation: false }));
        }
        //userId = (await AsyncStorage.getItem("userId")) || "none";
        //userName = (await AsyncStorage.getItem("userName")) || "none";
    }
    endServiceLocation = async() =>{
        try {
            const status = await Location.stopLocationUpdatesAsync(LOCATION_TASK_NAME);
            if (status != undefined)
                log(status);
        } catch (error) {
            log(status);
        }
    }

    async componentDidMount() {
        this.startServiceLocation();
    }
    async componentWillUnmount() {
        this.endServiceLocation();
    }
    render() {
        return (
            <View>
                <MapView
                    initialRegion={this.state.region}
                    showsCompass={true}
                    showsUserLocation={this.state.grantedLocation}
                    rotateEnabled={true}
                    ref={map => {
                        this.map = map;
                    }}
                    style={styles.mapStyle2}
                />
                <Text style={{paddingTop:10}} key='location'>{JSON.stringify(this.state.location)}</Text>
            </View>
        );
    }
}


const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: "#fff"
    },
    mapStyle1: {
        width: Dimensions.get('window').width,
        height: Dimensions.get('window').height * 1,
    },
    mapStyle2: {
        width: '100%',
        height: '80%',
    },
});
