package cn.hxh.storage.interfaces;

import cn.hxh.object.Password;

public interface PasswordData {
    boolean create(Password password, String code);

    boolean delete(String id, String code);

    boolean update(String id, Password password, String code);
}
