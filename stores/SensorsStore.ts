import { observable, action } from "mobx";

interface IAcceleromerReading {
    x: number;
    y: number;
    z: number;
    time: number;
}

class AccelerometerReading implements IAcceleromerReading {
    @observable x: number;
    @observable y: number;
    @observable z: number;
    @observable time: number;
    constructor(x: number, y: number, z: number) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.time = Date.now();
    }
}

class GyroReading implements IAcceleromerReading {
    @observable x: number;
    @observable y: number;
    @observable z: number;
    @observable time: number;
    constructor(x: number, y: number, z: number) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.time = Date.now();
    }
}

interface ISensorsInterval {
    startTime: number;
    stopTime: number;
    active: boolean;
    acceleromerArr: Array<IAcceleromerReading>;
    gyroArr: Array<IAcceleromerReading>;
}
export default class SensorsInterval implements ISensorsInterval {
    @observable startTime: number;
    @observable stopTime: number;
    @observable active: boolean;
    @observable acceleromerArr: Array<IAcceleromerReading>;
    @observable gyroArr: Array<IAcceleromerReading> = [];
    constructor() {
        this.startTime = Date.now();
        this.stopTime = Date.now();
        this.active = true;
        this.acceleromerArr = [];
        this.gyroArr = [];
    }

    @action addAcceleromerReading = (x: number, y: number, z: number) => {
        this.acceleromerArr.push(new AccelerometerReading(x, y, z));
    }
    @action addGyroReading = (x: number, y: number, z: number) => {
        this.gyroArr.push(new GyroReading(x, y, z));
    }
    @action stopInterval = () => {
        console.log('stopInterval')
        this.active = false;
        this.stopTime = Date.now();
    }
}
// export default class SensorsStore {
//     @observable currentSensoersInterval :SensorsInterval | null =null;
//     @observable sensoersIntervalArr: SensorsInterval[] = [];
//     @action startSensorInerval = () => {
//         this.currentSensoersInterval = new SensorsInterval(); 
//     }
//     @action StopSensorInerval = () => {
//         sensoersIntervalArr:
//         this.currentSensoersInterval = new SensorsInterval(); 
//     }
// }



// autorun(() =>{
//     console.log(store.todos[0])
//     console.log(store.filter)
// })

//export default store