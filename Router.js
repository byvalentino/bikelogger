
import React, { useEffect } from 'react';
import AsyncStorage from '@react-native-community/async-storage';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { inject, observer } from 'mobx-react';
import { AppLoading } from 'expo';

import Store from './stores/Store';
import MainScreen from './screens/MainScreen';
import LoginScreen from './screens/LoginScreen';
import LoggerScreen from './screens/LoggerScreen';
import ConfigScreen from './screens/ConfigScreen';
import UserPropsScreen from './screens/UserPropsScreen';
import MapForgroundScreen from './screens/MapForgroundScreen';
import AuthContext from './constants/MyContext';
import {log} from './services/Logger';
import { CONTACTS } from 'expo-permissions';

const Stack = createStackNavigator();

function Router({ navigation }) {
  const [isReady, setIsReady] = React.useState(false);
  const [state, dispatch] = React.useReducer(
    (prevState, action) => {
      switch (action.type) {
        case 'RESTORE_TOKEN':
          return {
            ...prevState,
            userToken: action.token,
            isLoading: false,
          };
        case 'SIGN_IN':
          return {
            ...prevState,
            isSignout: false,
            userToken: action.token,
          };
        case 'SIGN_OUT':
          return {
            ...prevState,
            isSignout: true,
            userToken: null,
          };
      }
    },
    {
      isLoading: true,
      isSignout: false,
      userToken: null,
    }
  );
  const {userStore} = Store;

  // Fetch the token from storage then navigate to our appropriate place
  const bootstrapAsync = async () => {
    console.log('bootstrapAsync 1');
    let userToken;
    try {
      userToken = await AsyncStorage.getItem('@userToken');
    } catch (e) {
      // Restoring token failed
    }
    // After restoring token, we may need to validate it in production apps
    // This will switch to the App screen or Auth screen and this loading
    // screen will be unmounted and thrown away.
    console.log('bootstrapAsync 2' )
    dispatch({ type: 'RESTORE_TOKEN', token: userToken });
  };

  useEffect(() => {
    Store.init();
    //bootstrapAsync();
  }, []);

  useEffect(() =>{
    console.log('useEffect2, token1:', state.userToken);
    console.log('useEffect2, token2:', userStore.userToken);
    if (userStore.userToken !== null && userStore.userToken !== '' ){
      userStore.updateUserLastLogin();
      userStore.fetchUserData();
    }
  },[userStore.userToken]);
  //},[state.userToken]);

  const authContext = React.useMemo(
    () => ({
      signIn: async (data) => {
        // In a production app, we need to send some data (usually username, password) to server and get a token
        // We will also need to handle errors if sign in failed
        // After getting token, we need to persist the token using `AsyncStorage`
        // In the example, we'll use a dummy token
        console.log("signIn");
        dispatch({ type: 'SIGN_IN', token: 'dummy-auth-token' });
      },
      signOut: () => dispatch({ type: 'SIGN_OUT' }),
      signUp: async (data) => {
        // In a production app, we need to send user data to server and get a token
        // We will also need to handle errors if sign up failed
        // After getting token, we need to persist the token using `AsyncStorage`
        // In the example, we'll use a dummy token
        console.log("signUp");
        dispatch({ type: 'SIGN_IN', token: 'dummy-auth-token' });
      },
    }),
    []
  );

  // const _cacheResourcesAsync = async () => {
  //   const images = [require('./assets/snack-icon.png')];
  //   const cacheImages = images.map(image => {
  //     return Asset.fromModule(image).downloadAsync();
  //   }); 
  //   return Promise.all(cacheImages);
  // }

  if (!isReady) {
    return (
      <AppLoading
        startAsync={bootstrapAsync}
        onFinish={() => setIsReady(true)}
        onError={console.warn}
      />
    ); }
    
  return (
    <AuthContext.Provider value={authContext}>
      <NavigationContainer>
        <Stack.Navigator
          screenOptions={{
            headerStyle: {
              backgroundColor: '#373EAC',
            },
            headerTintColor: '#fff',
            headerTitleStyle: {
              fontWeight: 'bold',
            },
          }}
        >
          {state.userToken == null ? (
            <Stack.Screen name="Login" component={LoginScreen} />
          ) : (
              <>
                <Stack.Screen name="MainScreen" component={MainScreen}
                  options={{ title: 'Bike Tracker' }}
                />
                <Stack.Screen name="UserProps" component={UserPropsScreen} 
                  options={{ title: 'User Props' }}
                />
                <Stack.Screen name="MapForground" component={MapForgroundScreen} />
                <Stack.Screen name="Logger" component={LoggerScreen} />
                <Stack.Screen name="Config" component={ConfigScreen} />
              </>
            )}
        </Stack.Navigator>
      </NavigationContainer>
    </AuthContext.Provider>
  );
}
export default inject("store")(observer(Router));
