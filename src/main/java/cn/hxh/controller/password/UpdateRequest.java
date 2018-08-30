package cn.hxh.controller.password;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class UpdateRequest extends CreateRequest {
    @JsonProperty
    @NotBlank
    String id;

    public String getId() {
        return id;
    }
}
