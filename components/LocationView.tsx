import React from 'react';
import { StyleSheet, View, Text } from 'react-native';
import { inject, observer } from 'mobx-react';
import FieldValueText from './FieldValueText';

export interface Props {
    store?: any;
  }

const LocationView: React.FC<Props> = (props:Props) => {
    const { statusText, locationText , speed, accuracy, altitude, routeDistance } = props.store;
    return (
        <View style={styles.view}>
            <Text style={styles.status} >{statusText}</Text>
            <Text style={styles.location}>{locationText}</Text>
            <FieldValueText  filed='Accuracy: ' value = {accuracy.toFixed(1)} units= 'm' />
            <FieldValueText  filed='Speed: ' value = {speed.toFixed(1)} units= 'm/s' />
            <FieldValueText  filed='Altitude: ' value = {altitude.toFixed(1)} units= 'm' />
            <FieldValueText  filed='Distance: ' value = {routeDistance.toFixed(2)} units= 'km' />
        </View>
    );
}
const styles = StyleSheet.create({
    view: {
        padding: 10,
    },
    status: {
        fontSize: 20,
    },
    location: {
        fontSize: 24,
    }
});
export default inject("store")(observer(LocationView));
