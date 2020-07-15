import React from 'react';
import { View, StyleSheet} from 'react-native';
import Header from '../components/Header';
//import MapForegroundLocation from '../components/MapForegroundLocation'
import MapBackgroundLocation from  '../components/MapBackgroundLocation';

export interface Props {
}
const MainScreen: React.FC<Props> = (props:Props) => {
  return (
      <View style={styles.container1}>
        {/* <Header /> */}
        <MapBackgroundLocation />
      </View>
  );
}
export default MainScreen;

const styles = StyleSheet.create({
  container1: {
    width: '100%',
    height: '100%',
    paddingTop: 30
  },
});
