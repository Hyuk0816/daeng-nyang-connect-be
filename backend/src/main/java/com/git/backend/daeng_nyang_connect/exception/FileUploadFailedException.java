package com.git.backend.daeng_nyang_connect.exception;

import org.apache.tomcat.util.http.fileupload.FileUploadException;

public class FileUploadFailedException extends FileUploadException {

    public FileUploadFailedException(String msg) {
        super(msg);
    }
}
