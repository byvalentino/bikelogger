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
            timeInterval: Store.trackingStore.trackingTimeInterval * 1000,
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
    const {trackingStore} = Store;
    // Asking for device location permission
    const { status } = await requestPermissionsAsync()
    if (status === "granted") {
        await startLocationTaskAsync();
        trackingStore.setStatusText('Tracking...');
        trackingStore.setIsTracking(true);
        log("start locating");
    } else {
        trackingStore.setIsTracking(false);
        trackingStore.setStatusText('Locations services needed');
    }
}

export const stopGetLocationAsync = async () => {
    const {trackingStore} = Store;
    await stopLocationUpdatesAsync(LOCATION_TASK_NAME);
    trackingStore.setStatusText('Not Tracking');
    trackingStore.setIsTracking(false);
    trackingStore.sendRoute();
}
export default startLocationTaskAsync;