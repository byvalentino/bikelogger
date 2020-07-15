//This function takes in latitude and longitude of two location 
// returns the distance between them as the crow flies (in km)
export const getDistanceKm = (lat1: number, lon1: number, lat2: number, lon2: number) => {
    let R = 6371; // km
    const dLat = toRad(lat2 - lat1);
    const dLon = toRad(lon2 - lon1);
    const lat1r = toRad(lat1);
    const lat2r = toRad(lat2);
    let a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1r) * Math.cos(lat2r);
    let c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    let d = R * c;
    return d;
}

// Converts numeric degrees to radians
const toRad = (value: number) => {
    return value * Math.PI / 180;
}