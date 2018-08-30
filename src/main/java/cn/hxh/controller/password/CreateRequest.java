package cn.hxh.controller.password;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class CreateRequest {

    @JsonProperty
    @NotBlank
    private String code;
    @JsonProperty
    @NotBlank
    private String where;
    @JsonProperty
    @NotBlank
    private String account;
    @JsonProperty
    @NotBlank
    private String password;
    @JsonProperty
    private List<Pair> ext;

    static class Pair {
        @JsonProperty
        String key;
        @JsonProperty
        String value;
    }

    public String getCode() {
        return code;
    }

    public String getWhere() {
        return where;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public List<Pair> getExt() {
        return ext;
    }
}
