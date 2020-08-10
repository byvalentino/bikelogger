import { decorate, observable, action } from "mobx";
import { LocationData } from "expo-location";
import { firestore } from 'firebase';
import { addRouteAsync } from '../services/FirestoreService';
import { getDistanceKm } from '../services/GeoUtils';
import { storeLocalData, getLocalData} from '../services/LocalStorage';
//import 'intl';

const INIT_REGION = {
    latitude: 31.728371,
    longitude: 35.040161,
    latitudeDelta: 1,
    longitudeDelta: 1,
}

class Store {

    // constructor(){
    //     console.log ('init Store');
    //     (async () => {
    //         const data = await this.initUserEmail();
    //     })();
    //     getLocalData('@password').then(res =>{
    //         if (res !== undefined )
    //         this.userPassword = res;
    //     });
    //     console.log ('finish init Store');
    // }

    init = async () => {
        console.log ('init Store');
        const data  = await this.initUserEmail();
        getLocalData('@password').then(res =>{
            if (res !== undefined )
            this.userPassword = res;
        });
        console.log ('finish init Store');
    }
   

    /// UI Store //////////////////
    @observable configModalVisible = false;
    @action setConfigModalVisible = (isVisable: boolean) => {
        this.configModalVisible = isVisable;
    }
    
    /// User Store //////////////////
    @observable userId = '111';
    @action updateUserId = (uid: string) => {
        this.userId = uid;
    }
    @observable tempMail = '';
    @action setTempMail = (value: string) => {
        this.tempMail = value;
    }
    @observable userEmail = '';
    @action setUserEmail = (value: string) => {
        this.userEmail = value;
        storeLocalData('@email', value);
    }
    initUserEmail = async () => {
        console.log ('init User Email');
        const email = await getLocalData('@email');
        if (email !== undefined){
            //this.userEmail = email;
            this.setUserEmail(email);
            this.setTempMail(email);
            console.log ('get', email);
        }
    }

    @observable userPassword = '';
    @action setUserPassword = (value: string) => {
        this.userEmail = value;
        storeLocalData('@password', value);
    }

    /// Tracking Store //////////////////

    //tracking Time Interval in sec
    @observable trackingTimeInterval = 3;
    @action setTrackingTimeInterval = (timeInterval: number) => {
        this.trackingTimeInterval = timeInterval;
    }

    // is gps location Tracking is on
    @observable isTracking = false;
    @action updateIsTracking = (isTrack: boolean) => {
        this.isTracking = isTrack;
    }

    // region in the map 
    @observable region: any = INIT_REGION;
    @action updateRegion = (newRegion: any) => {
        this.region = newRegion;
    }

    // observable to statusText
    @observable statusText = 'Not Tracking';
    // action to update statusText
    @action updateStatusText = (text: string) => {
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
    // action to update accuracy
    @action setAcuracy = (acc: number) => {
        this.accuracy = acc;
    }
    // observable to speed
    @observable speed = 0;
    // action to update speed
    @action setSpeed = (spd: number) => {
        this.speed = spd;
    }
    @observable altitude = 0;
    // action to update speed
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
    @action setRouteDistance = (point1:number[]) => {
        if (this.pointsArr.length > 1){
            let point2 = this.pointsArr[this.pointsArr.length - 2];
            let addedDist =  getDistanceKm(point1[0],point1[1],point2[0],point2[1]);
            this.routeDistance += addedDist;
        }

    }

    // observable for locationData - current location
    @observable locationData: LocationData | null = null;
    @action updatelocationData = (data: LocationData) => {
        this.locationData = data;
        const point: number[] = [data.coords.latitude, data.coords.longitude];
        this.InsertToPointsArr(point);
        this.InsertToDatesArr(data.timestamp);
        this.insertToAltArr(data.coords.altitude);
        this.setRouteDistance(point);
        //update ui and log
        const date = new Date(data.timestamp);
        const textlog = '(' + data.coords.latitude + ',' + data.coords.longitude + ')' + this.formatDate(date);
        console.log(textlog);
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
        this.updateRegion(region);
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
        const name = "route-" + this.userId + '-' + this.formatDate(startTime);
        const geojsonRoute = this.createGeoJsonRoute(name, startTime, this.routeDistance);
        //console.log(geojsonRoute);
        if (this.isSendRoute && geojsonRoute !== null) {
            addRouteAsync(geojsonRoute, name);
        }
        this.initDatesArr();
        this.initPointsArr();
        this.initAltArr();
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
