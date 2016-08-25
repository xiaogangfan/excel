package com.xiaogang;

import java.io.Serializable;

/**
 * Excel数据导入支持设置行号的基础类, 如果继承了该类, 在解析时会设置行号, 否则忽略
 *
 * @author xiaogang
 * @date 2016-07-04
 */
public class ExcelRowIndexSupport implements Serializable {

    private static final long serialVersionUID = -8898780234242768487L;

    /** 数据行号, 包含表头所在行 */
    protected Integer rowIndex;

    public Integer getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(Integer rowIndex) {
        this.rowIndex = rowIndex;
    }
}
