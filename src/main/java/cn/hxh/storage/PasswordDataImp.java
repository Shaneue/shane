package cn.hxh.storage;

import cn.hxh.common.Constants;
import cn.hxh.common.Response;
import cn.hxh.object.Password;
import cn.hxh.storage.interfaces.PasswordData;
import cn.hxh.util.HH;
import cn.hxh.util.Privacy;
import cn.hxh.util.file.FileUtil;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class PasswordDataImp implements PasswordData {
    private static Logger log = LoggerFactory.getLogger(PasswordDataImp.class);
    @Autowired
    Constants constants;

    @Override
    public boolean create(Password password, String code) {
        synchronized (DiaryDataImp.lock) {
            Map<String, Password> passwordMap = getPasswords(code);
            if (passwordMap == null) return false;
            passwordMap.put(UUID.randomUUID().toString(), password);
            if (savePassword(passwordMap, code)) return false;
        }
        return true;
    }

    private boolean savePassword(Map<String, Password> passwordMap, String code) {
        moveToBackup();
        String content;
        try {
            content = Privacy
                    .encrypt(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsBytes(passwordMap),
                            code.getBytes());
        } catch (Exception e) {
            log.warn("Fail to encrypt new password");
            return true;
        }
        FileUtil.writeOut(getEncryptedFile().getPath(), content);
        return false;
    }

    @Override
    public boolean delete(String id, String code) {
        synchronized (DiaryDataImp.lock) {
            Map<String, Password> passwordMap = getPasswords(code);
            if (passwordMap == null || passwordMap.isEmpty()) return false;
            if (!passwordMap.containsKey(id)) return false;
            passwordMap.remove(id);
            if (savePassword(passwordMap, code)) return false;
        }
        return true;
    }

    @Override
    public boolean update(String id, Password password, String code) {
        synchronized (DiaryDataImp.lock) {
            Map<String, Password> passwordMap = getPasswords(code);
            if (passwordMap == null || !passwordMap.containsKey(id)) return false;
            passwordMap.remove(id);
            passwordMap.put(UUID.randomUUID().toString(), password);
            if (savePassword(passwordMap, code)) return false;
        }
        return true;
    }

    @Override
    public Response changeCode(String oldCode, String newCode) {
        synchronized (DiaryDataImp.lock) {
            Map<String, Password> passwords = getPasswords(oldCode);
            if (passwords == null) {
                return new Response(Constants.FAILURE_STATUS, Constants.FAILURE_CODE, null);
            }
            if (savePassword(passwords, newCode)) return new Response(Constants.FAILURE_STATUS, Constants.FAIL_SAVE, null);
            return new Response();
        }
    }

    public static Map<String, Password> getPasswords(String code) {
        synchronized (DiaryDataImp.lock) {
            String content = FileUtil.readFile(getEncryptedFile().getPath());
            byte[] decrypted;
            try {
                decrypted = Privacy.decrypt(content, code.getBytes());
            } catch (Exception e) {
                log.error("Code is wrong");
                return null;
            }
            ObjectMapper mapper = new ObjectMapper();
            JavaType type = mapper.getTypeFactory()
                    .constructParametricType(HashMap.class, String.class, Password.class);
            Map<String, Password> passwords;
            try {
                passwords = mapper.readValue(decrypted, type);
            } catch (IOException e) {
                log.error("Fail to get file of passwords", e);
                return null;
            }
            return passwords;
        }
    }

    private static void moveToBackup() {
        synchronized (DiaryDataImp.lock) {
            File file = getEncryptedFile();
            String backFileName =
                    file.getName() + new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis());
            String backPath = HH.backupDir() + backFileName;
            File backFile = new File(backPath);
            try {
                Files.copy(file.toPath(), backFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            log.info("Back up file : {}", backFile.getName());
        }
    }

    private static File getEncryptedFile() {
        File file = new File(HH.resourceFilePath(Constants.ENCRYPTED));
        if (!file.exists()) {
            try {
                file.createNewFile();
                String content;
                content = Privacy.encrypt("{}".getBytes(), Constants.CODE_INIT.getBytes());
                FileUtil.writeOut(HH.resourceFilePath(Constants.ENCRYPTED), content);
                log.info("File of encrypted has been initialized");
            } catch (Exception e) {
                log.warn("Fail to encrypt this file", e);
            }
        }
        return file;
    }
}
