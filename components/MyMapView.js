import React from 'react';
import { StyleSheet } from 'react-native';
import MapView from "react-native-maps";
import { inject, observer } from 'mobx-react';
import {toJS} from 'mobx';

function MyMapView(props) {
    const { trackingStore } = props.store;
    const { isTracking, region } = trackingStore;
    const reactRegion = toJS(region);

    return (
            <MapView
                region={reactRegion}
                showsCompass={true}
                showsUserLocation={isTracking}
                rotateEnabled={true}
                style={styles.mapStyle}
            />
    );
}
const styles = StyleSheet.create({
    mapStyle: {
        width: '100%',
        height: '60%',
    },
});

export default inject("store")(observer(MyMapView));
