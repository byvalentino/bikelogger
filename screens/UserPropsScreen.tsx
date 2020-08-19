import React, { useState } from 'react';
import { View, Text, StyleSheet, Button, Keyboard, Alert } from "react-native";
import Constants from 'expo-constants';
import { inject, observer } from 'mobx-react';
import { useNavigation } from '@react-navigation/native';

import Input from '../components/Input';
import Colors from '../constants/colors';
import { getCurrentPositionAsync } from 'expo-location';
import { TextInput } from 'react-native-gesture-handler';

export interface Props {
  store?: any;
}
const UserPropsScreen: React.FC<Props> = (props: Props) => {
  const { userFirstName, setUserFirstName, userLastName, setUserLastName, sendUserData } = props.store;
  const navigation = useNavigation();
  const firstNameHandler = (text: string) => {
    setUserFirstName(text);
  }
  const lastNameHandler = (text: string) => {
    setUserLastName(text);
  }
  const confirmedButtonHandler = () => {
      sendUserData();
      navigation.goBack();
  }
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
  button: {
    width: 100,
  },
  input: {
    width: 250,
    height: 40, 
    borderColor: 'gray',
    borderWidth: 1,
    marginLeft: 15,
    fontSize: 18,
  },
  text:{
    fontSize: 18,
  },
  lineContainer: {
    flexDirection: 'row',
    justifyContent: 'flex-start',
    paddingHorizontal: 15,
    paddingTop: 20,
  },
  buttonContainer: {
    marginTop: 22,
    flexDirection: 'row',
    width: '80%',
    justifyContent: 'center',
    paddingHorizontal: 15,
  },
  textStyle: {
    color: "white",
    fontWeight: "bold",
  },
});