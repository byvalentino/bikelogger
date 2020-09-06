import React from 'react';
import { Text, View, StyleSheet } from 'react-native';
import Input from './Input';

export interface Props {
    lable: string;
    style?: any;
    children: Input;
  }
const LabelInput = (props:Props) => {
    const { lable } = props
    return (
        <View style={{...styles.lineContainer, ...props.style}} >
            <Text style={styles.label}>{lable} </Text>
            {props.children} 
        </View>
    )
}
export default LabelInput;

const styles = StyleSheet.create({
    lineContainer: {
        flexDirection: 'row',
        /* justifyContent: 'flex-start', */
    },
    label: {
        fontSize: 18,
        color: '#0000cc',
    },
    input: {
        fontSize: 18,
    },
});