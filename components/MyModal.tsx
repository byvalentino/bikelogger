import React, {FC} from 'react';
import { StyleSheet, View, Button, Modal, Alert } from 'react-native';
import { inject, observer } from 'mobx-react';
import Colors from '../constants/colors';

export interface Props {
  visible: boolean;
  closeMethod: () => void;
  children?:React.ReactNode;
}
const MyModal: FC<Props> = (props: Props) => {
//const MyModel: React.FC<Props> = (props: Props) => {
  //const { accelerometerModalVisable, setAccelerometerModalVisable } = props.store;
  return (
    <Modal
      animationType="slide"
      transparent={true}
      visible={props.visible}
      onRequestClose={() => {
        Alert.alert("Modal has been closed.");
      }}
    >
      <View style={styles.modalView}>
        {props.children}
        <View style={styles.buttonClose}>
          <Button title="Close" color={Colors.primary} onPress={props.closeMethod} />
        </View>
      </View>
    </Modal>
  );
}
export default MyModal;

const styles = StyleSheet.create({
  modalView: {
    marginTop: 70,
    margin: 20,
    backgroundColor: "white",
    borderRadius: 20,
    padding: 35,
    shadowColor: "#000",
    shadowOffset: {
      width: 0,
      height: 2
    },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,
    elevation: 5
  },
  buttonClose: {
    alignItems: "center",
    width: 100,
  },
});
