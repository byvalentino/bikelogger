import React ,{useState} from 'react';
import {  View, Text, Button, StyleSheet, } from 'react-native';
import * as firebase from 'firebase'
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import Store from '../stores/Store';
import Input from '../components/Input';

export interface Props {
  }
//const LoginScreen: React.FC<Props> = (props:Props, navigation : any) => {

function LoginScreen({ navigation }) {    
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [myState, setMyState] = useState(
        {
            error: '',
            loading: false,
        }
    );
    const emailInputHandler = (text) => {
        setEmail(text);
    }
    const passwordInputHandler = (text) => {
        setPassword(text);
    }
    const onLoginPress = () => {
        firebase.auth().signInWithEmailAndPassword(email, password)
        .then(()=>{
            //alert(firebase.auth().currentUser.uid);
            console.log(firebase.auth().currentUser.uid);
            Store.updateUserId(firebase.auth().currentUser.uid);
            setMyState(prev => ({ ...prev, error: '', loading: false, }));
            navigation.navigate('Main')
        })
        .catch((error)=>{
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
        .then(()=>{
            setMyState(prev => ({ ...prev, error: '', loading: false, }));
            navigation.navigate('Main')
        })
        .catch(()=>{
            setMyState(prev => ({ ...prev, error: 'Authentication Failed', loading: false, }));
        })
    }
    const renderButtonOrLoading =() =>{
        if (myState.loading){
            return <Text> Loading</Text>
        }
        else {
            return <View>
                <Button title='SignUp' onPress={() => { onSignUpPress() }} />
                <Button title='Login' onPress={() => { onLoginPress() }} />
            </View>
        }

    }
    return (
        <View style={styles.view}>
            <Text>Email:</Text>
            <Input
                style={styles.input}
                blurOnSubmit
                autoCapitalize='none'
                autoCorrect={false}
                maxLength={50}
                onChangeText={emailInputHandler}
                value={email}
            />
            <Text>Password:</Text>
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
    },
    input: {
        width: 250,
        textAlign: 'left',
    },
});
export default LoginScreen;