import React, { useState } from 'react';
import { View, StyleSheet } from 'react-native';

const Card = (props) => {
    return (
        <View style={{...styles.card, ...props.style}}>
            {props.children}
        </View>
    )
}
export default Card;

const styles = StyleSheet.create({
    card: {
        shadowColor: 'black',
        shadowOffset: {
            width: 0,
            height: 2,
        },
        shadowOpacity: 0.26,
        shadowRadius: 6,
        backgroundColor: 'white',
        elevation: 8,
        padding: 20,
        borderRadius: 10,
        paddingHorizontal: 15,
    },
});