import React, { useState, useEffect } from 'react';
import { View, Text, Button, StyleSheet, Alert} from 'react-native';
import * as firebase from 'firebase'
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { inject, observer } from 'mobx-react';
import Store from '../stores/Store';
import Input from '../components/Input';
import {log} from '../services/Logger';
// import { set } from 'mobx';
import Colors from '../constants/colors';
import AuthContext from '../constants/MyContext';
import {IStore} from '../stores/Store';

export interface Props {
  store?: IStore;
}

const LoginScreen: React.FC<Props> = (props: Props) => {    
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [myState, setMyState] = useState(
        {
            error: '',
            loading: false,
        }
    );
    const {userStore} = Store;
    const { signIn, signUp} = React.useContext(AuthContext);

    useEffect(() => {
        if (Store.isStoreReady)
        {
            setEmail(userStore.userEmail)
        }
    },[Store.isStoreReady])
    
    const emailInputHandler = (text:string) => {
        setEmail(text);
        //Store.setTempMail(text)
    }
    const passwordInputHandler = (text:string) => {
        setPassword(text);
    }
    const onLoginPress = () => {
        tryLogin('MainScreen');
    }
    const tryLogin = (navPage: string) =>{
        firebase.auth().signInWithEmailAndPassword(email, password)
        .then(() => {
                let userToken = firebase.auth().currentUser!.uid;
                setMyState(prev => ({ ...prev, error: '', loading: false, }));
                signIn({ token: userToken, email: email, password: password });
        })
        .catch((error) => {
            let errorCode = error.code;
            let errorMessage = error.message;
            if (errorCode === 'auth/wrong-password') {
                Alert.alert("Login Failed",'Wrong password.');
            } else {
                Alert.alert("Login Failed",errorMessage);
            }
            log(error);
            setMyState(prev => ({ ...prev, error: 'Authentication Failed', loading: false, }));
        })
    }
    const onSignUpPress = () => {
        firebase.auth().createUserWithEmailAndPassword(email, password)
            .then(() => {
                let userToken = firebase.auth().currentUser!.uid;
                //console.log(userToken);
                setMyState(prev => ({ ...prev, error: '', loading: false, }));
                signUp({ token: userToken, email: email, password: password });
            })
            .catch((error) => {
                let errorMessage = error.message;
                setMyState(prev => ({ ...prev, error: errorMessage, loading: false, }));
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
        marginTop: 15,
        flexDirection: 'row',
        justifyContent: 'space-around',
    },
    button: {
        width: 120,
    },
    text: {
        fontSize: 14,
    }
});
export default inject("store")(observer(LoginScreen));