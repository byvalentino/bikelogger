import React, { useEffect, useRef } from 'react';
import { View, StyleSheet, Button } from 'react-native';

import { NativeEventEmitter, NativeModules } from 'react-native';
import ToastExample from '../services/ToastExample';
import AccLog from '../services/androAccLogger';
import Colors from '../constants/colors';

class NativeAccPanel extends React.Component {
  eventListener: any;
  constructor(props) {
    super(props);
  }
  componentDidMount() {
    const eventEmitter = new NativeEventEmitter(NativeModules.ToastExample);
    this.eventListener = eventEmitter.addListener('EventAcc', (event:any) => {
       const eventMsg: string = event.eventMsg ;
       console.log(eventMsg); 
       if (eventMsg === 'onBufferRead')
       {
        if (event.eventProperty){
          console.log(event.eventProperty); 
         }
       }
       else {
        if (event.eventProperty){
          console.log(event.eventProperty); 
         }
         ToastExample.show(eventMsg, ToastExample.SHORT);
       }
    });
  }

  componentWillUnmount() {
    this.eventListener.remove(); //Removes the listener
  }
  buttonStartAccClicked() {
    AccLog.StartAcc(AccLog.FASTEST);
  };
  buttonStopAccClicked() {
    AccLog.StopAcc();
  };

render() {
  return (
    <View style={styles.buttonContainer}>
    <Button title="ACC start" color={Colors.primary} onPress={this.buttonStartAccClicked} />
    <Button title="ACC stop" color={Colors.primary} onPress={this.buttonStopAccClicked} />
    </View>
   )
   
  }
}

const styles = StyleSheet.create({
  buttonContainer: {
    marginTop: 22,
    flexDirection: 'row',
    width: '80%',
    justifyContent: 'center',
    paddingHorizontal: 15,
  },
});
export default NativeAccPanel;
