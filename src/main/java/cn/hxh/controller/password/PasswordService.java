package cn.hxh.controller.password;

import cn.hxh.object.Password;
import cn.hxh.storage.PasswordDataImp;
import cn.hxh.storage.interfaces.PasswordData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Map;

@RestController
public class PasswordService {

    @Autowired
    PasswordData passwordData;

    @PostMapping(value = "passwords")
    public String query(@RequestBody @Valid Code code) {
        return getPasswordsString(code.getCode());
    }

    @PostMapping(value = "password/{id}")
    public boolean delete(@RequestBody @Valid Code code, @PathVariable @NotBlank String id) {
        return passwordData.delete(id, code.getCode());
    }

    @PostMapping(value = "password")
    public boolean create(@RequestBody @Valid CreateRequest body) {
        Password password = new Password();
        password.setWhere(body.getWhere().getBytes());
        password.setAccount(body.getAccount().getBytes());
        password.setPassword(body.getPassword().getBytes());
        if (body.getExt() != null)
            for (CreateRequest.Pair pair : body.getExt()) {
                password.addPair(pair.key, pair.value);
            }
        return passwordData.create(password, body.getCode());
    }

    @PutMapping(value = "password")
    public boolean update(@RequestBody @Valid UpdateRequest body) {
        Password password = new Password();
        password.setWhere(body.getWhere().getBytes());
        password.setAccount(body.getAccount().getBytes());
        password.setPassword(body.getPassword().getBytes());
        if (body.getExt() != null)
            for (CreateRequest.Pair pair : body.getExt()) {
                password.addPair(pair.key, pair.value);
            }
        return passwordData.update(body.getId(), password, body.getCode());
    }

    private static String getPasswordsString(String code) {
        Map<String, Password> passwords = PasswordDataImp.getPasswords(code);
        if (passwords == null || passwords.isEmpty()) {
            return "Code is wrong.";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Password> entry : passwords.entrySet()) {
            sb.append(String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
        }
        return sb.toString();
    }
}
