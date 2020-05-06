import * as firebase from 'firebase'
import 'firebase/firestore';
import ApyKeys from '../constants/ApyKeys';

firebase.initializeApp(ApyKeys.FireBaseConfig);
const db = firebase.firestore();

export const addUser = (fullname, email) => {
    const userRef = db.collection("users").add({
      fullname: fullname,
      email: email
    }); 
  };