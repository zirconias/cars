package com.stefanski.cars.api;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * Error response compatible with Spring Actuator.
 *
 * @author Dariusz Stefanski
 */
@Data
@ApiModel
public class ErrorResp {

    @ApiModelProperty(value = "short error description", required = true)
    private String error;

    @ApiModelProperty(value = "more details about error", required = true)
    private String message;

    @ApiModelProperty(value = "HTTP status code", required = true)
    private int status;

    @ApiModelProperty(value = "timestamp of error", required = true)
    private long timestamp;

    public ErrorResp(String error, String message, HttpStatus status) {
        this.error = error;
        this.message = message;
        this.status = status.value();
        this.timestamp = System.currentTimeMillis();
    }
}
