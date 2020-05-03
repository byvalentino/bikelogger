import React from 'react';
import { StyleSheet, View, Text } from 'react-native';

export default function Header() {
    return (
        <View style={styles.header}>
            <Text style={styles.text}>Bike Location</Text>
        </View>
    );
}
const styles = StyleSheet.create({
    header: {
        width: '100%',
        height: 30,
        alignItems: 'center',
        backgroundColor: '#004ba0',
    },
    text:{
        color: 'white',
        fontSize : 20,
    }
});