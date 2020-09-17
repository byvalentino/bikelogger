import { Accelerometer } from 'expo-sensors';
import Store from '../stores/Store';


var globalAccLisnter: any = null;
var globalAccelerometerArr: any[] = [];
var globalAccStartTime: any;

export const subscribeAcc=  () => {
    console.log('subscribeAcc');
    globalAccStartTime = Date.now();
    globalAccelerometerArr = [];
    globalAccLisnter = Accelerometer.addListener(accelerometerData => {
      globalAccelerometerArr.push(accelerometerData);
    });
}

export const unsubscribeAcc = () => {
    console.log('unsubscribeAcc');
    globalAccLisnter && globalAccLisnter.remove();
    globalAccLisnter = null;
    const {trackingStore} = Store;
    const stopTime = Date.now();
    trackingStore.setAccMsg (globalAccelerometerArr.length +": " + (stopTime-globalAccStartTime))
    console.log(globalAccelerometerArr);
    console.log(globalAccelerometerArr.length);
    console.log(stopTime-globalAccStartTime);
  };

  export default globalAccLisnter;