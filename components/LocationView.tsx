import React from 'react';
import { StyleSheet, View, Text } from 'react-native';
import { inject, observer } from 'mobx-react';

export interface Props {
    store?: any;
  }

const LocationView: React.FC<Props> = (props:Props) => {
    const { statusText, locationText } = props.store;
    return (
        <View style={styles.view}>
            <Text>{statusText}</Text>
            <Text>{locationText}</Text>
        </View>
    );
}
const styles = StyleSheet.create({
    view: {
        padding: 10,
    },
});
export default inject("store")(observer(LocationView));
