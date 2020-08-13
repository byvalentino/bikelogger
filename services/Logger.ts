
export const log = (message?: any, ...optionalParams: any[]): void => {
    if (optionalParams.length > 0) {
        console.log(message,optionalParams);
    }
    else
    {
        console.log(message);
    }
}

export const logError = (message?: any, ...optionalParams: any[]): void => {
    console.error(message,optionalParams);
}