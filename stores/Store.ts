import { observable, action } from "mobx";


import UiStore from './uiStore';
import UserStore from './UserStore';
import TrackingStore from './TrackingStore';
import { storeLocalData, getLocalData } from '../services/LocalStorage';
import { log } from '../services/Logger';
import { USER_FACING_NOTIFICATIONS } from "expo-permissions";
//import 'intl';



class Store {
    constructor() {
        this.uiStore = new UiStore(this);
        this.userStore = new UserStore(this);
        this.trackingStore = new TrackingStore(this);
    }
    uiStore: UiStore;
    userStore: UserStore;
    trackingStore: TrackingStore;

    init = async () => {
        log('init Store');
        const dataUserToken = await this.userStore.initUserToken();
        const data = await this.userStore.initUserEmail();
        getLocalData('@password').then(res => {
            if (res !== undefined)
                this.userStore.userPassword = res;
        });
        this.setStoreReady(true);
    }
    @observable isStoreReady = false;
    @action setStoreReady = (value: boolean) => {
        this.isStoreReady = value;
    }
 
}

export default new Store();
