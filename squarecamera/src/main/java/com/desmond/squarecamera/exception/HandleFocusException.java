package com.desmond.squarecamera.exception;

/**
 * @author dkc.adam@gmail.com.
 */
public class HandleFocusException extends Exception {
    public HandleFocusException(){
        super("Cannot handle camera focus", null);
    }

    public HandleFocusException(Throwable t) {
        super("Cannot handle camera focus", t);
    }
}
