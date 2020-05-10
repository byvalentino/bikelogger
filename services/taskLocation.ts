import * as Location from "expo-location";
import * as TaskManager from "expo-task-manager";

const LocationTaskExecutor: TaskManager.TaskManagerTaskExecutor = async (body: TaskManager.TaskManagerTaskBody) =>{
    if (body.error) {
        console.log(body.error);
        return;
    }
    if (body.data) {
        // @ts-ignore
        const { locations } = body.data;
        let lat = locations[0].coords.latitude;
        let long = locations[0].coords.longitude;
        console.log(lat +',' + long);
    }
};
// export default inject("store")(observer(LocationTaskExecutor));
export default LocationTaskExecutor;
