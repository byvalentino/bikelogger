import * as firebase from 'firebase'
import 'firebase/firestore';
import ApyKeys from '../constants/ApyKeys';

firebase.initializeApp(ApyKeys.FireBaseConfig);
const db = firebase.firestore();

// export const addUser = (fullname, email) => {
//     const userRef = db.collection("users").add({
//       fullname: fullname,
//       email: email
//     }); 
//   };

export const addRoute = (route) => {
  db.collection("routes").doc("one").set(route).then(function () {
    console.log("Document successfully written!");
  });
};
