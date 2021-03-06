package cn.hxh.controller;

import cn.hxh.common.FileValid;
import cn.hxh.common.Response;
import cn.hxh.storage.DiaryDataImp;
import cn.hxh.storage.interfaces.DiaryData;
import cn.hxh.storage.interfaces.MemoData;
import cn.hxh.util.HH;
import cn.hxh.util.file.FileUtil;
import cn.hxh.util.file.ZipUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;


@RestController
@Slf4j
public class Resources {
    final MemoData memoData;
    final DiaryData diaryData;

    public Resources(MemoData memoData, DiaryData diaryData) {
        this.memoData = memoData;
        this.diaryData = diaryData;
    }

    @GetMapping("/resources")
    public ResponseEntity<FileSystemResource> getResourcesZip() {
        File zipFile;
        try {
            File resourceDir = new File(HH.resourceDir());
            String zipTo = HH.temporaryDir() + "resources" +
                    new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis()) + ".zip";
            ZipUtil.zip(resourceDir, zipTo);
            zipFile = new File(zipTo);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + zipFile.getName())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM).body(new FileSystemResource(zipTo));
        } finally {
            log.info("Tried to download resources zip.");
        }
    }

    @PostMapping(value = "/resources/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @FileValid(allow = "zip", max = 10)
    public Response unzipResources(@RequestParam("zip") MultipartFile zip) {
        try {
            String zipPath = HH.temporaryDir() + File.separator + zip.getOriginalFilename();
            File zipUploaded = new File(zipPath);
            zip.transferTo(zipUploaded);
            synchronized (DiaryDataImp.lock) {
                FileUtil.deleteDirRecursively(HH.resourceDir());
                ZipUtil.unZipFiles(zipUploaded, HH.resourceDir());
                memoData.cleanAndInit();
                diaryData.cleanAndInit();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.failure(e.getMessage());
        }
        return new Response();
    }
}
