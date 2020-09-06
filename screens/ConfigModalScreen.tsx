import React, { useState } from 'react';
import { View, Text, StyleSheet, Modal, Button, Keyboard, Alert } from "react-native";
import { inject, observer } from 'mobx-react';
//import Card from '../components/Card';
import { IStore } from '../stores/Store';
import Input from '../components/Input';
import Colors from '../constants/colors';

export interface Props {
    store?: IStore;
}
const ConfigModalScreen: React.FC<Props> = (props: Props) => {
    const { uiStore, trackingStore } = props.store!;
    const { configModalVisible, setConfigModalVisible } = uiStore;
    const { trackingTimeInterval, setTrackingTimeInterval } = trackingStore;
    const [enteredText, setEnteredText] = useState(trackingTimeInterval.toString());
    const numberInputHandler = (text: string) => {
        setEnteredText(text.replace(/[^0-9]/g, ''));
    }
    const cacnelButtonHandler = () => {
        setEnteredText(trackingTimeInterval.toString());
        setConfigModalVisible(false);
    }
    const confirmedButtonHandler = () => {
        const intervalNumnber = parseInt(enteredText)
        if (isNaN(intervalNumnber) || intervalNumnber <= 0 || intervalNumnber > 30) {
            Alert.alert(
                'Invalid Interval',
                'Interval has to be between 1 and 30 sec.',
                [{ text: 'Okay', style: 'destructive' }]
            );
            setEnteredText(trackingTimeInterval.toString());
            return;
        }
        else {
            setTrackingTimeInterval(intervalNumnber);
            setConfigModalVisible(false);
        }
    }
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
                    <Text>Time Interval:</Text>
                    <Input
                        style={styles.input}
                        blurOnSubmit
                        autoCapitalize='none'
                        autoCorrect={false}
                        keyboardType='number-pad'
                        maxLength={2}
                        onChangeText={numberInputHandler}
                        value={enteredText}
                    />
                    <View style={styles.buttonContainer}>
                        <View style={styles.button}>
                            <Button title="Cancel" color={Colors.secondary} onPress={cacnelButtonHandler} />
                        </View>
                        <View style={styles.button}>
                            <Button title="Confirm" color={Colors.primary} onPress={confirmedButtonHandler} />
                        </View>
                    </View>
                </View>
            </View>
        </Modal>
    );
};
export default inject("store")(observer(ConfigModalScreen));

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
    button: {
        width: 100,
    },
    cardStyle: {
        width: 300,
        maxWidth: '80%',
        alignItems: 'center',
    },
    input: {
        width: 50,
        textAlign: 'center',
    },
    buttonContainer: {
        flexDirection: 'row',
        width: '100%',
        justifyContent: 'space-between',
        paddingHorizontal: 15,
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