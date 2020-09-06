import React from 'react';
import { View, StyleSheet, Button } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import Header from '../components/Header';
import MapForegroundLocation from '../components/MapForegroundLocation';
import Colors from '../constants/colors';

export interface Props {
}
const MapForgroundScreen: React.FC<Props> = (props:Props) => {
  return (
      <View style={styles.container1}>
        {/* <Header /> */}
        <MapForegroundLocation />
      </View>
  );
}
export default MapForgroundScreen;

const styles = StyleSheet.create({
  container1: {
    width: '100%',
    height: '100%',
    paddingTop: 30
  },
});
