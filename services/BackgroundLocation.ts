import { requestPermissionsAsync, startLocationUpdatesAsync, stopLocationUpdatesAsync }  from "expo-location";
import Store from '../stores/Store';

const LOCATION_TASK_NAME = "background-location-task";

const startLocationTaskAsync = async () => {
    await startLocationUpdatesAsync(LOCATION_TASK_NAME, {
         // @ts-ignore
        enableHighAccuracy: true,
        distanceInterval: 1,
        timeInterval: 5000,
        foregroundService: {
            notificationTitle: 'Bike Location',
            notificationBody: 'Enable this to make GPS alive'
        },
    });
};

export const startGetLocationAsync = async () => {
    // Asking for device location permission
    const { status } = await requestPermissionsAsync()
    if (status === "granted") {
        await startLocationTaskAsync();
        Store.updateStatusText('Locating...');
        Store.updateIsTracking(true);
        // console.log("start locating")
    } else {
        Store.updateIsTracking(false);
        Store.updateStatusText('Locations services needed');
    }
}

export const stopGetLocationAsync = async () => {
    await stopLocationUpdatesAsync(LOCATION_TASK_NAME);
    Store.updateStatusText('Not Locating');
    Store.updateIsTracking(false);
    Store.sendRoute();
}
export default startLocationTaskAsync;