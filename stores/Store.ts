import { decorate, observable, action } from "mobx";
import { LocationData } from "expo-location";
import * as firebase from 'firebase';

class Store {
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
        let { timestamp } = data;
        let date = new Date(timestamp);
        console.log(date.toLocaleString());
        //console.log(this.locationData.coords);
        const point: number[] = [data.coords.latitude, data.coords.longitude];
        this.InsertToPointsArr(point);
        
    }

    @observable pointsArr:  any[] = [];

    @action InsertToPointsArr =(point : number[]) => {
        this.pointsArr.push(point);
    }

    @action sendRoute = () => {
        const  geojsonRoute = {
            type: "Feature",
            properties: {
                name: "Route1",
                startDate: firebase.firestore.Timestamp.fromDate(new Date("December 10, 1815")),
                times: [
                    "Fri May  8 06:29:50 2020",
                    "Fri May  8 06:29:55 2020"
                ]
            },
            geometry: {
                type: "LineString",
                coordinates: [
                  "[100.0, 0.0]",
                  "[101.0, 1.0]"  
                ]
            }
          };
        //console.log(geojsonRoute);
        // console.log(this.pointsArr); 
        //addRoute(geojsonRoute);
    }
}

export default new Store();
