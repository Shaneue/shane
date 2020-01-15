package cn.hxh.common.aspect;

import cn.hxh.common.Constants;
import cn.hxh.common.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@ControllerAdvice
@Slf4j
public class ExceptionAdvice {
    @ExceptionHandler
    @ResponseBody
    public Response handleParamsException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> errors = bindingResult.getFieldErrors();
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Parameters exception: ");
        for (FieldError error : errors) {
            errorMessage.append(error.getField());
            errorMessage.append(":");
            errorMessage.append(error.getDefaultMessage());
            errorMessage.append(". ");
        }
        return new Response(Constants.FAILURE_STATUS, errorMessage.toString(), null);
    }
}
