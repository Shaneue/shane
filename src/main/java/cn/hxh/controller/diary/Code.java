package cn.hxh.controller.diary;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class Code {
    @JsonProperty(value = "code")
    @NotBlank
    private int code = 0;

    public void setCode(int code) {
        this.code = code;
    }
}
