package com.xiaogang;

import lombok.Data;

/**
 * created by xiaogangfan
 * on 16/9/14.
 */
@Data
public class InboundEO {
    @ExcelColumn(name = "入库单号")
    private String 入库单号;

    @ExcelColumn(name = "采购订单号")
    private String 采购订单号;

    @ExcelColumn(name = "状态")
    private String 状态;

    @ExcelColumn(name = "入库机构")
    private String 入库机构;

    @ExcelColumn(name = "入库部门")
    private String 入库部门;

    @ExcelColumn(name = "供应商")
    private String 供应商;

    @ExcelColumn(name = "入库日期")
    private String 入库日期;

    @ExcelColumn(name = "要求到货日期")
    private String 要求到货日期;

    @ExcelColumn(name = "失效日期")
    private String 失效日期;

    @ExcelColumn(name = "发票状态")
    private String 发票状态;

    @ExcelColumn(name = "结算日期")
    private String 结算日期;

    @ExcelColumn(name = "商品编码")
    private String 商品编码;

    @ExcelColumn(name = "商品名称")
    private String 商品名称;

    @ExcelColumn(name = "存储单位")
    private String 存储单位;

    @ExcelColumn(name = "采购规格")
    private String 采购规格;

    @ExcelColumn(name = "采购单位")
    private String 采购单位;

    @ExcelColumn(name = "采购件数")
    private String 采购件数;

    @ExcelColumn(name = "入库件数")
    private String 入库件数;

    @ExcelColumn(name = "入库数量")
    private String 入库数量;

    @ExcelColumn(name = "生产日期")
    private String 生产日期;

    @ExcelColumn(name = "过保日期")
    private String 过保日期;

    @ExcelColumn(name = "含税采购价")
    private String 含税采购价;

    @ExcelColumn(name = "含税金额")
    private String 含税金额;

    @ExcelColumn(name = "税率")
    private String 税率;

    @ExcelColumn(name = "批次号")
    private String 批次号;

    @ExcelColumn(name = "去税采购价")
    private String 去税采购价;

    @ExcelColumn(name = "去税金额")
    private String 去税金额;

    @ExcelColumn(name = "税额")
    private String 税额;

    @ExcelColumn(name = "订单备注")
    private String 订单备注;
}
