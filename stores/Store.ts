import { observable, action } from "mobx";

import UiStore from './uiStore';
import UserStore from './UserStore';
import TrackingStore from './TrackingStore';
import { log } from '../services/Logger';
import { USER_FACING_NOTIFICATIONS } from "expo-permissions";

export interface IStore {
    uiStore: UiStore;
    userStore: UserStore;
    trackingStore: TrackingStore;
    init: () => void; 
}

class Store implements IStore {
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
        const initUSerStore = await this.userStore.init();
        this.setStoreReady(true);
    }
    @observable isStoreReady = false;
    @action setStoreReady = (value: boolean) => {
        this.isStoreReady = value;
    }
 
}

export default new Store();
