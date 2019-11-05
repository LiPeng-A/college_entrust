package com.college.common.Exception;

import com.college.common.enums.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EntrustException extends RuntimeException {
    private ExceptionEnum exceptionEnum;
}
