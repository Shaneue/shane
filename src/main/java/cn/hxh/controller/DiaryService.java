package cn.hxh.controller;

import cn.hxh.common.Constants;
import cn.hxh.common.Response;
import cn.hxh.common.log.Log;
import cn.hxh.object.Diary;
import cn.hxh.storage.interfaces.DiaryData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class DiaryService {
    private final DiaryData diaryData;
    private final Log myLog;

    @Autowired
    public DiaryService(DiaryData diaryData, Log myLog) {
        this.diaryData = diaryData;
        this.myLog = myLog;
    }

    @ResponseBody
    @GetMapping(value = "/diary/{year}/{month}/{date}")
    public Response get(@PathVariable("year") int year,
                        @PathVariable("month") int month,
                        @PathVariable("date") int date) {
        Diary diary = diaryData.query(new Diary.Key(year, month, date));
        if (diary == null) {
            myLog.record(String.format("Query diary unsuccessfully-> %s-%s-%s", year, month, date));
            return new Response(-1, Constants.FAILURE, null);
        } else {
            myLog.record(String.format("Query diary successfully-> %s-%s-%s", year, month, date));
            return new Response(diary);
        }
    }

    @ResponseBody
    @GetMapping(value = "/diary/{year}/{month}")
    @Validated
    public Response get(@PathVariable("year") int year,
                        @PathVariable("month") int month) {
        myLog.record(String.format("Query diary list successfully-> %s-%s", year, month));
        return new Response(diaryData.query(year, month));
    }

    @ResponseBody
    @DeleteMapping(value = "/diary/{year}/{month}/{date}")
    public Response delete(@PathVariable("year") int year,
                           @PathVariable("month") int month,
                           @PathVariable("date") int date) {
        Response re = new Response();
        if (diaryData.delete(new Diary.Key(year, month, date))) {
            myLog.record(String.format("Delete diary successfully-> %s-%s-%s", year, month, date));
        } else {
            re.setFailure();
            myLog.record(String.format("Delete diary unsuccessfully-> %s-%s-%s", year, month, date));
        }
        return re;
    }

    @ResponseBody
    @PostMapping(value = "/diary")
    public Response create(@RequestBody @Valid Diary diary) {
        Response re = new Response();
        if (diaryData.create(diary)) {
            myLog.record(String.format("Create diary successfully-> %s", diary.getDate().toString()));
        } else {
            re.setFailure();
            myLog.record(String.format("Create diary unsuccessfully-> %s", diary.getDate().toString()));
        }
        return re;
    }


    @ResponseBody
    @PutMapping(value = "/diary")
    public Response update(@RequestBody @Valid Diary diary) {
        Response re = new Response();
        if (diaryData.update(diary)) {
            myLog.record(String.format("Update diary successfully-> %s", diary.getDate().toString()));
        } else {
            re.setFailure();
            myLog.record(String.format("Update diary unsuccessfully-> %s", diary.getDate().toString()));
        }
        return re;
    }
}
