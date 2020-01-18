package cn.hxh.storage.interfaces;

import java.util.List;
import java.util.Map;

public interface MemoData {
    String query(String note);

    List<Map<String, String>> queryMemos();

    boolean create(String topic);

    boolean delete(String topic);

    boolean update(String fileName, String topic, String content);

    void cleanAndInit();
}
