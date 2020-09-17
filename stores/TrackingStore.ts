import { observable, action } from 'mobx';
import { firestore } from 'firebase';
import { LocationData } from "expo-location";

import Store from './Store';
import SensorsInterval from './SensorsStore';
import { addRouteAsync } from '../services/FirestoreService';
import { getDistanceKm } from '../services/GeoUtils';
import { log } from '../services/Logger';

const INIT_REGION = {
    latitude: 31.728371,
    longitude: 35.040161,
    latitudeDelta: 1,
    longitudeDelta: 1,
}

export default class TrackingStore {
    appInitialized = false

    constructor(rootStore: any) {
        // init app data
        this.rootStore = rootStore;
    }
    rootStore: any;

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
    // point inedex in route
    @observable pointIndex: number = -1;
    @action incrementPointIndex = () => {
        this.pointIndex++;
    }
    @action initRoute = () => {
        this.pointIndex = -1;
        this.datesArr = [];
        this.pointsArr = [];
        this.altArr = [];
        this.sensoersIntervalArr = [];
    }

    // observable points lat/lon
    @observable pointsArr: any[] = [];
    @action InsertToPointsArr = (point: number[]) => {
        this.pointsArr.push(point);
    }
    // observable dates
    @observable datesArr: Date[] = [];
    @action InsertToDatesArr = (timestamp: number) => {
        let date = new Date(timestamp);
        this.datesArr.push(date);
    }
    // observable alts         
    @observable altArr: number[] = [];
    @action insertToAltArr = (alt: number) => {
        this.altArr.push(alt);
    }

    /////Sensoers store///////////////////////////
    @observable sensoersIntervalLimit: number = 6* 1000;
    chaeckIntervalLimit = (interval :SensorsInterval) => {
        const aTime = Date.now();
        return (aTime - interval.startTime > this.sensoersIntervalLimit);
    }
    @observable accMsg: string = 'accMsg';
    @action setAccMsg = (value: string) => {
        this.accMsg= value;
    }

    @observable currentSensoersInterval :SensorsInterval | null =null;
    @observable sensoersIntervalArr: SensorsInterval[] = [];
    @action startSensorInerval = () => {
        console.log( 'startSensorInerval');
        this.currentSensoersInterval = new SensorsInterval();
    }
    @observable subscriptionAcc: any = null;
    @action setSubscriptionAcc = (listener: any) => {
        this.subscriptionAcc = listener;
    }
    @action removeSubscriptionAcc = () => {
        this.subscriptionAcc && this.subscriptionAcc.remove();
        this.subscriptionAcc = null;
    }
    @action addAcceleromerReading = (data: any) => {
        if (this.currentSensoersInterval && this.currentSensoersInterval.active == true){
            let { x, y, z} = data;
            //console.log( 'x', x);
            this.currentSensoersInterval.addAcceleromerReading(x,y,z)
            // if (this.chaeckIntervalLimit(this.currentSensoersInterval)){
            //     this.stopSensorInerval();
            // }
            return true;   
        }
        else return false
    }
    @action addGyroReading =(data: any) => {
        let { x, y, z} = data;
        if (this.currentSensoersInterval && this.currentSensoersInterval.active == true){
            this.currentSensoersInterval.addGyroReading(x,y,z)
            if (this.chaeckIntervalLimit(this.currentSensoersInterval)){
                this.stopSensorInerval();
            } 
        }
    }
    @action stopSensorInerval = () => {
        if (this.currentSensoersInterval !== null ){
            this.currentSensoersInterval.stopInterval()
            this.sensoersIntervalArr.push(this.currentSensoersInterval);
            //console.log( this.currentSensoersInterval);
            console.log( this.currentSensoersInterval.acceleromerArr.length);
            console.log( this.currentSensoersInterval.stopTime-  this.currentSensoersInterval.startTime);
            this.setAccMsg (this.currentSensoersInterval.acceleromerArr.length +"(" + (this.currentSensoersInterval.stopTime-  this.currentSensoersInterval.startTime) +')');
        }
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
        //ignore first point - might be history grabage
        if (this.pointIndex >= 0) {
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
        }
        // increment point index
        this.incrementPointIndex();
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
        const name = "route-" + this.rootStore.userStore.userToken + '-' + this.formatDate(startTime);
        const geojsonRoute = this.createGeoJsonRoute(name, startTime, this.routeDistance);
        // log(geojsonRoute);
        if (this.isSendRoute && geojsonRoute !== null) {
            addRouteAsync(geojsonRoute, name);
        }
        this.initRoute();
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
