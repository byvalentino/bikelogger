import React, { Component } from "react";
import { StyleSheet, View, Dimensions } from "react-native";
import MapView from "react-native-maps";
import * as Location from "expo-location";
import * as Permissions from "expo-permissions";
import * as TaskManager from "expo-task-manager";

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
        // console.log("Received new locations for user = ", userId, locations);
    }
});


export default class MyMap2 extends Component {
    constructor(props) {
        super(props);
        this.state = {
            region: null,
            error: '',
            grantedLocation: false,
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
                // console.log(coords);
                let region = {
                    latitude: coords.latitude,
                    longitude: coords.longitude,
                    latitudeDelta: 0.01,
                    longitudeDelta: 0.01
                };
                this.setState({ region: region });
            },
            error => console.log(error)
        );
        return this.location;
    };

    async componentDidMount() {
        // Asking for device location permission
        const { status } = await Permissions.askAsync(Permissions.LOCATION);
        if (status === "granted") {
            this.getLocationAsync();
            this.setState({grantedLocation: true});
        } else {
            this.setState({ error: "Locations services needed" });
        }
        //userId = (await AsyncStorage.getItem("userId")) || "none";
        //userName = (await AsyncStorage.getItem("userName")) || "none";
    }
    async componentWillUnmount() {
        try {
            const status = await Location.stopLocationUpdatesAsync(LOCATION_TASK_NAME);
            if (status != undefined)
                console.log(status);
        } catch (error) {
            console.log(status);
        }
    }
    render() {
        return (
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
        width:'100%', 
        height:'80%',
    },
});
