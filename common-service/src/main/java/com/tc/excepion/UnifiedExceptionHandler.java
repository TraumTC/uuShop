package com.tc.excepion;

import com.tc.util.ResultVOUtil;
import com.tc.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class UnifiedExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ResultVO handleException(Exception e) {
        return ResultVOUtil.fail(e.getMessage());
    }
}
