import { TaskManagerTaskExecutor, TaskManagerTaskBody} from "expo-task-manager";
import Store from '../stores/Store';
import {log} from '../services/Logger';

// task that will run when location is found
const LocationTaskExecutor: TaskManagerTaskExecutor = async (body: TaskManagerTaskBody) =>{
    if (body.error) {
        log(body.error);
        return;
    }
    if (body.data) {
        const { trackingStore } = Store;
        // @ts-ignore
        const { locations } = body.data;
        const locationData  = locations[0];
        trackingStore.setLocationData(locationData);
    }
};
export default LocationTaskExecutor;
