package com.thinktag.user.exception;

import java.util.Date;

public class ServiceException {

    private Date timestamp;
    private String message;
    private String details;

    public ServiceException(Date timestamp, String message, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }
}
