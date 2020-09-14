import React, { useState } from 'react';
import { View, Text, StyleSheet, Button, Keyboard, Alert } from "react-native";
import Constants from 'expo-constants';
import { inject, observer } from 'mobx-react';
import { useNavigation } from '@react-navigation/native';

import {IStore} from '../stores/Store';
import Input from '../components/Input';
import Colors from '../constants/colors';

export interface Props {
  store?: IStore;
}

const ConfigScreen: React.FC<Props> = (props: Props) => {
  const { trackingStore } = props.store!;
  const { userStore } = props.store!;
  const { trackingTimeInterval, setTrackingTimeInterval } = trackingStore;
  const navigation = useNavigation();
  const [enteredText, setEnteredText] = useState(trackingTimeInterval.toString());
  const numberInputHandler = (text: string) => {
    setEnteredText(text.replace(/[^0-9]/g, ''));
  }
  const cacnelButtonHandler = () => {
    setEnteredText(trackingTimeInterval.toString());
    navigation.goBack();
  }
  const onForgroundPress = () => {
    navigation.navigate('MapForground');
  }
  const onUserPropsPress = () => {
    navigation.navigate('UserProps');
  }
  const onLogOutPress = () => {
    userStore.setSignInState({ type: 'SIGN_OUT'});
  }
  const confirmedButtonHandler = () => {
    const intervalNumnber = parseInt(enteredText)
    if (isNaN(intervalNumnber) || intervalNumnber <= 0 || intervalNumnber > 30) {
      Alert.alert(
        'Invalid Interval',
        'Interval has to be between 1 and 30 sec.',
        [{ text: 'Okay', style: 'destructive' }]
      );
      setEnteredText(trackingTimeInterval.toString());
      return;
    }
    else {
      setTrackingTimeInterval(intervalNumnber);
      navigation.goBack();
    }
  }
  return (
    <View style={styles.main}>
      <View style={styles.lineContainer}>
        <Text style={styles.text} >Version:</Text>
        <Text style={styles.text}>{Constants.manifest.version}</Text>
      </View>
      <View style={styles.lineContainer}>
        <Text style={styles.text}>Time Interval:</Text>
        <Input
          style={styles.input}
          blurOnSubmit
          autoCapitalize='none'
          autoCorrect={false}
          keyboardType='number-pad'
          maxLength={2}
          onChangeText={numberInputHandler}
          value={enteredText}
        />
      </View>
      <View style={styles.lineContainer}>
        <View style={styles.button}>
          <Button title='Forground' color={Colors.primary} onPress={onForgroundPress} />
        </View>
      </View>
      <View style={styles.lineContainer}>
        <View style={styles.button}>
          <Button title='User Props' color={Colors.primary} onPress={onUserPropsPress} />
        </View>
      </View>
      <View style={styles.lineContainer}>
        <View style={styles.button}>
          <Button title='Log out' color={Colors.primary} onPress={onLogOutPress} />
        </View>
      </View>
      <View style={styles.buttonContainer}>
        <View style={styles.button}>
          <Button title="Cancel" color={Colors.secondary} onPress={cacnelButtonHandler} />
        </View>
        <View style={styles.button}>
          <Button title="Confirm" color={Colors.primary} onPress={confirmedButtonHandler} />
        </View>
      </View>
    </View>
  );
};
export default inject("store")(observer(ConfigScreen));

const styles = StyleSheet.create({
  main: {
    flex: 1,
    marginTop: 22
  },
  button: {
    width: 120,
  },
  input: {
    width: 50,
    height: 40, 
    borderColor: 'gray',
    borderWidth: 1,
    paddingLeft: 10,
    marginLeft: 15,
    fontSize: 18,
  },
  text:{
    marginLeft: 15,
    fontSize: 18,
  },
  lineContainer: {
    flexDirection: 'row',
    justifyContent: 'flex-start',
    alignItems:  'center',
    paddingHorizontal: 15,
    paddingTop: 20,
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
    textAlign: "center"
  },
});