
import RouteStore from './RouteStore';

export default class RootStore {
    appInitialized = false;

    constructor() {
      this.routeStore = new RouteStore(this);
    }

    routeStore: RouteStore;

}
