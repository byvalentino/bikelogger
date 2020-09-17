import React, { useState, useEffect, } from 'react';
import { StyleSheet, Text, TouchableOpacity, View, Button, Modal, Alert } from 'react-native';
import { Accelerometer, Gyroscope } from 'expo-sensors';
import { inject, observer } from 'mobx-react';
import MyModal from '../components/MyModal';
import { IStore } from '../stores/Store';
import Colors from '../constants/colors';
import { log } from '../services/Logger';

export interface Props {
  store?: IStore;
}
const SlOW_GYRO: number = 1000;
const FAST_GYRO: number = 1;

const AccelerometerScreen: React.FC<Props> = (props: Props) => {
  const { uiStore } = props.store!;
  const { trackingStore}  = props.store!;
  const { accelerometerModalVisable, setAccelerometerModalVisable } = uiStore;
  const {startSensorInerval, addAcceleromerReading, stopSensorInerval, addGyroReading, accMsg} = trackingStore;
  const [dataA, setDataA] = useState({ x: 0, y: 0, z: 0 });
  const [dataG, setDataG] = useState({ x: 0, y: 0, z: 0 });
  const [tugglemsg, setTugglemsg] = useState('start');

  useEffect(() => {
    _toggle();
  }, []);

  useEffect(() => {
    return () => {
      _unsubscribe();
    };
  }, []);

  const closeButtonHandler = () => {
    setAccelerometerModalVisable(false);
  }
  const _toggle = () => {
    if (this._subscriptionA) {
      _unsubscribe();
    } else {
      _subscribe();
    }
  };

  const _slow = () => {
    Accelerometer.setUpdateInterval(SlOW_GYRO);
    Gyroscope.setUpdateInterval(SlOW_GYRO);
  };

  const _fast = () => {
    Accelerometer.setUpdateInterval(FAST_GYRO);
    Gyroscope.setUpdateInterval(FAST_GYRO);
  };

  const _subscribe = () => {
    console.log('_subscribe')
    startSensorInerval();
    this._subscriptionA = Accelerometer.addListener(accelerometerData => {
      addAcceleromerReading(accelerometerData);
      //setDataA(accelerometerData);
    });
    this._subscriptionG = Gyroscope.addListener(gyroscopeData => {
      //addGyroReading(gyroscopeData);
      //setDataG(gyroscopeData);
    });
    setTugglemsg('stop');
  };

  const _unsubscribe = () => {
    console.log('_unsubscribe')
    this._subscriptionA && this._subscriptionA.remove();
    this._subscriptionA = null;
    this._subscriptionG && this._subscriptionG.remove();
    this._subscriptionG = null;
    stopSensorInerval();
    setTugglemsg('start');
  };

  let { x, y, z } = dataA;
  let { x:xG, y:yG, z:zG } = dataG;
  return (
    <MyModal
      visible={accelerometerModalVisable}
      closeMethod={closeButtonHandler}
    >
      <View style={styles.sensor}>
        <Text style={styles.text}>Accelerometer: (in Gs, 1 G = 9.81 m s^-2)</Text>
        <Text style={styles.text}>
          x: {round(x)} y: {round(y)} z: {round(z)}
        </Text>
        <Text style={styles.text}>Gyroscope:</Text>
        <Text style={styles.text}>
          x: {round(xG)} y: {round(yG)} z: {round(zG)}
        </Text>
        <Text>
          {accMsg}
        </Text> 
        <View style={styles.buttonContainer}>
          <TouchableOpacity onPress={_toggle} style={styles.button}>
            <Text>{tugglemsg}</Text>
          </TouchableOpacity>
          <TouchableOpacity onPress={_slow} style={[styles.button, styles.middleButton]}>
            <Text>Slow</Text>
          </TouchableOpacity>
          <TouchableOpacity onPress={_fast} style={styles.button}>
            <Text>Fast</Text>
          </TouchableOpacity>
        </View>
      </View>
    </MyModal>
  );
}
export default inject("store")(observer(AccelerometerScreen));

function round(n: number) {
  if (!n) {
    return 0;
  }

  return Math.floor(n * 100) / 100;
}

const styles = StyleSheet.create({
  buttonContainer: {
    flexDirection: 'row',
    alignItems: 'stretch',
    marginTop: 15,
  },
  button: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#eee',
    padding: 10,
  },
  middleButton: {
    borderLeftWidth: 1,
    borderRightWidth: 1,
    borderColor: '#ccc',
  },
  sensor: {
    marginTop: 25,
    paddingHorizontal: 10,
  },
  text: {
    textAlign: 'center',
  },
});
