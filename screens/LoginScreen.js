import React, { useState, useEffect } from 'react';
import { View, Text, Button, StyleSheet, } from 'react-native';
import * as firebase from 'firebase'
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import Colors from '../constants/colors';
import Store from '../stores/Store';
import Input from '../components/Input';

export interface Props {
}
//const LoginScreen: React.FC<Props> = (props:Props, navigation : any) => {

function LoginScreen({ navigation }) {
    useEffect(() => {
        Store.init();
    });

    const [email, setEmail] = useState('reshef.dror@gmail.com');
    const [password, setPassword] = useState('');
    const [myState, setMyState] = useState(
        {
            error: '',
            loading: false,
        }
    );
    console.log('email', email);
    const emailInputHandler = (text) => {
        setEmail(text);
        //Store.setTempMail(text)
    }
    const passwordInputHandler = (text) => {
        setPassword(text);
    }
    const onLoginPress = () => {
        firebase.auth().signInWithEmailAndPassword(email, password)
            .then(() => {
                // alert(firebase.auth().currentUser.uid);
                // console.log(firebase.auth().currentUser.uid);
                Store.setUserEmail(email);
                Store.setUserPassword(password);
                Store.updateUserId(firebase.auth().currentUser.uid);
                setMyState(prev => ({ ...prev, error: '', loading: false, }));
                navigation.navigate('Bike Tracker')
            })
            .catch((error) => {
                let errorCode = error.code;
                let errorMessage = error.message;
                if (errorCode === 'auth/wrong-password') {
                    alert('Wrong password.');
                } else {
                    alert(errorMessage);
                }
                console.log(error);
                setMyState(prev => ({ ...prev, error: 'Authentication Failed', loading: false, }));
            })
    }
    const onSignUpPress = () => {
        firebase.auth().createUserWithEmailAndPassword(email, password)
            .then(() => {
                setMyState(prev => ({ ...prev, error: '', loading: false, }));
                navigation.navigate('Main')
            })
            .catch(() => {
                setMyState(prev => ({ ...prev, error: 'Authentication Failed', loading: false, }));
            })
    }
    const renderButtonOrLoading = () => {
        if (myState.loading) {
            return <Text> Loading</Text>
        }
        else {
            return <View style={styles.buttonContainer}>
                <View style={styles.button}>
                    <Button title='SignUp' color={Colors.primary} onPress={() => { onSignUpPress() }} />
                </View>
                <View style={styles.button}>
                    <Button title='Login' color={Colors.primary} onPress={() => { onLoginPress() }} />
                </View>
            </View>
        }

    }
    return (
        <View style={styles.view}>
            <Text style={styles.text}>Email:</Text>
            <Input
                style={styles.input}
                blurOnSubmit
                autoCapitalize='none'
                autoCorrect={false}
                maxLength={50}
                onChangeText={emailInputHandler}
                value={email}
            />
            <Text style={styles.text}>Password:</Text>
            <Input
                style={styles.input}
                blurOnSubmit
                autoCapitalize='none'
                autoCorrect={false}
                maxLength={50}
                onChangeText={passwordInputHandler}
                value={password}
            />
            <Text>{myState.error}</Text>
            {renderButtonOrLoading()}
        </View>
    );
}

const styles = StyleSheet.create({
    view: {
        padding: 10,
        width: '100%',
    },
    input: {
        width: 200,
        textAlign: 'left',
    },
    buttonContainer: {
        flexDirection: 'row',
        justifyContent: 'space-around',
    },
    button: {
        width: 150,
    },
    text: {
        fontSize: 14,
    }
});
export default LoginScreen;