/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.duke.archives.erprocessor;

import edu.duke.archives.erprocessor.exceptions.PreviewException;
import edu.duke.archives.erprocessor.metadata.mets.MetsFile;
import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

/**
 *
 * @author Seth Shaw
 */
public class PreviewHandler {

    public static final String VIEWER_DEFAULT = "Default";
    
    private static PreviewHandler handler = null;
    private BrowserLauncher browserLauncher = null;
    private File fileBase = null;
    private String selectedBrowser = VIEWER_DEFAULT;

    private PreviewHandler() throws PreviewException{
        try {

            browserLauncher = new BrowserLauncher();
            
        } 
        //TODO: Consolidate like Exceptions
        catch (BrowserLaunchingInitializingException ex) {
            throw new PreviewException(ex.getLocalizedMessage(), PreviewException.BROWSER_LAUNCH_FAIL);
        } catch (UnsupportedOperatingSystemException ex) {
            throw new PreviewException(ex.getLocalizedMessage(), PreviewException.BROWSER_LAUNCH_FAIL);
        }

        
    }

    public static PreviewHandler getHandler() throws Exception {
        if (handler == null) {
            handler = new PreviewHandler();
        }
        return handler;
    }

    public void setBase(File fileBase) throws PreviewException {
        if (fileBase.isFile()) {
            throw new PreviewException(fileBase.getAbsolutePath() +
                    " is a file. Only directories are permitted.", PreviewException.ILLEGAL_BASE_DIR);
        }
        if (!fileBase.canRead()) {
            throw new PreviewException("Cannot read selected directory: " +
                    fileBase.getAbsolutePath(), PreviewException.ILLEGAL_BASE_DIR);
        }
        this.fileBase = fileBase;
    }
    
    public boolean isBaseSet(){
        if (fileBase == null)
            return false;
        if (fileBase.isFile()) {
            return false;
        }
        if (!fileBase.canRead()) {
            return false;
        }
        return true;
    }

    public void previewFile(MetsFile node) throws PreviewException, FileNotFoundException {
        //Check for base path
        if(fileBase == null){
            throw new PreviewException("Base has not been set.", PreviewException.NO_BASE_DIR);
        }
        
        //FILE
        String nodePath = node.getFilePath();
        //String protocol prefix
        if(nodePath.startsWith("file://")){
            nodePath = nodePath.replaceFirst("file://", "");
        }
        if(nodePath.length() < 1){
            throw new FileNotFoundException("File path for " + node.getName()+
                    " not found");
        }
        File toDisplay = new File(fileBase.getAbsolutePath()+File.separator+nodePath);
        if(!toDisplay.exists()){
            throw new FileNotFoundException("Cannot find: "+toDisplay.getAbsolutePath());
        }
        
        //BROWSER
//        if (toDisplay.canWrite() && selectedBrowser.equals(VIEWER_DEFAULT)) {
          if (toDisplay.canWrite()) {            
            //COPY before display
//            System.out.println("We can edit " + toDisplay.getAbsolutePath() +
//                    "\nCreating copy to display...");
            //Find an extension to use if one exists
            int pointIndex = toDisplay.getName().lastIndexOf('.');
            String extension = null;
            if (pointIndex > 0) {
                extension = toDisplay.getName().substring(pointIndex);
            }
            //Copy
            try {
                File temp = File.createTempFile("temp_", extension);
                temp.deleteOnExit();
                FileChannel in = new FileInputStream(toDisplay).getChannel();
                FileChannel out = new FileOutputStream(temp).getChannel();
                in.transferTo(0, in.size(), out);
                in.close();
                out.close();
                if(toDisplay.lastModified() > 0){
                    temp.setLastModified(toDisplay.lastModified());
                }
                toDisplay = temp;
            } catch (IOException ioe) {
                throw new PreviewException(ioe.getLocalizedMessage(),
                        PreviewException.COPY_FAIL);
            }
        } else {
//                System.out.println("Cannot edit " + toDisplay.getAbsolutePath() +
//                        "\nOpening file using default browser.");
        }
        //Launch
        browserLauncher.openURLinBrowser(selectedBrowser, "file://" +
                toDisplay.getAbsolutePath());
    }
    
    public List getAvailableBrowsers(){
        return browserLauncher.getBrowserList();
    }

    void previewFile(MetsFile metsFile, String browserRequest) throws PreviewException, FileNotFoundException {
        if(browserLauncher.getBrowserList().contains(browserRequest)){
            selectedBrowser = browserRequest;
        }
        previewFile(metsFile);
    }
}
