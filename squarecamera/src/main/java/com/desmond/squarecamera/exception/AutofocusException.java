package com.desmond.squarecamera.exception;

/**
 * @author dkc.adam@gmail.com.
 */
public class AutofocusException extends Exception {
    public AutofocusException(){
        super("Autofocus exception", null);
    }

    public AutofocusException(Throwable t) {
        super("Autofocus exception", t);
    }
}
