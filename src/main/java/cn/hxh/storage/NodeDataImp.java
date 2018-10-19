package cn.hxh.storage;

import cn.hxh.object.Diary;
import cn.hxh.object.Note;
import cn.hxh.storage.interfaces.NoteData;
import cn.hxh.util.HH;
import cn.hxh.util.file.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class NodeDataImp implements NoteData {
    private static final Object lock = new Object();
    private static final Logger log = LoggerFactory.getLogger(NodeDataImp.class);
    private static final String DIR_NAME = "notes";

    private ObjectMapper mapper = new ObjectMapper();
    private Map<String, String> notes = new HashMap<>();

    @PostConstruct
    public void init() {
        synchronized (lock) {
            File dir = new File(HH.resourceFilePath(DIR_NAME));
            File[] noteText = dir.listFiles();
            if (noteText == null || noteText.length == 0) {
                log.info("No notes.");
                return;
            }
            for (File f : noteText) {
                try {
                    notes.put(getTopicOfNote(f), f.getName());
                } catch (IOException e) {
                    log.warn("Fail to get note.", e);
                }
            }
        }
    }

    @Override
    public String query(String id) {
        synchronized (lock) {
            try {
                Note note = mapper.readValue(new File(notePath(id)), Note.class);
                return note.getContent();
            } catch (IOException e) {
                log.warn("Fail to query note.", e);
                return "";
            }
        }
    }

    @Override
    public boolean create(String topic) {
        synchronized (lock) {
            if (notes.containsKey(topic)) return false;
            try {
                String fileName = Long.toString(System.currentTimeMillis());
                mapper.writerWithDefaultPrettyPrinter()
                        .writeValue(new File(notePath(fileName)), new Note(topic, ""));
                notes.put(topic, fileName);
            } catch (IOException e) {
                log.warn("Fail to update note.", e);
                return false;
            }
            return true;
        }
    }

    @Override
    public boolean delete(String topic) {
        synchronized (lock) {
            if (!notes.containsKey(topic)) return false;
            String path = notePath(notes.get(topic));
            if (!new File(path).delete()) return false;
            notes.remove(topic);
            return true;
        }
    }

    @Override
    public boolean update(String fileName, String topic, String content) {
        synchronized (lock) {
            if (!notes.containsKey(topic)) return false;
            try {
                mapper.writerWithDefaultPrettyPrinter()
                        .writeValue(new File(notePath(fileName)), new Note(topic, content));
            } catch (IOException e) {
                log.warn("Fail to update note.", e);
                return false;
            }
            return true;
        }
    }


    @Override
    public Map<String, String> queryNotes() {
        synchronized (lock) {
            return notes;
        }
    }

    private String getTopicOfNote(File noteFile) throws IOException {
        Note note = mapper.readValue(noteFile, Note.class);
        return note.getTopic();
    }

    private String notePath(String noteFileName) {
        return HH.resourceFilePath(DIR_NAME) + '/' + noteFileName;
    }
}
