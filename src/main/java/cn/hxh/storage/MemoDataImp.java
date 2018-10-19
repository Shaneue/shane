package cn.hxh.storage;

import cn.hxh.object.Memo;
import cn.hxh.storage.interfaces.MemoData;
import cn.hxh.util.HH;
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
public class MemoDataImp implements MemoData {
    private static final Object lock = new Object();
    private static final Logger log = LoggerFactory.getLogger(MemoDataImp.class);
    private static final String DIR_NAME = "memos";

    private ObjectMapper mapper = new ObjectMapper();
    private Map<String, String> memos = new HashMap<>();

    @PostConstruct
    public void init() {
        synchronized (lock) {
            File dir = new File(HH.resourceFilePath(DIR_NAME));
            File[] memoText = dir.listFiles();
            if (memoText == null || memoText.length == 0) {
                log.info("No memos.");
                return;
            }
            for (File f : memoText) {
                try {
                    memos.put(getTopicOfMemo(f), f.getName());
                } catch (IOException e) {
                    log.warn("Fail to get memo.", e);
                }
            }
        }
    }

    @Override
    public String query(String id) {
        synchronized (lock) {
            try {
                Memo memo = mapper.readValue(new File(memoPath(id)), Memo.class);
                return memo.getContent();
            } catch (IOException e) {
                log.warn("Fail to query memo.", e);
                return "";
            }
        }
    }

    @Override
    public boolean create(String topic) {
        synchronized (lock) {
            if (memos.containsKey(topic)) return false;
            try {
                String fileName = Long.toString(System.currentTimeMillis());
                mapper.writerWithDefaultPrettyPrinter()
                        .writeValue(new File(memoPath(fileName)), new Memo(topic, ""));
                memos.put(topic, fileName);
            } catch (IOException e) {
                log.warn("Fail to update memo.", e);
                return false;
            }
            return true;
        }
    }

    @Override
    public boolean delete(String topic) {
        synchronized (lock) {
            if (!memos.containsKey(topic)) return false;
            String path = memoPath(memos.get(topic));
            if (!new File(path).delete()) return false;
            memos.remove(topic);
            return true;
        }
    }

    @Override
    public boolean update(String fileName, String topic, String content) {
        synchronized (lock) {
            if (!memos.containsKey(topic)) return false;
            try {
                mapper.writerWithDefaultPrettyPrinter()
                        .writeValue(new File(memoPath(fileName)), new Memo(topic, content));
            } catch (IOException e) {
                log.warn("Fail to update memo.", e);
                return false;
            }
            return true;
        }
    }


    @Override
    public Map<String, String> queryMemos() {
        synchronized (lock) {
            return memos;
        }
    }

    private String getTopicOfMemo(File memoFile) throws IOException {
        Memo memo = mapper.readValue(memoFile, Memo.class);
        return memo.getTopic();
    }

    private String memoPath(String memoFileName) {
        return HH.resourceFilePath(DIR_NAME) + '/' + memoFileName;
    }
}
