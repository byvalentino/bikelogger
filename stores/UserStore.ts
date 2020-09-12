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

    init = async () => {
        const dataUserToken = await this.initUserToken();
        const data = await this.initUserEmail();
        const pass = await this.initUserPassword();
    }
    /// User Store ////////////////////////////////////////////////////////////////////
    @observable userToken = '';
    @action setUserToken = (value: string) => {
        this.userToken = value;
        storeLocalData('@userToken', value);
    }
    initUserToken = async () => {
        const val = await getLocalData('@userToken');
        console.log('init token', val);
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
        this.userPassword = value;
        storeLocalData('@password', value);
    }
    @action initUserPassword = async () => {
        const email = await getLocalData('@password').then(res => {
            if (res !== undefined)
                this.userPassword = res;
        });
    }

    /// @observable signInState:string  = 'RESTORE_TOKEN';
    @observable isLoadingToken: boolean = true;
    @observable isSignout: boolean = false;
    @action setSignInState = (signInData: any) => {
        // this.setSignInState(actionType.type)
        switch (signInData.type) {
            case 'RESTORE_TOKEN':
                this.isLoadingToken = false;
                break;
            case 'SIGN_IN':
                this.setUserToken(signInData.token);
                this.isSignout = false;
                this.setUserEmail(signInData.email);
                this.setUserPassword(signInData.password);
                this.updateUserLastLogin();
                this.fetchUserData();
                break;
            case 'SIGN_UP':
                    this.setUserToken(signInData.token);
                    this.isSignout = false;
                    this.setUserEmail(signInData.email);
                    this.setUserPassword(signInData.password);
                    this.initUserDataToCloud();
                    break;    
            case 'SIGN_OUT':
                this.setUserToken('');
                this.isSignout = true;
                break;
        }
    };

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
    // get data on user, like first name, from server 
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
        log('user token: ' + this.userToken);
        const userData = { last_logged_in: fDate };
        this.updateUserToCloud(userData);
    }
    @action updateUserExpoPushToken = () => {
        const userData = {
            push_token: this.expoPushToken,
        };
        this.updateUserToCloud(userData);
    }
    @action initUserDataToCloud = () => {
        const DateNow = new Date();
        const fDate = this.formatDate(DateNow);
        const userData = {
            created_at: fDate,
            email: this.userEmail,
            first_name: this.userFirstName,
            last_name: this.userLastName,
            locale: 'en',
            last_logged_in: fDate,
            push_token: this.expoPushToken,
        };
        if (this.userToken !== '') {
            const name = "user-" + this.userToken;
            setUserAsync(userData, name);
        }
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
