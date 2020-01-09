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
        this.status = 0;
        this.message = Constants.SUCCESS;
        this.data = null;
    }

    public Response(Object data) {
        this.status = 0;
        this.message = Constants.SUCCESS;
        this.data = data;
    }

    public Response(int status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public void setFailure() {
        this.status = -1;
        this.message = Constants.FAILURE;
    }
}
