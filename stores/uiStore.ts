import { observable, action } from 'mobx';
import Store from './Store';

export default class UiStore {
    appInitialized = false

    constructor(rootStore: any) {
      // init app data
      this.rootStore = rootStore;
    }
    rootStore: any;

    /// UI Store /////////////////////////////////////////////////////////////////////////
    @observable configModalVisible = false;
    @action setConfigModalVisible = (isVisable: boolean) => {
        this.configModalVisible = isVisable;
    }
    @observable accelerometerModalVisable = false;
    @action setAccelerometerModalVisable = (isVisable: boolean) => {
        this.accelerometerModalVisable = isVisable;
    }

    @observable loggerModalVisable = false;
    @action setLoggerModalVisable = (isVisable: boolean) => {
        this.loggerModalVisable = isVisable;
    }
}
