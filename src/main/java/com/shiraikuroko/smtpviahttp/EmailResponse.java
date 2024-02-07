package com.shiraikuroko.smtpviahttp;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailResponse {
    @JsonProperty
    boolean success;
    @JsonProperty
    String exception;

    EmailResponse(boolean success,String exception){
        this.success = success;
        this.exception = exception;
    }
}
