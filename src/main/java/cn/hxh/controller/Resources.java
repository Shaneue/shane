package cn.hxh.controller;

import cn.hxh.common.log.MyLog;
import cn.hxh.util.HH;
import cn.hxh.util.file.ZipUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.text.SimpleDateFormat;


@RestController
public class Resources {
    @Autowired
    MyLog myLog;

    @GetMapping("/resources")
    public ResponseEntity<FileSystemResource> zip() {
        File zipFile = null;
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
            myLog.record("Tried to download resources zip.");
        }
    }
}
