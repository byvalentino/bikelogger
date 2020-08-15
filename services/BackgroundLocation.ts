import { requestPermissionsAsync, startLocationUpdatesAsync, stopLocationUpdatesAsync } from "expo-location";
import Store from '../stores/Store';
import { log } from '../services/Logger';

const LOCATION_TASK_NAME = "background-location-task";

const startLocationTaskAsync = async () => {
    try {
        log("startLocationTaskAsync");
        await startLocationUpdatesAsync(LOCATION_TASK_NAME, {
            // @ts-ignore
            enableHighAccuracy: true,
            distanceInterval: 1,
            timeInterval: Store.trackingTimeInterval * 1000,
            foregroundService: {
                notificationTitle: 'Bike Location',
                notificationBody: 'Enable this to make GPS alive'
            },
        });
    } catch (error) {
        log(error.message);
        throw(error);
    }
};

export const startGetLocationAsync = async () => {
    log("startGetLocationAsync - background");
    // Asking for device location permission
    const { status } = await requestPermissionsAsync()
    if (status === "granted") {
        await startLocationTaskAsync();
        Store.setStatusText('Tracking...');
        Store.setIsTracking(true);
        log("start locating");
    } else {
        Store.setIsTracking(false);
        Store.setStatusText('Locations services needed');
    }
}

export const stopGetLocationAsync = async () => {
    await stopLocationUpdatesAsync(LOCATION_TASK_NAME);
    Store.setStatusText('Not Tracking');
    Store.setIsTracking(false);
    Store.sendRoute();
}
export default startLocationTaskAsync;