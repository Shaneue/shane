package cn.hxh.storage;

import cn.hxh.storage.interfaces.NoteData;
import cn.hxh.util.HH;
import cn.hxh.util.file.FileUtil;
import org.springframework.stereotype.Component;

@Component
public class NodeDataImp implements NoteData {
    Object lock = new Object();

    private static final String FILE_NAME = "note";

    @Override
    public String query() {
        synchronized (lock) {
            return FileUtil.readFile(HH.resourceFilePath(FILE_NAME));
        }
    }

    @Override
    public void update(String note) {
        synchronized (lock) {
            FileUtil.writeOut(HH.resourceFilePath(FILE_NAME), note);
        }
    }
}
