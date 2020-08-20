import * as firebase from 'firebase'
import 'firebase/firestore';
import ApiKeys from '../constants/ApiKeys';
import {log, logError} from './Logger';

firebase.initializeApp(ApiKeys.FireBaseConfig);
const db = firebase.firestore();

// export const addUser = (fullname, email) => {
//     const userRef = db.collection("users").add({
//       fullname: fullname,
//       email: email
//     }); 
//   };


// use "firebase": "7.9.0", to fix bug  "7.9.1" Can't find variable: atob 
export const addRouteAsync = async (route, name) => {
  log("try to write to firestore");
  await db.collection("routes").doc(name).set(route)
  .then(function () {
    log("Document successfully written!");
  })
  .catch(function(error) {
    logError("Error writing document: ", error);
  });
};

// use "firebase": "7.9.0", to fix bug  "7.9.1" Can't find variable: atob 
export const setUserAsync = async (userData, name) => {
  await db.collection("users").doc(name).set(userData)
  .then(function () {
    log("User info successfully written!");
  })
  .catch(function(error) {
    logError("Error writing user: ", error);
  });
};

export const updateUserAsync = async (userData, name) => { 
  log("try update firestore user");
  await db.collection("users").doc(name).update(userData)
  .then(function () {
    log("User info update ok");
  })
  .catch(function(error) {
    logError("Error writing user: ", error);
  });
};

