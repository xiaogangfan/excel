package com.xiaogang;

import lombok.Data;

import java.io.Serializable;

/**
 * created by xiaogangfan
 * on 16/8/26.
 */
@Data
public class TableStructor extends ExcelRowIndexSupport implements Serializable {

    @ExcelColumn(name = "列名")
    private String columName;
    @ExcelColumn(name = "类型")
    private String type;
    @ExcelColumn(name = "长度")
    private String length;
    @ExcelColumn(name = "是否为空")
    private String isNull;
    @ExcelColumn(name = "缺省值")
    private String defaultValue;
    @ExcelColumn(name = "描述")
    private String desc;

}
