package com.bikelogger;

//define messages, which will be send to owner.
public interface MyBufferListener {
    //when buffer is filled with data 
    void onBufferRead(String data);
}