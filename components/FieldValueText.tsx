import React from 'react';
import { Text, View, StyleSheet } from 'react-native';

export interface Props {
    filed: string;
    value: string;
    units: string;
  }
const FieldValueText = (props:Props) => {
    const { filed, value , units } = props
    return (
        <View style={styles.lineContainer}>
            <Text style={styles.filed}>{filed} </Text>
            <Text style={styles.value}>{value} ({units})</Text>
        </View>
    )
}
export default FieldValueText;

const styles = StyleSheet.create({
    lineContainer: {
        flexDirection: 'row',
        justifyContent: 'flex-start',
    },
    filed: {
        fontSize: 18,
        color: '#0000cc',
    },
    value: {
        fontSize: 20,
    },
});