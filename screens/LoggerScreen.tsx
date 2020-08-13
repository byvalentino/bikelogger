//@ts-nocheck
import React, { FC, useState } from 'react';
import { StyleSheet, SafeAreaView, FlatList, Text, View, TouchableOpacity, Button } from 'react-native';
import { inject, observer } from 'mobx-react';
import { useNavigation } from '@react-navigation/native';
import { logHistory } from '../services/Logger';
import Colors from '../constants/colors';

export interface Props {
  store?: any;
}
const DATA = [
  {
    id: "bd7acbea-c1b1-46c2-aed5-3ad53abb28ba",
    title: "First Item",
  },
  {
    id: "3ac68afc-c605-48d3-a4f8-fbd91aa97f63",
    title: "Second Item",
  },
  {
    id: "58694a0f-3da1-471f-bd96-145571e29d72",
    title: "Third Item",
  },
];

const Item = ({ item, onPress, style }) => (
  <TouchableOpacity onPress={onPress} style={[styles.item, style]}>
    <Text style={styles.title}>{item.title}</Text>
  </TouchableOpacity>
);

const LoggerScreen: FC<Props> = (props: Props ) => {
  // const { loggerModalVisable, setLoggerModalVisable } = props.store;
  const [selectedId, setSelectedId] = useState(null);
  const navigation = useNavigation();
  //const list = logHistory.map((x, index) => <Text style={styles.line} key={`line-${index}`} >{x}</Text>);
  //const listItems = logHistory.map((x, index) => <li key={`line-${index}`}><Text >{x}</Text></li>);

  const closeMethod =() =>{
    navigation.goBack();
  }
  const renderItem = ({ item }) => {
    const backgroundColor = item.id === selectedId ? "#6e3b6e" : "#f9c2ff";
    return (
      <Item
        item={item}
        onPress={() => setSelectedId(item.id)}
        style={{ backgroundColor }}
      />
    );
  };

  return (
    <View>
      <Text>Log:</Text>
      <SafeAreaView style={styles.container}>
        <FlatList
          data={DATA}
          renderItem={renderItem}
          keyExtractor={(item) => item.id}
          extraData={selectedId}
        />
      </SafeAreaView>
      <View style={styles.buttonClose}>
        <Button title="Close" color={Colors.primary} onPress={closeMethod} />
      </View>
    </View>

  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    margin: 20,
  },
  item: {
    backgroundColor: '#f9c2ff',
    padding: 20,
    marginVertical: 8,
    marginHorizontal: 16,
  },
  title: {
    fontSize: 32,
  },
  buttonClose: {
    alignItems: "center",
    width: 100,
  },
});
export default inject("store")(observer(LoggerScreen));
