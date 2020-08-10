import AsyncStorage from '@react-native-community/async-storage';

export const storeLocalData = async (key: string, value: string) => {
    try {
        await AsyncStorage.setItem(key, value)
    } catch (e) {
        // saving error
        console.log ('storeLocalData', e);
    }
}

export const getLocalData = async (key: string) => {
    try {
        const value = await AsyncStorage.getItem(key)
        if (value !== null) {
            // value previously stored
            return value;
        }
    } catch (e) {
        // error reading value
        console.log ('getLocalData', e);
    }
}
