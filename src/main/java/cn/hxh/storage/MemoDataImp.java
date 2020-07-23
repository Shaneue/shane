package cn.hxh.storage;

import cn.hxh.common.Constants;
import cn.hxh.common.Response;
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
import java.util.*;

@Component
public class MemoDataImp implements MemoData {
    private static final Logger log = LoggerFactory.getLogger(MemoDataImp.class);
    private static final String DIR_NAME = "memos";

    private ObjectMapper mapper = new ObjectMapper();
    private List<Map<String, String>> memos = new ArrayList<>();
    final Constants constants;

    public MemoDataImp(Constants constants) {
        this.constants = constants;
    }

    @PostConstruct
    public void init() {
        synchronized (DiaryDataImp.lock) {
            File dir = memoFile();
            File[] memoText = dir.listFiles();
            if (memoText == null || memoText.length == 0) {
                log.info("No memos.");
                return;
            }
            for (File f : memoText) {
                try {
                    Memo memo = getMemo(f);
                    Map<String, String> tmp = new HashMap<>();
                    tmp.put("topic", memo.getTopic());
                    tmp.put("time", memo.getTime());
                    tmp.put("k", f.getName());
                    memos.add(tmp);
                } catch (IOException e) {
                    log.warn("Fail to get memo.", e);
                }
            }
        }
    }

    @Override
    public void cleanAndInit() {
        memos.clear();
        init();
    }

    @Override
    public String query(String id) {
        synchronized (DiaryDataImp.lock) {
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
        synchronized (DiaryDataImp.lock) {
            for (Map<String, String> map : memos) {
                if (map.get("topic").equals(topic)) return false;
            }
            try {
                String fileName = Long.toString(System.currentTimeMillis());
                mapper.writerWithDefaultPrettyPrinter()
                        .writeValue(new File(memoPath(fileName)), new Memo(topic, ""));
                Map<String, String> tmp = new HashMap<>();
                tmp.put("topic", topic);
                tmp.put("time", HH.timeNow());
                tmp.put("k", fileName);
                memos.add(tmp);
            } catch (IOException e) {
                log.warn("Fail to update memo.", e);
                return false;
            }
            return true;
        }
    }

    @Override
    public boolean delete(String topic) {
        synchronized (DiaryDataImp.lock) {
            Map<String, String> tmp = getMemoMap(topic);
            if (tmp == null) return false;
            String path = memoPath(tmp.get("k"));
            if (!new File(path).delete()) return false;
            Iterator<Map<String, String>> iterator = memos.iterator();
            while (iterator.hasNext()) {
                if (topic.equals(iterator.next().get("topic"))) {
                    iterator.remove();
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public boolean update(String fileName, String topic, String content) {
        synchronized (DiaryDataImp.lock) {
            Map<String, String> tmp = getMemoMap(topic);
            if (tmp == null) return false;
            try {
                mapper.writerWithDefaultPrettyPrinter()
                        .writeValue(new File(memoPath(fileName)), new Memo(topic, content));
                memos.clear();
                this.init();
            } catch (IOException e) {
                log.warn("Fail to update memo.", e);
                return false;
            }
            return true;
        }
    }

    @Override public Response reName(String oldName, String newName) {
        Map<String, String> tmp = getMemoMap(oldName);
        if (tmp == null) return Response.failure("Old name does not exist.");
        String fileName = tmp.get("k");
        try {
            Memo memo = mapper.readValue(new File(memoPath(fileName)), Memo.class);
            memo.setTopic(newName);
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(memoPath(fileName)), memo);
            memos.clear();
            this.init();
        } catch (IOException e) {
            log.error(e.getMessage());
            return Response.failure(e.getMessage());
        }
        return new Response();
    }

    private Map<String, String> getMemoMap(String topic) {
        Map<String, String> tmp = null;
        for (Map<String, String> map : memos) {
            if (map.get("topic").equals(topic)) tmp = map;
        }
        return tmp;
    }


    @Override
    public List<Map<String, String>> queryMemos() {
        synchronized (DiaryDataImp.lock) {
            return memos;
        }
    }

    private Memo getMemo(File memoFile) throws IOException {
        return mapper.readValue(memoFile, Memo.class);
    }

    private String memoPath(String memoFileName) {
        return HH.resourceFilePath(DIR_NAME) + '/' + memoFileName;
    }


    private File memoFile() {
        String path = HH.resourceFilePath(DIR_NAME);
        File memoDir = new File(path);
        if (!memoDir.exists()) {
            if (!memoDir.mkdirs()) {
                log.error("Failed to make " + DIR_NAME);
            }
        }
        return memoDir;
    }
}
