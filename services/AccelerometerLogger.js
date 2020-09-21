import { Accelerometer } from 'expo-sensors';
import Store from '../stores/Store';


global.AccLisnter = null;
global.AccelerometerArr = [];
global.AccStartTime;

export const subscribeAcc=  () => {
    console.log('subscribeAcc');
    global.AccStartTime = Date.now();
    global.AccelerometerArr = [];
    global.AccLisnter = Accelerometer.addListener(accelerometerData => {
      global.AccelerometerArr.push(accelerometerData);
    });
}

export const unsubscribeAcc = () => {
    console.log('unsubscribeAcc');
    global.AccLisnter && global.AccLisnter.remove();
    global.AccLisnter = null;
    const {trackingStore} = Store;
    const stopTime = Date.now();
    trackingStore.setAccMsg (global.AccelerometerArr.length +": " + (stopTime-global.AccStartTime))
    console.log(global.AccelerometerArr);
    console.log(global.AccelerometerArr.length);
    console.log(stopTime-global.AccStartTime);
  };

  export default subscribeAcc;