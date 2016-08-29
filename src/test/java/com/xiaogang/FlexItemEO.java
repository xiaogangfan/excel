package com.xiaogang;

import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 */
@Data
public class FlexItemEO extends ExcelRowIndexSupport implements Serializable {


    @NotNull(message = "所属类目不能为空")
    @ExcelColumn(name = "所属类目")
    private String fourthCategoryCode;

    @NotNull(message = "商品名称不能为空")
    @Size(max = 35, message = "商品名称不能超过35个字符")
    @ExcelColumn(name = "商品名称")
    private String title;

    @ExcelColumn(name = "副标题")
    @Size(max = 40, message = "副标题不能超过40个字符")
    private String subTitle;


    //@NotNull(message = "商品简称不能为空")
    @Size(max = 20, message = "副标题不能超过20个字符")
    @ExcelColumn(name = "商品简称")
    private String shortTitle;

    @NotNull(message = "财务分类不能为空")
    @ExcelColumn(name = "财务分类")
    private String financeTypeCode;

    @NotNull(message = "保质期不能为空")
    @ExcelColumn(name = "保质期")
    @Min(value = 0, message="保质期必须为正整数或零")
    private Integer period;


    @ExcelColumn(name = "商品条码")
    private String barcode;

    @ExcelColumn(name = "溯源国标码")
    @Size(max=8, message = "溯源国标码不能超过8个字符")
    private String sourceCode;

    //@NotNull(message = "商品品牌不能为空")
    @ExcelColumn(name = "商品品牌")
    private String brandName;

    @ExcelColumn(name = "产地")
    @Size(max=15, message = "产地不能超过15个字符")
    private String produceArea;

    @NotNull(message = "税率不能为空")
    @ExcelColumn(name = "税率")
    private String taxRateName;

    @NotNull(message = "存储条件不能为空")
    @Size(max=50, message = "存储条件不能超过50个字符")
    @ExcelColumn(name = "存储条件")
    private String storageName;

    @ExcelColumn(name = "净含量")
    private String content;

    @ExcelColumn(name = "食用方法")
    @Size(max=20, message = "食用方法不能超过20个字符")
    private String eatMethod;

    @ExcelColumn(name = "是否进口商品", defaultValue = "是")
    private String isImportName;

    @ExcelColumn(name = "产品标准号")
    private String designCode;

    @ExcelColumn(name = "厂方货号")
    private String goodNo;

    @ExcelColumn(name = "产品等级")
    private String level;

    @ExcelColumn(name = "产品长宽高")
    private String size;

    @Size(max=200, message = "成份不能超过200个字符")
    @ExcelColumn(name = "成份")
    private String mix;

    @NotNull(message = "生产者名称不能为空")
    @Size(max=30, message = "生产者名称不能超过30个字符")
    @ExcelColumn(name = "生产者名称")
    private String factory;


    @NotNull(message = "生产地址不能为空")
    @Size(max=30, message = "生产地址不能超过30个字符")
    @ExcelColumn(name = "生产地址")
    private String contact;

    @NotNull(message = "是否可采不能为空")
    @ExcelColumn(name = "是否可采", defaultValue = "是")
    private String isPurchaseName;

    @NotNull(message = "是否可配货不能为空")
    @ExcelColumn(name = "是否可配货", defaultValue = "是")
    private String isPickName;

    @NotNull(message = "是否可退供应商不能为空")
    @ExcelColumn(name = "是否可退供应商", defaultValue = "是")
    private String isReturnSupplierName;

    @NotNull(message = "是否可销售不能为空")
    @ExcelColumn(name = "是否可销售", defaultValue = "是")
    private String isSaleName;


    @NotNull(message = "是否称重商品不能为空")
    @ExcelColumn(name = "是否APP可售", defaultValue = "是")
    private String isAppSaleName;


    @NotNull(message = "是否加工商品不能为空")
    @ExcelColumn(name = "是否加工商品", defaultValue = "否")
    private String isHandingName;



    @NotNull(message = "是否自动补货不能为空")
    @ExcelColumn(name = "是否自动补货")
    private String isReplenishmentName;





    @NotNull(message = "采购单位不能为空")
    @ExcelColumn(name = "采购单位")
    private String purchaseUnitName;

    @NotNull(message = "采购规格不能为空")
    @ExcelColumn(name = "采购规格")
    @Digits(integer=Integer.MAX_VALUE, fraction=4, message = "采购规格精确到小数点后四位")
    @DecimalMin(value="0.0001", message = "采购规格必须大于0")
    private BigDecimal purchaseSpec;

    @NotNull(message = "采购单价不能为空")
    @ExcelColumn(name = "采购单价", defaultValue = "0")
    @Digits(integer=Integer.MAX_VALUE, fraction=4, message = "采购单价精确到小数点后四位")
    @DecimalMin(value="0", message = "采购单价必须大于等于0")
    private BigDecimal perUnitPrice;

    @ExcelColumn(name = "默认供应商")
    private String defaultSupplierCode;

    @NotNull(message = "最小起订量不能为空")
    @ExcelColumn(name = "最小起订量", defaultValue = "0")
    @Digits(integer=Integer.MAX_VALUE, fraction=4, message = "最小起订量精确到小数点后四位")
    @DecimalMin(value="0", message = "最小起订量必须大于等于0")
    private BigDecimal minOrderNo;

    @ExcelColumn(name = "货物验收标准")
    private String goodsAcceptCriteria;

    @NotNull(message = "是否定制品不能为空")
    @ExcelColumn(name = "是否定制品", defaultValue = "是")
    private String isCustomName;

    @NotNull(message = "存储单位不能为空")
    @Size(max = 30, message = "存储单位最多30个字符")
    @ExcelColumn(name = "存储单位")
    private String storageUnitName;

    @NotNull(message = "配货单位不能为空")
    @ExcelColumn(name = "配货单位")
    private String pickUnitName;

    @NotNull(message = "配货规格不能为空")
    @ExcelColumn(name = "配货规格")
    @DecimalMin(value = "0", message = "配货规格为非负数")
    private BigDecimal pickSpec;

    @NotNull(message = "存货性质不能为空")
    @ExcelColumn(name = "存货性质")
    private String stockNatureName;

    @NotNull(message = "默认配送方式不能为空")
    @ExcelColumn(name = "默认配送方式")
    private String deliveryPlanName;

    @ExcelColumn(name = "默认物流中心")
    private String logisticsCenterCode;


    @NotNull(message = "商品标签类型不能为空")
    @ExcelColumn(name = "商品标签类型", defaultValue = "1")
    private String labelTypeCode;

    @NotNull(message = "标价签类型不能为空")
    @ExcelColumn(name = "标价签类型", defaultValue = "1")
    private String labelPriceTypeCode;



    @NotNull(message = "是否称重商品不能为空")
    @ExcelColumn(name = "是否称重商品", defaultValue = "否")
    private String isSaleByWeightName;


    @NotNull(message = "售卖单位不能为空")
    @ExcelColumn(name = "售卖单位")
    private String saleUnitName;//默认值 不填写默认为储存单位


    //@NotNull(message = "销售规格描述不能为空")
    @Size(max=20, message = "销售规格描述不能超过20个字符")
    @ExcelColumn(name = "销售规格描述")
    private String saleSpecDesc;//默认值，默认等于含量


    @NotNull(message = "最低售价不能为空")
    @DecimalMin(value = "0", message = "最低售价为非负数")
    @ExcelColumn(name = "最低售价", defaultValue = "0")
    @Digits(integer=Integer.MAX_VALUE, fraction=4, message = "最低售价精确到小数点后四位")
    private BigDecimal minSalePrice;


    @NotNull(message = "APP最小起购量不能为空")
    @ExcelColumn(name = "APP最小起购量", defaultValue = "1")
    private BigDecimal minBuyNoByApp;


    @NotNull(message = "购买步长不能为空")
    @ExcelColumn(name = "购买步长", defaultValue = "1")
    private BigDecimal buyStepByApp;


    @ExcelColumn(name = "SPU商品编码")
    private String spuItemCode;

    @ExcelColumn(name = "均重")
    private BigDecimal avgWeight;
//    如果销售单位和储存单位不一致，则均重和预扣款重量必填。否则默认为1.
//            比如：来了一批鱼按照条卖，每条鱼均重约1.2kg


    @DecimalMin(value = "0", message = "预扣款重量为非负数")
    @ExcelColumn(name = "预扣款重量")
    private BigDecimal preDebitWeight;//销售单位和储存单位不一致时要填写


    @NotNull(message = "是否联销不能为空")
    @ExcelColumn(name = "是否联销", defaultValue = "否")
    private String isUnionSaleGoodsName;




    @NotNull(message = "是否大件商品不能为空")
    @ExcelColumn(name = "是否大件商品", defaultValue = "否")
    private String isLargeItemName;



    @Size(max = 50, message = "服务描述不能超过50个字符")
    @ExcelColumn(name = "服务描述")
    private String serviceDesc;


    @ExcelColumn(name = "加工单位")
    private String processUnitName;



    @DecimalMin(value = "0", message = "加工换算率为非负数")
    @ExcelColumn(name = "加工换算率")
    @Digits(integer=Integer.MAX_VALUE, fraction=3, message = "加工换算率精确到小数点后三位")
    private BigDecimal processConvertRate;//除以100


    @DecimalMin(value = "0", message = "考核成本价为非负数")
    @ExcelColumn(name = "考核成本价")
    private BigDecimal checkCostPrice;


    @ExcelColumn(name = "成本单位")
    private String costUnitName;

    @DecimalMin(value = "0", message = "成本换算率为非负数")
    @ExcelColumn(name = "成本换算率")
    @Digits(integer=Integer.MAX_VALUE, fraction=3, message = "成本换算率精确到小数点后三位")
    private BigDecimal costConvertRate;//除以100




    @DecimalMin(value = "0", message = "出料率必须为0-1的小数")
    @DecimalMax(value = "1", message = "出料率必须为0-1的小数")
    @Digits(integer=Integer.MAX_VALUE, fraction=3, message = "出料率精确到小数点后三位")
    @ExcelColumn(name = "出料率")
    private BigDecimal outRate;//除以100



}
