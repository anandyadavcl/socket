package com.anand.socket.service;

import com.anand.socket.dto.ResponseMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import static com.anand.socket.constants.Constants.NOOFLINESTOREAD;

public class FileReader implements Runnable{
    private String logFilePath;
    private boolean keepReading;
    private int _updateInterval = 1000;
    private SimpMessagingTemplate template;

    private String userid;

    public FileReader(String logFilePath, boolean keepReading, SimpMessagingTemplate template, String userid) {
        this.logFilePath = logFilePath;
        this.keepReading = keepReading;
        this.template = template;
        this.userid = userid;
    }

    public void setKeepReading(boolean keepReading) {
        this.keepReading = keepReading;
    }

    @Override
    public void run() {
        File file = new File(this.logFilePath);
        int readLines = 0;
        long _filePointer = 0;
        StringBuilder builder = new StringBuilder();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            long fileLen = file.length() - 1;
            if(fileLen > 0) {
                randomAccessFile.seek(fileLen);

                for (long pointer = fileLen; pointer >= 0; pointer--) {
                    randomAccessFile.seek(pointer);
                    char c;
                    c = (char) randomAccessFile.read();
                    if (c == '\n') {
                        readLines++;
                        if (readLines == NOOFLINESTOREAD)
                            break;
                    }
                    builder.append(c);
                    fileLen = fileLen - pointer;
                }
                builder.reverse();
                ResponseMessage message = new ResponseMessage(builder.toString());
                template.convertAndSendToUser(userid, "/topic/private-messages", message);
                _filePointer = file.length() - 1;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            while (keepReading) {
                Thread.sleep(_updateInterval);
                long len = file.length();
                if (len < _filePointer) {
                    this.keepReading = false;
                }
                else if (len > _filePointer) {
                    RandomAccessFile raf = new RandomAccessFile(file, "r");
                    raf.seek(_filePointer);
                    String line = null;
                    while ((line = raf.readLine()) != null) {
                        ResponseMessage message = new ResponseMessage(line);
                        template.convertAndSendToUser(userid,"/topic/private-messages", message);
                    }
                    _filePointer = raf.getFilePointer();
                    raf.close();
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
