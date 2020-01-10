package cn.hxh.common.aspect;

import cn.hxh.common.FileValid;
import cn.hxh.common.Response;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@Slf4j
public class Validation {

    @Around(value = "@annotation(valid) && args(zipFile)")
    public Response doValidFile(ProceedingJoinPoint point, FileValid valid, MultipartFile zipFile) throws Throwable {
        if (zipFile == null) {
            log.error("No uploadFile in multipart.");
        } else {
            String fileName = zipFile.getOriginalFilename();
            assert fileName != null;
            String format = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

            String[] allow = valid.allow();
            List<String> allows = Arrays.asList(allow);
            if (!allows.contains(format.toLowerCase())) {
                return Response.failure("Format is not allowed");
            }

            long size = valid.max();
            if (size != 0) {
                size = size * 1024 * 1024;
                long fileSize = zipFile.getSize();
                if (fileSize > size) {
                    return Response.failure("File is too large");
                }
            }
        }
        return (Response) point.proceed();
    }

}
