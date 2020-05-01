import React from 'react';
import { StyleSheet, Text, View, Button } from 'react-native';

export default function App() {
  const [isGetLocation, setGetLocation] = React.useState(false);
  const textStatus = (isGetLocation)? 'Getting Location' : 'No Location';
  const textButton = (isGetLocation)? 'Stop Locating' : 'Start Locating';
  return (
    <View style={styles.container}>
      <Text key='status'>{textStatus}</Text>
      <Button title={textButton} onPress={()=>setGetLocation(!isGetLocation)}/>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
