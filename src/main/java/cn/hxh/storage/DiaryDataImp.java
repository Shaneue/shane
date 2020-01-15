package cn.hxh.storage;

import cn.hxh.common.Constants;
import cn.hxh.object.Diary;
import cn.hxh.storage.interfaces.DiaryData;
import cn.hxh.util.HH;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DiaryDataImp implements DiaryData {
    private static final Logger log = LoggerFactory.getLogger(DiaryDataImp.class);
    static final Object lock = new Object();

    private final Map<Diary.Key, Diary> diaryMap = new HashMap<>();

    private ObjectMapper mapper = new ObjectMapper();
    @Autowired
    Constants constants;

    @PostConstruct
    public void init() {
        synchronized (lock) {
            File diaryDir = diaryFile();
            File[] diaries = diaryDir.listFiles();
            if (diaries != null) {
                for (File diaryFile : diaries) {
                    Diary diary;
                    try {
                        diary = mapper.readValue(diaryFile, Diary.class);
                    } catch (IOException e) {
                        log.warn("Fail to new diary.", e);
                        continue;
                    }
                    diaryMap.put(diary.getDate(), diary);
                }
            }
        }
    }

    @Override
    public boolean delete(Diary.Key date) {
        synchronized (lock) {
            if (!diaryMap.containsKey(date)) return false;
            String path = HH.resourceFilePath(Constants.DIARY) + File.separator + date.toString();
            if (!new File(path).delete()) return false;
            diaryMap.remove(date);
        }
        return true;
    }

    @Override
    public boolean create(Diary diary) {
        synchronized (lock) {
            if (diaryMap.containsKey(diary.getDate())) return false;
            try {
                saveDiary(diary);
            } catch (IOException e) {
                log.warn("Fail to create diary.", e);
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean update(Diary diary) {
        synchronized (lock) {
            if (!diaryMap.containsKey(diary.getDate())) return false;
            try {
                saveDiary(diary);
            } catch (IOException e) {
                log.warn("Fail to update diary.", e);
                return false;
            }
        }
        return true;
    }

    private void saveDiary(Diary diary) throws IOException {
        String savePath = HH.resourceFilePath(Constants.DIARY) + File.separator + diary.getDate().toString();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(savePath), diary);
        diaryMap.put(diary.getDate(), diary);
    }


    @Override
    public Diary query(Diary.Key date) {
        synchronized (lock) {
            if (!diaryMap.containsKey(date)) return null;
            return diaryMap.get(date);
        }
    }

    @Override
    public List<Integer> query(int year, int month) {
        synchronized (lock) {
            List<Integer> keys = new ArrayList<>();
            for (Map.Entry entry : diaryMap.entrySet()) {
                Diary.Key key = (Diary.Key) entry.getKey();
                if (key.getYear() == year && key.getMonth() == month) {
                    keys.add(key.getDate());
                }
            }
            return keys;
        }
    }

    private File diaryFile() {
        String path = HH.resourceFilePath(Constants.DIARY);
        File diaryDir = new File(path);
        if (!diaryDir.exists()) {
            if (!diaryDir.mkdirs()) {
                log.error("Failed to mkdir");
            }
        }
        return diaryDir;
    }
}
