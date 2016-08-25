package com.xiaogang;

import java.lang.annotation.*;

/**
 * @author xiaogang
 * @date 2016-06-25
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ExcelColumn {

    /**
     * 对应EXCEL的列名, 如果不设置, 默认使用字段名
     *
     * @return EXCEL的列名
     */
    String name() default "";

    /**
     * 字段的默认值
     *
     * @return 默认值
     */
    String defaultValue() default "";

}
