import { decorate, observable, action } from "mobx";
import { LocationData } from "expo-location";
import { firestore } from 'firebase';
import { addRouteAsync } from '../services/FirestoreService';
//import 'intl';

class Store {

    @observable userId = '111';

    @action updateUserId = (uid: string) => {
        this.userId = uid;
    }

    // observable to statusText
    @observable statusText = '';

    // action to update statusText
    @action updateStatusText = (text: string) => {
        this.statusText = text;
    }

    // observable to statusText
    @observable locationText = '';

    // action to update statusText
    @action updateLocationText = (text: string) => {
        this.locationText = text;
    }


    // observable for locationData - current location
    @observable locationData: LocationData | null = null;

    @action updatelocationData = (data: LocationData) => {
        this.locationData = data;
        const point: number[] = [data.coords.latitude, data.coords.longitude];
        this.InsertToPointsArr(point);
        this.InsertToDatesArr(data.timestamp);
        //update ui and log
        const date = new Date(data.timestamp);
        const textlog = '(' + data.coords.latitude + ',' + data.coords.longitude + ')' + this.formatDate(date);
        console.log(textlog);
        const textUI = '(' + data.coords.latitude + ',' + data.coords.longitude + ') acc: ' + data.coords.accuracy + ' spd: ' + data.coords.speed ;
        this.updateLocationText(textUI);
    }

    @observable pointsArr: any[] = [];

    @action InsertToPointsArr = (point: number[]) => {
        this.pointsArr.push(point);
    }

    @action initPointsArr = () => {
        this.pointsArr = [];
    }

    @observable datesArr: Date[] = [];

    @action InsertToDatesArr = (timestamp: number) => {
        let date = new Date(timestamp);
        this.datesArr.push(date);
    }

    @action initDatesArr = () => {
        this.datesArr = [];
    }

    @action sendRoute = () => {
        if (this.datesArr.length < 2)
            return;
        const times = this.datesArr.map(date => this.formatDate(date));
        const coords = this.pointsArr.map(p => new firestore.GeoPoint(p[0], p[1]));
        const startTime = this.datesArr[0];
        // const endTime = new Date(Date.now());
        const name = "route-" + this.userId + '-' + this.formatDate(startTime);
        const geojsonRoute = {
            type: "Feature",
            properties: {
                name: name,
                startDate: firestore.Timestamp.fromDate(startTime),
                times: times
            },
            geometry: {
                type: "LineString",
                coordinates: coords
            }
        };
        //console.log(geojsonRoute);
        addRouteAsync(geojsonRoute, name);
        this.initDatesArr();
        this.initPointsArr();
    }

    formatDate = (dt: Date) => {
        return `${dt.getDate().toString().padStart(2, '0')}-${
            (dt.getMonth() + 1).toString().padStart(2, '0')}-${
            dt.getFullYear().toString().padStart(4, '0')},${
            dt.getHours().toString().padStart(2, '0')}:${
            dt.getMinutes().toString().padStart(2, '0')}:${
            dt.getSeconds().toString().padStart(2, '0')}`;
    }
}

export default new Store();
