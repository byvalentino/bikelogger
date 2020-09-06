import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, Button, Alert, Platform } from "react-native";
import * as Permissions from "expo-permissions";
import { Notifications } from 'expo';
import Constants from 'expo-constants';
import { inject, observer } from 'mobx-react';
import { useNavigation } from '@react-navigation/native';

import Input from '../components/Input';
import Colors from '../constants/colors';
import { getCurrentPositionAsync } from 'expo-location';
import { TextInput } from 'react-native-gesture-handler';
import {IStore} from '../stores/Store';

export interface Props {
  store?: IStore;
}
const UserPropsScreen: React.FC<Props> = (props: Props) => {
  const {userStore} = props.store!;
  const { userFirstName, setUserFirstName, userLastName, 
    setUserLastName, setExpoPushToken, postUserData } = userStore;
  const navigation = useNavigation();

  useEffect (() =>{
    registerForPushNotificationsAsync();
  },[])

  const firstNameHandler = (text: string) => {
    setUserFirstName(text);
  }
  const lastNameHandler = (text: string) => {
    setUserLastName(text);
  }
  const confirmedButtonHandler = () => {
      postUserData();
      navigation.goBack();
  }

  const registerForPushNotificationsAsync = async () => {
    if (Constants.isDevice) {
      const { status: existingStatus } = await Permissions.getAsync(Permissions.NOTIFICATIONS);
      let finalStatus = existingStatus;
      if (existingStatus !== 'granted') {
        const { status } = await Permissions.askAsync(Permissions.NOTIFICATIONS);
        finalStatus = status;
      }
      if (finalStatus !== 'granted') {
        Alert.alert('Failed to get push token for push notification!');
        return;
      }
      const token = await Notifications.getExpoPushTokenAsync();
      console.log(token);
      if (token !==null && token !== undefined){
        setExpoPushToken(token);
      }
    } else {
      Alert.alert('Must use physical device for Push Notifications');
    }
  
    if (Platform.OS === 'android') {
      Notifications.createChannelAndroidAsync('default', {
        name: 'default',
        sound: true,
        priority: 'max',
        vibrate: [0, 250, 250, 250],
      });
    }
    };

  return (
    <View style={styles.main}>
      <View style={styles.lineContainer}>
        <Text style={styles.text}>First Name:</Text>
        <Input
          style={styles.input}
          blurOnSubmit
          autoCapitalize='none'
          autoCorrect={false}
          maxLength={20}
          onChangeText={firstNameHandler}
          value={userFirstName}
        />
      </View>   
      <View style={styles.lineContainer}>
        <Text style={styles.text}>Last Name:</Text>
        <TextInput
          style={styles.input}
          blurOnSubmit
          autoCapitalize='none'
          autoCorrect={false}
          maxLength={20}
          onChangeText={lastNameHandler}
          value={userLastName}
        />
      </View>
           
      <View style={styles.buttonContainer}>
        <View style={styles.button}>
          <Button title="Confirm" color={Colors.primary} onPress={confirmedButtonHandler} />
        </View>
      </View>
    </View>
  );
};
export default inject("store")(observer(UserPropsScreen));

const styles = StyleSheet.create({
  main: {
    flex: 1,
    marginTop: 22
  },
  lineContainer: {
    flexDirection: 'row',
    justifyContent: 'flex-start',
    alignItems:  'center',
    paddingHorizontal: 15,
    paddingTop: 20,
  },
  input: {
    flex: 1,
    height: 40, 
    borderColor: 'gray',
    borderWidth: 1,
    marginLeft: 15,
    paddingLeft: 10,
    fontSize: 18,
  },
  text:{
    fontSize: 18,
  },
  button: {
    width: 100,
  },
  buttonContainer: {
    marginTop: 22,
    flexDirection: 'row',
    justifyContent: 'center',
    paddingHorizontal: 15,
  },
  textStyle: {
    color: "white",
    fontWeight: "bold",
  },
});