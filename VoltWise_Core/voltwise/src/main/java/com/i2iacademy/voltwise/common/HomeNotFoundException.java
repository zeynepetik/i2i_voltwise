package com.i2iacademy.voltwise.common;

import java.util.UUID;

public class HomeNotFoundException extends RuntimeException{
    public HomeNotFoundException(UUID homeId){
        super("Home not found in live state cache: "+homeId);
    }

}
