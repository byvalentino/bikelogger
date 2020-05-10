import { TaskManagerTaskExecutor, TaskManagerTaskBody} from "expo-task-manager";
import Store from '../stores/Store';

const LocationTaskExecutor: TaskManagerTaskExecutor = async (body: TaskManagerTaskBody) =>{
    if (body.error) {
        console.log(body.error);
        return;
    }
    if (body.data) {
        // @ts-ignore
        const { locations } = body.data;
        const lat = locations[0].coords.latitude;
        const long = locations[0].coords.longitude;
        const date = new Date(locations[0].timestamp);
        const text = lat +',' + long +',' + date.toLocaleString();
        console.log (text);
        Store.text = text;
    }
};
//export default inject("store")(observer(LocationTaskExecutor));
export default LocationTaskExecutor;
