import { observable, action } from 'mobx';
import Store from './Store';
import { storeLocalData, getLocalData } from '../services/LocalStorage';
import { readUserAsync, setUserAsync, updateUserAsync } from '../services/FirestoreService';
import { log } from '../services/Logger';

export default class UserStore {
    appInitialized = false

    constructor(rootStore: any) {
        // init app data
        this.rootStore = rootStore;
    }
    rootStore: any;

    /// User Store ////////////////////////////////////////////////////////////////////
    @observable userToken = '';
    @action setUserToken = (value: string) => {
        this.userToken = value;
        storeLocalData('@userToken', value);
    }
    initUserToken = async () => {
        const val = await getLocalData('@userToken');
        if (val !== undefined) {
            this.setUserToken(val);
        }
    }
    @observable userEmail = '';
    @action setUserEmail = (value: string) => {
        this.userEmail = value;
        storeLocalData('@email', value);
    }
    initUserEmail = async () => {
        const email = await getLocalData('@email');
        if (email !== undefined) {
            this.setUserEmail(email);
        }
    }

    @observable userPassword = '';
    @action setUserPassword = (value: string) => {
        this.userEmail = value;
        storeLocalData('@password', value);
    }
    @observable userFirstName = '';
    @action setUserFirstName = (value: string) => {
        this.userFirstName = value;
    }
    @observable userLastName = '';
    @action setUserLastName = (value: string) => {
        this.userLastName = value;
    }

    @observable expoPushToken = '';
    @action setExpoPushToken = (value: string, sendToCloud: boolean = true) => {
        this.expoPushToken = value;
        if (sendToCloud)
            this.updateUserExpoPushToken();
    }


    @action postUserData = () => {
        //const name = "user-" + this.userToken;
        const DateNow = new Date();
        const fDate = this.formatDate(DateNow);
        const userData = {
            first_name: this.userFirstName,
            last_name: this.userLastName,
        };
        //log(geojsonRoute);
        if (userData !== null) {
            this.updateUserToCloud(userData);
        }
    }
    @action fetchUserData = () => {
        if (this.userToken !== '') {
            const name = "user-" + this.userToken;
            readUserAsync(name)
                .then((data) => {
                    if (data) {
                        this.setUserFirstName(data.first_name);
                        this.setUserLastName(data.last_name);
                        this.setExpoPushToken(data.push_token, false);
                    }

                })
        }
    }
    @action updateUserLastLogin = () => {
        const DateNow = new Date();
        const fDate = this.formatDate(DateNow);
        log('logged: ' + fDate);
        // console.log("user-" + this.userToken);
        const userData = { last_logged_in: fDate };
        this.updateUserToCloud(userData);
    }
    @action updateUserExpoPushToken = () => {
        const userData = {
            push_token: this.expoPushToken,
        };
        this.updateUserToCloud(userData);
    }
    updateUserToCloud = (userData: any) => {
        if (this.userToken !== '') {
            const name = "user-" + this.userToken;
            updateUserAsync(userData, name);
        }
    }
    formatDate = (dt: Date) => {
        return `${dt.getFullYear().toString().padStart(4, '0')}-${
            (dt.getMonth() + 1).toString().padStart(2, '0')}-${
            dt.getDate().toString().padStart(2, '0')},${
            dt.getHours().toString().padStart(2, '0')}:${
            dt.getMinutes().toString().padStart(2, '0')}:${
            dt.getSeconds().toString().padStart(2, '0')}`;
    }
}