import { TaskManagerTaskExecutor, TaskManagerTaskBody} from "expo-task-manager";
import Store from '../stores/Store';
import {log} from '../services/Logger';

const LocationTaskExecutor: TaskManagerTaskExecutor = async (body: TaskManagerTaskBody) =>{
    if (body.error) {
        log(body.error);
        return;
    }
    if (body.data) {
        // @ts-ignore
        const { locations } = body.data;
        const locationData  = locations[0];
        Store.updatelocationData(locationData);
    }
};
export default LocationTaskExecutor;
