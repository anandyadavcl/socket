package com.anand.socket.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

@Service
public class LogFileReader {
    private long fileLength = 0;
    private String logFilePath = "/Users/anandyadav/Downloads/socket/log/test.log";


    public void readFileFromPoint(SimpMessagingTemplate template){
        File file = new File(this.logFilePath);
        long currentFileLength = file.length() - 1;
        if(currentFileLength > this.fileLength) {
            StringBuilder builder = new StringBuilder();
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
                randomAccessFile.seek(this.fileLength);
                for (long pointer = this.fileLength; pointer <= currentFileLength; pointer++) {
                    randomAccessFile.seek(pointer);
                    char c;
                    c = (char) randomAccessFile.read();
                    builder.append(c);
                }
                System.out.println(builder);
                if (!builder.isEmpty()) {
                    template.convertAndSend("/topic/logs", builder.toString());
                }
                this.fileLength = currentFileLength;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void readLastLines(SimpMessagingTemplate template) {
        File file = new File(this.logFilePath);
        int lines = 10;
        int readLines = 0;
        StringBuilder builder = new StringBuilder();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            this.fileLength = file.length() - 1;
            // Set the pointer at the last of the file
            randomAccessFile.seek(fileLength);

            for (long pointer = fileLength; pointer >= 0; pointer--) {
                randomAccessFile.seek(pointer);
                char c;
                c = (char) randomAccessFile.read();
                if (c == '\n') {
                    readLines++;
                    if (readLines == lines)
                        break;
                }
                builder.append(c);
                fileLength = fileLength - pointer;
            }
            builder.reverse();
            template.convertAndSend("/topic/greetings",builder.toString());
            this.fileLength = file.length() - 1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
