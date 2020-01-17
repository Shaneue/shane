package cn.hxh.controller;

import cn.hxh.common.Constants;
import cn.hxh.common.Response;
import cn.hxh.object.Diary;
import cn.hxh.storage.interfaces.DiaryData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
public class DiaryService {
    private final DiaryData diaryData;

    @Autowired
    public DiaryService(DiaryData diaryData) {
        this.diaryData = diaryData;
    }

    @ResponseBody
    @GetMapping(value = "/diary/{year}/{month}/{date}")
    public Response getDate(@PathVariable("year") int year,
                            @PathVariable("month") int month,
                            @PathVariable("date") int date) {
        Diary diary = diaryData.query(new Diary.Key(year, month, date));
        if (diary == null) {
            log.info(String.format("Query diary unsuccessfully-> %s-%s-%s", year, month, date));
            return new Response(Constants.FAILURE_STATUS, Constants.FAILURE, null);
        } else {
            log.info(String.format("Query diary successfully-> %s-%s-%s", year, month, date));
            return new Response(diary);
        }
    }

    @ResponseBody
    @GetMapping(value = "/diary/{year}/{month}")
    @Validated
    public Response getMonth(@PathVariable("year") int year,
                             @PathVariable("month") int month) {
        log.info(String.format("Query diary list successfully-> %s-%s", year, month));
        return new Response(diaryData.query(year, month));
    }

    @ResponseBody
    @GetMapping(value = "/diary")
    public Response getAll() {
        log.info("Query diaries successfully");
        return new Response(diaryData.queryAll());
    }

    @ResponseBody
    @DeleteMapping(value = "/diary/{year}/{month}/{date}")
    public Response deleteDate(@PathVariable("year") int year,
                               @PathVariable("month") int month,
                               @PathVariable("date") int date) {
        Response re = new Response();
        if (diaryData.delete(new Diary.Key(year, month, date))) {
            log.info(String.format("Delete diary successfully-> %s-%s-%s", year, month, date));
        } else {
            re.setFailure();
            log.info(String.format("Delete diary unsuccessfully-> %s-%s-%s", year, month, date));
        }
        return re;
    }

    @ResponseBody
    @PostMapping(value = "/diary")
    public Response createDiary(@RequestBody @Valid Diary diary) {
        Response re = new Response();
        if (diaryData.create(diary)) {
            log.info(String.format("Create diary successfully-> %s", diary.getDate().toString()));
        } else {
            re.setFailure();
            log.info(String.format("Create diary unsuccessfully-> %s", diary.getDate().toString()));
        }
        return re;
    }


    @ResponseBody
    @PutMapping(value = "/diary")
    public Response updateDiary(@RequestBody @Valid Diary diary) {
        Response re = new Response();
        if (diaryData.update(diary)) {
            log.info(String.format("Update diary successfully-> %s", diary.getDate().toString()));
        } else {
            re.setFailure();
            log.info(String.format("Update diary unsuccessfully-> %s", diary.getDate().toString()));
        }
        return re;
    }
}
