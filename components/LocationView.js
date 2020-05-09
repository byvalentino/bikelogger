import React from 'react';
import { StyleSheet, View, Text } from 'react-native';
import { inject, observer } from 'mobx-react';

function LocationView(props) {
    const { text } = props.store;
    const textLocation = JSON.stringify(props.location);
    return (
        <View style={styles.view}>
            <Text>{text}</Text>
            <Text>{textLocation}</Text>
        </View>
    );
}
const styles = StyleSheet.create({
    view: {
        paddingTop: 10,
    },
});
export default inject("store")(observer(LocationView));