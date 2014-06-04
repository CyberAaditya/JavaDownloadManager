import java.io.*;
import java.net.*;
import java.util.*;

//class to download a file from a URL

class Download extends Observable implements Runnable {
    //Maximum size of Download buffer..
    
    private static final int MAX_BUFFER_SIZE = 1024;
    
    //Status Names..
    
    public static final String STATUSES[] = {"Downloading","Paused","Completed","Cancelled","Error"};
    
    //these are thhe status codes..
    
    public static final int DOWNLOADING = 0;
    public static final int PAUSED = 1;
    public static final int COMPLETE = 2;
    public static final int CANCELLED = 3;
    public static final int ERROR=4;
    private URL url; //download URL
    private int size; // size of download in bytes..
    private int downloaded; // number of bytes downloaded.
    private int status; // current status of download.
    
    //constructor for Download.
    public Download(URL url) {
        this.url = url;
        size = -1;
        downloaded = 0;
        status = DOWNLOADING;
        
        
        //begin the download..
        download();
    }
    
    //get this download URL...
    
    public String getUrl() {
        return url.toString();
    }
    //get the download size,...
    public int getSize() {
        return size;
    }
    
    //get the download's progresss..
    
    public float getProgress() {
        return ((float) downloaded / size) *100;   
    }
    
    //get the download status..
    public int getStatus() {
        return status;
    }
    
    //pause this download...
    public void pause() {
     status = PAUSED;
        stateChanged();
    }
    
    //resume this download..
    public void resume() {
        status = DOWNLOADING;
        stateChanged();
        download();
    }
    
    //cancel this download...
    
    public void cancel() {
        status = CANCELLED;
        stateChanged();
    }
    
    //download having an error...
    
    
    private void error() {
     status = ERROR;
        stateChanged();
    }
    
    //start or Resume Downloading..
    
    private void download() {
     Thread thread = new Thread(this);
        thread.start();
    }
    //get file name portion of URL
    private String getFileName(URL url) {
        String fileName = url.getFile();
        return fileName.substring(fileName.lastIndexOf('/') +1);
    }
    
    //download file...
    
    public void run() {
        RandomAccessFile file = null;
        InputStream stream = null;
        
        try {
            //open the connection to URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            //what portion of file to download..
            
            connection.setRequestProperty("Range","bytes=" +downloaded + "-");
            
            //connect to server();
            
            connection.connect();
            
            //make sure response code is in the 200 range..
            
            if(connection.getResponseCode() /100 !=2) {
             error();   
            }
            
            //check for valid content length.
            
            int contentLength = connection.getContentLength();
            if(contentLength <1) {
                error();   
            }
            
            //set the size for this donwload of it has been already set...
            
            if(size == -1 ) {
                size = contentLength;
                stateChanged();
            }
    //open file and seek to the end of it..
            
        file = new RandomAccessFile(getFileName(url),"rw");
        file.seek(downloaded);
            
            stream = connection.getInputStream();
            
            while(status == DOWNLOADING)  {
             // size buffer according to how much of the file is left to download..
                
                byte buffer[];
                if(size - downloaded > MAX_BUFFER_SIZE) {
                    buffer = new byte[MAX_BUFFER_SIZE];
                } else {
                    buffer = new byte[size - downloaded];
                }
                
                //read from server into buffer..
                
                int read = stream.read(buffer);
                if(read == -1) 
                    break;
                
                //write buffer to file..
                
                file.write(buffer, 0, read);
                downloaded += read;
                stateChanged();
            }
            //change status to complete if the point was reached because download has finished..
            
            if(status == DOWNLOADING) {
                status = COMPLETE;
                SendEmail se = new SendEmail();
                stateChanged();
            }
        } catch(Exception e) {
            
            error();
        }
        finally {
            //close file..
            if(file!=null) {
             try {
                 
                 file.close();
             } catch(Exception e) {}
                
            }
            
            //close the connection to server...
            if(stream !=null) {
                try {
                 stream.close();
                    
                } catch(Exception e) {}
            }
        }
    }
    //notify observaer that download is completed..
    
    private void stateChanged() {
       // stateChanged();
        notifyObservers();
    }
}
        
    

    
