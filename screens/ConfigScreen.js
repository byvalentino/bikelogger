import React, { useState } from 'react';
import { View, Text, StyleSheet, Modal, TouchableHighlight,Alert } from "react-native";
import { inject, observer } from 'mobx-react';
import Card from '../components/Card';
import Input from '../components/Input';
import Colors from '../constants/colors';

export interface Props {
    store?: any;
}
const ConfigScreen: React.FC<Props> = (props: Props) => {
    const { configModalVisible, SetConfigModalVisible } = props.store;
    return (
        <Modal
            animationType="slide"
            transparent={true}
            visible={configModalVisible}
            onRequestClose={() => {
                Alert.alert("Modal has been closed.");
            }}
        >
            <View style={styles.centeredView}>
                <View style={styles.modalView}>
                    <Text style={styles.modalText}>Hello World!</Text>

                    <TouchableHighlight
                        style={{ ...styles.openButton, backgroundColor: "#2196F3" }}
                        onPress={() => {
                            SetConfigModalVisible(!configModalVisible);
                        }}
                    >
                        <Text style={styles.textStyle}>Hide Modal</Text>
                    </TouchableHighlight>
                </View>
            </View>
        </Modal>
    );
};
export default inject("store")(observer(ConfigScreen));

const styles = StyleSheet.create({
    centeredView: {
        flex: 1,
        justifyContent: "center",
        alignItems: "center",
        marginTop: 22
    },
    modalView: {
        margin: 20,
        backgroundColor: "white",
        borderRadius: 20,
        padding: 35,
        alignItems: "center",
        shadowColor: "#000",
        shadowOffset: {
            width: 0,
            height: 2
        },
        shadowOpacity: 0.25,
        shadowRadius: 3.84,
        elevation: 5
    },
    textStyle: {
        color: "white",
        fontWeight: "bold",
        textAlign: "center"
    },
    modalText: {
        marginBottom: 15,
        textAlign: "center"
    }
});