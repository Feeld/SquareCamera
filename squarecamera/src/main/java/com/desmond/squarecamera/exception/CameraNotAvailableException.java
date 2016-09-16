package com.desmond.squarecamera.exception;

/**
 * @author dkc.adam@gmail.com.
 */
public class CameraNotAvailableException extends Exception{
    public CameraNotAvailableException(){
        super("Cannot access to the camera in this moment.");
    }

}
