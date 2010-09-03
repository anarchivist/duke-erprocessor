/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.duke.archives.erprocessor.exceptions;

/**
 *
 * @author Seth Shaw
 */
public class PreviewException extends Exception{

    public static final int NO_REASON = -1;
    public static final int NO_BASE_DIR = 0;
    public static final int BROWSER_LAUNCH_FAIL = 1;
    public static final int COPY_FAIL = 2;
    public static final int ILLEGAL_BASE_DIR = 3;
    
    private int reason = -1;
    
    public PreviewException(String message) {
        super(message);
    }

    public PreviewException(String message, int reason) {
        this(message);
        this.reason = reason;
    }

    public int getReason(){
        return reason;
    }

    @Override
    public String getMessage() {
        String message = "";
        switch (reason) {
            case (NO_BASE_DIR):
                message = "No base directory is currently set.";
                break;
            case (BROWSER_LAUNCH_FAIL):
                message = "Failed to start the previewer system.";
                break;
            case (COPY_FAIL):
                message = "Could not create a copy of the requested file.";
                break;
            case (ILLEGAL_BASE_DIR):
                message = "The provided base directory cannot be used.";
                break;
        }
        return message;
    }
}
