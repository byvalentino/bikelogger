import { decorate, observable, action } from "mobx";

class Store {
    // observable to save search query
    @observable text = 'mobx test!';

    // action to update text
    @action updateText = (text: string) => {
        this.text = text;
    }
}

// // another way to decorate variables with observable
// decorate(Store, {
//   text: observable,
//   updateText: action,
//   data: observable,
//   searchImage: action,
//   setData: action,
// });

// export class
export default new Store();