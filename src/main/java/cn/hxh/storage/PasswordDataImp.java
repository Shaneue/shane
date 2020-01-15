package cn.hxh.storage;

import cn.hxh.common.Constants;
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
        FileUtil.writeOut(HH.resourceFilePath(Constants.ENCRYPTED), content);
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

    public static Map<String, Password> getPasswords(String code) {
        synchronized (DiaryDataImp.lock) {
            String content = FileUtil.readFile(HH.resourceFilePath(Constants.ENCRYPTED));
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
            File file = new File(HH.resourceFilePath(Constants.ENCRYPTED));
            String backFileName =
                    file.getName() + new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis());
            String backPath = HH.backupDir() + backFileName;
            File backFile = new File(backPath);
            boolean flag = file.renameTo(backFile);
            log.info("Back up file : {} -> {}", backFile.getName(), flag);
        }
    }
}
