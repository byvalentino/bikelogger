import * as firebase from 'firebase'
import 'firebase/firestore';
import ApiKeys from '../constants/ApiKeys';
import { log, logError } from './Logger';

firebase.initializeApp(ApiKeys.FireBaseConfig);
const db = firebase.firestore();
// use "firebase": "7.9.0", to fix bug  "7.9.1" Can't find variable: atob 

export const addRouteAsync = async (data, docPath) => {
  log("try to write to firestore");
  await setAsync(data, 'routes', docPath, "Document successfully written!");
};

export const readUserAsync = async (docPath) => {
  const data = await readAsync("users", docPath);
  return data;
};

export const setUserAsync = async (data, docPath) => {
  await setAsync(data, 'users', docPath, "User info successfully written!");
};

export const updateUserAsync = async (data, docPath) => {
  await updateAsync(data, "users", docPath);
};

/////////////////

const setAsync = async (data, collName, docPath, sucessmsg = '') => {
  await db.collection(collName).doc(docPath).set(data)
    .then(function () {
      if (sucessmsg !== '')
        log(docPath);
        log(sucessmsg);
    })
    .catch(function (error) {
      logError("Error writing document: ", error);
    });
};

const updateAsync = async (data, collName, docPath, sucessmsg = '') => {
  await db.collection(collName).doc(docPath).update(data)
    .then(function () {
      if (sucessmsg !== '')
        log(sucessmsg);
    })
    .catch(function (error) {
      logError("Error updating doc: ", error);
    });
};

const readAsync = async (collName, docPath) => {
  let doc;
  try {
    doc = await db.collection(collName).doc(docPath).get();
    if (doc.exists) {
      let data = doc.data();
      //let data1 = cahngeFireStoreToJSON(data);
      return data;
    } else {
      log("No such document: " + docPath);
      return null;
    }
  } catch (error) {
    logError("Error geeting document: ", error);
    return null;
  }
};
