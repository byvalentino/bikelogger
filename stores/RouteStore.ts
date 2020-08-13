
import { action, observable } from 'mobx';
import RootStore from './RootStore'
import {log} from '../services/Logger';

export default class RouteStore {
  appInitialized = false

  constructor(rootStore: RootStore) {
    // init app data
    this.rootStore = rootStore;
  }

  rootStore: RootStore;

  @observable property = '';

  @action setProperty(newProperty: string) {
    this.property = newProperty;
    log(this.property);
  }
}
