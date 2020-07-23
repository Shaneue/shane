package cn.hxh.storage;

import cn.hxh.common.Constants;
import cn.hxh.object.Diary;
import cn.hxh.storage.interfaces.DiaryData;
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
public class DiaryDataImp implements DiaryData {
    private static final Logger log = LoggerFactory.getLogger(DiaryDataImp.class);
    public static final Object lock = new Object();

    private final Map<Diary.Key, Diary> diaryMap = new HashMap<>();

    private ObjectMapper mapper = new ObjectMapper();
    final Constants constants;

    public DiaryDataImp(Constants constants) {
        this.constants = constants;
    }

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

    public void cleanAndInit() {
        diaryMap.clear();
        init();
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
            // if (diaryMap.containsKey(diary.getDate())) return false;
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
    public List<String> query(int year, int month) {
        synchronized (lock) {
            List<String> keys = new ArrayList<>();
            diaryMap.forEach((key, value) -> {
                if (key.getYear() == year && key.getMonth() == month) {
                    keys.add(String.format("%04d-%02d-%02d", key.getYear(), key.getMonth(), key.getDate()));
                }
            });
            return keys;
        }
    }

    @Override
    public List<String> queryAll() {
        synchronized (lock) {
            List<String> keys = new ArrayList<>();
            diaryMap.forEach((key, value) -> keys.add(String.format("%04d-%02d-%02d", key.getYear(), key.getMonth(), key.getDate())));
            keys.sort(Comparator.reverseOrder());
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
