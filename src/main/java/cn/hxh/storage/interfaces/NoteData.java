package cn.hxh.storage.interfaces;

import java.util.Map;

public interface NoteData {
    String query(String note);

    Map<String, String> queryNotes();

    boolean create(String topic);

    boolean delete(String topic);

    boolean update(String fileName, String topic, String content);
}
