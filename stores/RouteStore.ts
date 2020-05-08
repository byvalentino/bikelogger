
import { action, observable } from 'mobx';
import RootStore from './RootStore'

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
    console.log(this.property);
  }
}
