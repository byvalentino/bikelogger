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

    // observable to save search query
    @observable text = '';

    // action to update text
    @action updateText = (text: string) => {
        this.text = text;
    }
    // observable for locationData - current location
    @observable locationData: LocationData | null = null;

    @action updatelocationData = (data: LocationData) => {
        this.locationData = data;
        const point: number[] = [data.coords.latitude, data.coords.longitude];
        this.InsertToPointsArr(point);
        this.InsertToDatesArr(data.timestamp);
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
        console.log(date.toLocaleString());
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
