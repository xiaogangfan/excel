package com.xiaogang;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author 'xiaogang'
 * @date 2016-06-25
 */
@Data
public class ExcelResolveResult<T> {

    /**
     * 解析后的数据
     */
    private List<T> data;

    /**
     * 数据校验错误信息
     */
    private String message;

    public boolean success() {
        return StringUtils.isBlank(message);
    }

    public static <T> ExcelResolveResult<T> fail(String message) {
        ExcelResolveResult<T> result = new ExcelResolveResult<>();
        result.setData(Collections.emptyList());
        result.setMessage(message);
        return result;
    }

}
