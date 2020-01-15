package cn.hxh.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response {
    @JsonProperty
    int status;
    @JsonProperty
    String message;
    @JsonProperty
    Object data;

    public Response() {
        this.status = Constants.SUCCESS_STATUS;
        this.message = Constants.SUCCESS;
        this.data = null;
    }

    public Response(Object data) {
        this.status = Constants.SUCCESS_STATUS;
        this.message = Constants.SUCCESS;
        this.data = data;
    }

    public Response(int status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public void setFailure() {
        this.status = Constants.FAILURE_STATUS;
        this.message = Constants.FAILURE;
    }

    public void setFailure(String message) {
        this.status = Constants.FAILURE_STATUS;
        this.message = message;
    }

    public static Response failure(Object data) {
        return new Response(Constants.FAILURE_STATUS, Constants.FAILURE, data);
    }
}
