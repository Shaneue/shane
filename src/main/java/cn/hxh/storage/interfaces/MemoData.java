package cn.hxh.storage.interfaces;

import cn.hxh.common.Response;

import java.util.List;
import java.util.Map;

public interface MemoData {
    String query(String note);

    List<Map<String, String>> queryMemos();

    boolean create(String topic);

    boolean delete(String topic);

    boolean update(String fileName, String topic, String content);

    Response reName(String oldName, String newName);

    void cleanAndInit();
}
