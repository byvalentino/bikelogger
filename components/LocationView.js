import React from 'react';
import { StyleSheet, View, Text } from 'react-native';

export default function LocationView(props) {
    const text = JSON.stringify(props.location);
    return (
        <View style={styles.view}>
            <Text>{text}</Text>
        </View>
    );
}
const styles = StyleSheet.create({
    view: {
        paddingTop: 10,
    },
});