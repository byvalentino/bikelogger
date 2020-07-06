import React, { useState } from 'react';
import { TextInput, StyleSheet } from 'react-native';

const Input = (props) => {
    return (
    <TextInput {...props} style={{ ...styles.input, ...props.style }} />
    )
}
export default Input;

const styles = StyleSheet.create({
    input: {
        borderBottomColor: 'gray',
        borderBottomWidth: 1,
        height: 30,
        marginVertical: 10,
    },
});