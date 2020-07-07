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


// use "firebase": "7.9.0", to fix bug  "7.9.1" Can't find variable: atob 
export const addRouteAsync = async (route, name) => {
  console.log("try to write to firestore");
  await db.collection("routes").doc(name).set(route)
  .then(function () {
    console.log("Document successfully written!");
  })
  .catch(function(error) {
    console.error("Error writing document: ", error);
  });
};
