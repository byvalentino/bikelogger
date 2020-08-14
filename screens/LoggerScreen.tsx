//@ts-nocheck
import React, { Component } from 'react';
import { Text, View, FlatList, Dimensions, Button, StyleSheet } from 'react-native';
import { logHistory } from '../services/Logger';
import Colors from '../constants/colors';

const { width } = Dimensions.get('window');

const style = {
  justifyContent: 'center',
  alignItems: 'center',
  width: width,
  height: 50,
  flex: 1,
  borderWidth: 1,
};

const COLORS = ['#FBFCFC','#AED6F1'];

class ScrollFlatList extends Component {
  size = Math.min([this.props.data.length,50]);
  getItemLayout = (data, index) => (
    { length: 50, offset: 50 * index, index }
  )
  getColor(index) {
    const mod = index%2;
    return COLORS[mod];
  }
  scrollToIndex = () => {
    let randomIndex = Math.floor(Math.random(Date.now()) * this.props.data.length);
    this.flatListRef.scrollToIndex({animated: true, index: randomIndex});
  }
  scrollToItem = () => {
    let randomIndex = Math.floor(Math.random(Date.now()) * this.props.data.length);
    this.flatListRef.scrollToIndex({animated: true, index: "" + randomIndex});
  }

  render() {
    return (
      <View style={styles.container}>
        <View style={styles.header}>
          <Button
            onPress={this.scrollToIndex}
            title="scroll To Index"
            color={Colors.primary}
          />
          <Button
            onPress={this.scrollToItem}
            title="scroll To Item"
            color={Colors.primary}
          />
        </View>
        <FlatList
          style={{ flex: 1 }}
          ref={(ref) => { this.flatListRef = ref; }}
          keyExtractor={(item) => item.id}
          getItemLayout={this.getItemLayout}
          initialScrollIndex={this.size}
          initialNumToRender={2}
          renderItem={({ item, index}) => (
              <View style={{...style, backgroundColor: this.getColor(index)}}>
                <Text>{item.msg}</Text>
              </View>
            )}
          {...this.props}
        />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1
  },
  header: {
    paddingTop: 20,
    backgroundColor: 'darkturquoise',
    justifyContent: 'flex-start', 
  }
});

export default class LoggerScreen extends Component {
  render() {
    return  <ScrollFlatList
               data={logHistory}
             />
  }
}