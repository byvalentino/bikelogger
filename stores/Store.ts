import { observable, action } from "mobx";
import { LocationData } from "expo-location";
import { firestore } from 'firebase';
import UiStore from './uiStore';
import { addRouteAsync, readUserAsync, setUserAsync, updateUserAsync } from '../services/FirestoreService';
import { getDistanceKm } from '../services/GeoUtils';
import { storeLocalData, getLocalData } from '../services/LocalStorage';
import { log } from '../services/Logger';
//import 'intl';

const INIT_REGION = {
    latitude: 31.728371,
    longitude: 35.040161,
    latitudeDelta: 1,
    longitudeDelta: 1,
}

class Store {
    constructor() {
        this.uiStore = new UiStore(this);
    }
    uiStore: UiStore;

    init = async () => {
        log('init Store');
        const dataUserToken = await this.initUserToken();
        const data = await this.initUserEmail();
        getLocalData('@password').then(res => {
            if (res !== undefined)
                this.userPassword = res;
        });
        this.setStoreReady(true);
    }
    @observable isStoreReady = false;
    @action setStoreReady = (value: boolean) => {
        this.isStoreReady = value;
    }


    /// User Store ////////////////////////////////////////////////////////////////////
    @observable userToken = '';
    @action setUserToken = (value: string) => {
        this.userToken = value;
        storeLocalData('@userToken', value);
    }
    initUserToken = async () => {
        const val = await getLocalData('@userToken');
        if (val !== undefined) {
            this.setUserToken(val);
        }
    }
    // @observable tempMail = '';
    // @action setTempMail = (value: string) => {
    //     this.tempMail = value;
    // }
    @observable userEmail = '';
    @action setUserEmail = (value: string) => {
        this.userEmail = value;
        storeLocalData('@email', value);
    }
    initUserEmail = async () => {
        const email = await getLocalData('@email');
        if (email !== undefined) {
            this.setUserEmail(email);
        }
    }

    @observable userPassword = '';
    @action setUserPassword = (value: string) => {
        this.userEmail = value;
        storeLocalData('@password', value);
    }
    @observable userFirstName = '';
    @action setUserFirstName = (value: string) => {
        this.userFirstName = value;
    }
    @observable userLastName = '';
    @action setUserLastName = (value: string) => {
        this.userLastName = value;
    }

    @observable expoPushToken = '';
    @action setExpoPushToken = (value: string, sendToCloud: boolean = true) => {
        this.expoPushToken = value;
        if (sendToCloud)
            this.updateUserExpoPushToken();
    }


    @action postUserData = () => {
        //const name = "user-" + this.userToken;
        const DateNow = new Date();
        const fDate = this.formatDate(DateNow);
        const userData = {
            first_name: this.userFirstName,
            last_name: this.userLastName,
        };
        //log(geojsonRoute);
        if (userData !== null) {
            this.updateUserToCloud(userData);
        }
    }
    @action fetchUserData = () => {
        if (this.userToken !== '') {
            const name = "user-" + this.userToken;
            readUserAsync(name)
                .then((data) => {
                    if (data) {
                        this.setUserFirstName(data.first_name);
                        this.setUserLastName(data.last_name);
                        this.setExpoPushToken(data.push_token, false);
                    }

                })
        }
    }
    @action updateUserLastLogin = () => {
        const DateNow = new Date();
        const fDate = this.formatDate(DateNow);
        log('logged: ' + fDate);
        // console.log("user-" + this.userToken);
        const userData = { last_logged_in: fDate };
        this.updateUserToCloud(userData);
    }
    @action updateUserExpoPushToken = () => {
        const userData = {
            push_token: this.expoPushToken,
        };
        this.updateUserToCloud(userData);
    }
    updateUserToCloud = (userData: any) => {
        if (this.userToken !== '') {
            const name = "user-" + this.userToken;
            updateUserAsync(userData, name);
        }
    }
    /// Tracking Store //////////////////

    //tracking Time Interval in sec
    @observable trackingTimeInterval = 3;
    @action setTrackingTimeInterval = (timeInterval: number) => {
        this.trackingTimeInterval = timeInterval;
    }

    // is gps location Tracking is on
    @observable isTracking = false;
    @action setIsTracking = (isTrack: boolean) => {
        this.isTracking = isTrack;
    }

    // region in the map 
    @observable region: any = INIT_REGION;
    @action setRegion = (newRegion: any) => {
        this.region = newRegion;
    }

    // observable to statusText
    @observable statusText = 'Not Tracking';
    // action to set statusText
    @action setStatusText = (text: string) => {
        this.statusText = text;
    }

    // observable to locationText
    @observable locationText = '';
    // action to update locationText
    @action setLocationText = (text: string) => {
        this.locationText = text;
    }
    // observable to accuracy
    @observable accuracy = 0;
    // action to set accuracy
    @action setAcuracy = (acc: number) => {
        this.accuracy = acc;
    }
    // observable to speed
    @observable speed = 0;
    // action to set speed
    @action setSpeed = (spd: number) => {
        this.speed = spd;
    }
    @observable altitude = 0;
    // action to set altitude
    @action setAltitude = (alt: number) => {
        this.altitude = alt;
    }
    // observable points lat/lon
    @observable pointsArr: any[] = [];
    @action InsertToPointsArr = (point: number[]) => {
        this.pointsArr.push(point);
    }
    @action initPointsArr = () => {
        this.pointsArr = [];
    }
    // observable dates
    @observable datesArr: Date[] = [];
    @action InsertToDatesArr = (timestamp: number) => {
        let date = new Date(timestamp);
        this.datesArr.push(date);
    }
    @action initDatesArr = () => {
        this.datesArr = [];
    }
    // observable alts         
    @observable altArr: number[] = [];
    @action insertToAltArr = (alt: number) => {
        this.altArr.push(alt);
    }
    @action initAltArr = () => {
        this.altArr = [];
    }
    //routeDistance in KM
    @observable routeDistance: number = 0;
    @action setRouteDistance = (point1: number[]) => {
        if (this.pointsArr.length > 1) {
            let point2 = this.pointsArr[this.pointsArr.length - 2];
            let addedDist = getDistanceKm(point1[0], point1[1], point2[0], point2[1]);
            this.routeDistance += addedDist;
        }

    }

    // observable for locationData - current location
    @observable locationData: LocationData | null = null;
    @action setLocationData = (data: LocationData) => {
        this.locationData = data;
        const point: number[] = [data.coords.latitude, data.coords.longitude];
        this.InsertToPointsArr(point);
        this.InsertToDatesArr(data.timestamp);
        this.insertToAltArr(data.coords.altitude);
        this.setRouteDistance(point);
        //update ui and log
        const date = new Date(data.timestamp);
        const textlog = '(' + data.coords.latitude + ',' + data.coords.longitude + ')' + this.formatDate(date);
        log(textlog);
        const textUI = '(' + data.coords.latitude + ', ' + data.coords.longitude + ')';
        this.setLocationText(textUI);
        this.setAcuracy(data.coords.accuracy);
        this.setAltitude(data.coords.altitude);
        this.setSpeed(data.coords.speed);
        //update map
        const region = {
            latitude: data.coords.latitude,
            longitude: data.coords.longitude,
            latitudeDelta: 0.005,
            longitudeDelta: 0.005
        };
        this.setRegion(region);
    }

    createGeoJsonRoute = (name: string, startTime: Date, dist: number) => {
        if (this.datesArr.length < 2)
            return null;
        const times = this.datesArr.map(date => this.formatDate(date));
        const coords = this.pointsArr.map(p => new firestore.GeoPoint(p[0], p[1]));
        const geojsonRoute = {
            type: "Feature",
            properties: {
                name: name,
                startDate: firestore.Timestamp.fromDate(startTime),
                dist: dist.toFixed(2),
                times: times,
                alts: this.altArr,
            },
            geometry: {
                type: "LineString",
                coordinates: coords
            }
        };
        return geojsonRoute;
    }

    @observable isSendRoute = true;
    @action sendRoute = () => {
        const startTime = this.datesArr[0];
        const name = "route-" + this.userToken + '-' + this.formatDate(startTime);
        const geojsonRoute = this.createGeoJsonRoute(name, startTime, this.routeDistance);
        //log(geojsonRoute);
        if (this.isSendRoute && geojsonRoute !== null) {
            addRouteAsync(geojsonRoute, name);
        }
        this.initDatesArr();
        this.initPointsArr();
        this.initAltArr();
    }

    formatDate = (dt: Date) => {
        return `${dt.getFullYear().toString().padStart(4, '0')}-${
            (dt.getMonth() + 1).toString().padStart(2, '0')}-${
            dt.getDate().toString().padStart(2, '0')},${
            dt.getHours().toString().padStart(2, '0')}:${
            dt.getMinutes().toString().padStart(2, '0')}:${
            dt.getSeconds().toString().padStart(2, '0')}`;
    }
}

export default new Store();
