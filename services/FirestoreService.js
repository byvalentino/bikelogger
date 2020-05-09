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

export const addRouteAsync = async (route, name) => {
  await db.collection("routes").doc(name).set(route).then(function () {
    console.log("Document successfully written!");
  });
};
