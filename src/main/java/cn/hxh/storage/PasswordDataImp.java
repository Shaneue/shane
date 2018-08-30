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

    private static final Object lock = new Object();


    @Override
    public boolean create(Password password, String code) {
        synchronized (lock) {
            Map<String, Password> passwordMap = getPasswords(code);
            if (passwordMap == null || passwordMap.isEmpty()) return false;
            passwordMap.put(UUID.randomUUID().toString(), password);
            moveToBackup(Constants.ENCRYPTED);
            String content;
            try {
                content = Privacy
                        .encrypt(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsBytes(passwordMap),
                                code.getBytes());
            } catch (Exception e) {
                log.warn("Fail to encrypt new password");
                return false;
            }
            FileUtil.writeOut(HH.resourceFilePath(Constants.ENCRYPTED), content);
        }
        return true;
    }

    @Override
    public boolean delete(String id, String code) {
        synchronized (lock) {
            Map<String, Password> passwordMap = getPasswords(code);
            if (!passwordMap.containsKey(id)) return false;
            passwordMap.remove(id);
            moveToBackup(Constants.ENCRYPTED);
            String content;
            try {
                content = Privacy
                        .encrypt(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsBytes(passwordMap),
                                code.getBytes());
            } catch (Exception e) {
                log.warn("Fail to encrypt new password");
                return false;
            }
            FileUtil.writeOut(HH.resourceFilePath(Constants.ENCRYPTED), content);
        }
        return true;
    }

    @Override
    public boolean update(String id, Password password, String code) {
        synchronized (lock) {
            Map<String, Password> passwordMap = getPasswords(code);
            if (!passwordMap.containsKey(id)) return false;
            passwordMap.remove(id);
            passwordMap.put(UUID.randomUUID().toString(), password);
            moveToBackup(Constants.ENCRYPTED);
            String content;
            try {
                content = Privacy
                        .encrypt(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsBytes(passwordMap),
                                code.getBytes());
            } catch (Exception e) {
                log.warn("Fail to encrypt new password");
                return false;
            }
            FileUtil.writeOut(HH.resourceFilePath(Constants.ENCRYPTED), content);
        }
        return true;
    }

    public static Map<String, Password> getPasswords(String code) {
        synchronized (lock) {
            String content = FileUtil.readFile(HH.resourceFilePath(Constants.ENCRYPTED));
            byte[] decrypted;
            try {
                decrypted = Privacy.decrypt(content, code.getBytes());
            } catch (Exception e) {
                log.error("Code is wrong", e);
                return new HashMap<>();
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

    private static void moveToBackup(String filePath) {
        synchronized (lock) {
            File file = new File(HH.resourceFilePath(filePath));
            String path = file.getParent();
            String backFileName =
                    file.getName() + new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis());
            String backPath = path + File.separator + "backup" + File.separator + backFileName;
            File backFile = new File(backPath);
            boolean flag = file.renameTo(backFile);
            log.info("Back up file : {} -> {}", backFile.getName(), flag);
        }
    }
}
