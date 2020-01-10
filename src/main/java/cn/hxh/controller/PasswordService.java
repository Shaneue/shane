package cn.hxh.controller;

import cn.hxh.common.Constants;
import cn.hxh.common.Response;
import cn.hxh.object.Password;
import cn.hxh.storage.PasswordDataImp;
import cn.hxh.storage.interfaces.PasswordData;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class PasswordService {

    private final PasswordData passwordData;

    @Autowired
    public PasswordService(PasswordData passwordData) {
        this.passwordData = passwordData;
    }

    @PostMapping(value = "passwords")
    public Response queryAll(@RequestBody @Valid Code code) {
        Map<String, Map> passwords = getPasswordMap(code.getCode());
        if (passwords == null) {
            return new Response(Constants.FAILURE_STATUS, Constants.FAILURE_CODE, null);
        }
        return new Response(passwords);
    }

    @PostMapping(value = "password/{id}")
    public Response deletePassword(@RequestBody @Valid Code code, @PathVariable @NotBlank String id) {
        Response re = new Response();
        if (passwordData.delete(id, code.getCode())) {
            log.info(String.format("Deleted password %s", id));
        } else {
            re.setFailure();
        }
        return re;
    }

    @PostMapping(value = "password")
    public Response createPassword(@RequestBody @Valid CreateRequest body) {
        Password password = convertRequestToPassword(body);
        Response re = new Response();
        if (passwordData.create(password, body.getCode())) {
            log.info(String.format("Created password %s", body.getWhere()));
        } else {
            re.setFailure();
        }
        return re;
    }

    @PutMapping(value = "password")
    public Response updatePassword(@RequestBody @Valid UpdateRequest body) {
        Password password = convertRequestToPassword(body);
        Response re = new Response();
        if (passwordData.update(body.getId(), password, body.getCode())) {
            log.info(String.format("Updated password %s", body.getWhere()));
        } else {
            re.setFailure();
        }
        return re;
    }

    private Password convertRequestToPassword(CreateRequest body) {
        Password password = new Password();
        password.setWhere(body.getWhere().getBytes());
        password.setAccount(body.getAccount().getBytes());
        password.setPassword(body.getPassword().getBytes());
        if (body.getExt() != null)
            for (CreateRequest.Pair pair : body.getExt()) {
                password.addPair(pair.key, pair.value);
            }
        return password;
    }

    private static Map<String, Map> getPasswordMap(String code) {
        Map<String, Password> passwords = PasswordDataImp.getPasswords(code);
        if (passwords == null) {
            return null;
        }
        Map<String, Map> map = new HashMap<>();
        for (Map.Entry<String, Password> entry : passwords.entrySet()) {
            Map<String, Object> p = new HashMap<>();
            p.put("w", new String(entry.getValue().getWhere()));
            p.put("a", new String(entry.getValue().getAccount()));
            p.put("p", new String(entry.getValue().getPassword()));
            if (entry.getValue().getExt() != null)
                p.put("e", entry.getValue().convertExt());
            map.put(entry.getKey(), p);
        }
        return map;
    }

    @Getter
    static class CreateRequest {
        @JsonProperty
        @NotBlank
        String code;
        @JsonProperty
        @NotBlank
        String where;
        @JsonProperty
        @NotBlank
        String account;
        @JsonProperty
        @NotBlank
        String password;
        @JsonProperty
        List<Pair> ext;

        static class Pair {
            @JsonProperty
            String key;
            @JsonProperty
            String value;
        }
    }

    @Getter
    static class UpdateRequest extends CreateRequest {
        @JsonProperty
        @NotBlank
        String id;
    }

    @Getter
    static class Code {
        @JsonProperty(value = "code")
        @NotBlank
        String code;
    }
}
