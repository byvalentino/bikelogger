import React, { useState, useEffect } from 'react';
import { StyleSheet, Text, TouchableOpacity, View, Button, Modal, Alert } from 'react-native';
import { Accelerometer } from 'expo-sensors';
import { inject, observer } from 'mobx-react';
import MyModal from '../components/MyModal';
import Colors from '../constants/colors';
import { log } from '../services/Logger';

export interface Props {
  store?: any;
}
const AccelerometerScreen: React.FC<Props> = (props: Props) => {
  const { accelerometerModalVisable, setAccelerometerModalVisable } = props.store;
  const [data, setData] = useState({});

  useEffect(() => {
    _toggle();
    log('enter AccelerometerScreen');
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
    if (this._subscription) {
      _unsubscribe();
    } else {
      _subscribe();
    }
  };

  const _slow = () => {
    Accelerometer.setUpdateInterval(1000);
  };

  const _fast = () => {
    Accelerometer.setUpdateInterval(16);
  };

  const _subscribe = () => {
    this._subscription = Accelerometer.addListener(accelerometerData => {
      setData(accelerometerData);
    });
  };

  const _unsubscribe = () => {
    this._subscription && this._subscription.remove();
    this._subscription = null;
  };

  let { x, y, z } = data;
  return (
    <MyModal 
      visible={accelerometerModalVisable}
      closeMethod = {closeButtonHandler} 
    >
        <View style={styles.sensor}>
          <Text style={styles.text}>Accelerometer: (in Gs, 1 G = 9.81 m s^-2)</Text>
          <Text style={styles.text}>
            x: {round(x)} y: {round(y)} z: {round(z)}
          </Text>
          <View style={styles.buttonContainer}>
            <TouchableOpacity onPress={_toggle} style={styles.button}>
              <Text>Toggle</Text>
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

function round(n) {
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
    marginTop: 45,
    paddingHorizontal: 10,
  },
  text: {
    textAlign: 'center',
  },
});
