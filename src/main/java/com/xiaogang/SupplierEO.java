package com.xiaogang;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author 'xiaogang'
 * @date 2016-06-22
 */
@Data
public class SupplierEO implements Serializable {

    private static final long serialVersionUID = -4379843075130415968L;

    // ============基础信息============
    @ExcelColumn(name = "供应商全称")
    private String companyName; // 供应商全称
    @ExcelColumn(name = "简称")
    private String shortName; // 简称
    @ExcelColumn(name = "企业性质", defaultValue = "1")
    private Integer companyNature; // 企业性质, 包括制造商、一级经销商、二级经销商、三级经销商、一级代理商、二级代理商、三级代理商
    @ExcelColumn(name = "企业类别", defaultValue = "1")
    private Integer companyCategory; // 企业类别, 选项包括：公司、个体工商户、合伙、外商投资企业、国有企业。不维护，则默认为公司。
    @ExcelColumn(name = "实收资本")
    private BigDecimal paidinCapital; // 实收资本（万）
    @ExcelColumn(name = "注册资本")
    private BigDecimal registeredCapital; // 注册资本（万）
    @ExcelColumn(name = "员工数量")
    private Integer staffCount; // 员工数量
    @ExcelColumn(name = "公司法人")
    private String legalPerson; // 法人
    @ExcelColumn(name = "公司电话")
    private String telephone; // 公司电话
    @ExcelColumn(name = "公司传真")
    private String fax; // 公司传真
    @ExcelColumn(name = "成立时间")
    private String establishedDate; // 成立时间
    @ExcelColumn(name = "注册地址")
    private String registeredAddress; // 注册地址, 不超过30个汉字
    @ExcelColumn(name = "邮编")
    private String postCode; // 邮编
    @ExcelColumn(name = "公司收货地址")
    private String address; // 公司收货地址
    @ExcelColumn(name = "对应采购员")
    private String buyer; // 对应采购员, 工号或花名

    // 这两个字段用于暂存查询出来的工号或花名, 不需要导入
    private String buyerId;
    private String buyerName;

    // ============联系人信息============
    @ExcelColumn(name = "业务员")
    private String operatorName; // 业务员
    @ExcelColumn(name = "业务员电话")
    private String operatorPhone; // 业务员电话
    @ExcelColumn(name = "业务员EMAIL")
    private String operatorEmail; // 业务员EMAIL
    @ExcelColumn(name = "业务员旺旺")
    private String operatorWangWang; // 业务员旺旺
    @ExcelColumn(name = "负责人")
    private String managerName; // 负责人
    @ExcelColumn(name = "负责人电话")
    private String managerPhone; // 负责人电话
    @ExcelColumn(name = "负责人EMAIL")
    private String managerEmail; // 负责人EMAIL
    @ExcelColumn(name = "负责人旺旺")
    private String managerWangWang; // 负责人旺旺

    // ============采购、配送信息============
    @ExcelColumn(name = "默认经营方式")
    private Integer defaultOperateType; // 默认经营方式 1 经销 2 联销
    @ExcelColumn(name = "订单失效天数")
    private Integer orderExpireDays; // 订单失效天数
    @ExcelColumn(name = "默认订货频率")
    private Integer orderFrequency; // 默认订货频率
    @ExcelColumn(name = "默认送货天数")
    private Integer deliverDays; // 默认送货天数
    @ExcelColumn(name = "起订金额", defaultValue = "0")
    private BigDecimal minimumAmount; // 起订金额, 起订金额或者起订数量只能维护其中一个。都不维护则默认为0
    @ExcelColumn(name = "起订数量")
    private BigDecimal minimumQuantity; // 起订数量

    // ============财务信息============
    @ExcelColumn(name = "收款方式", defaultValue = "0")
    private Integer paymentTerms; // 收款方式 0支付宝付款，1银行账号付款。默认为0
    @ExcelColumn(name = "支付宝账号")
    private String alipayAccount; // 支付宝账号
    @ExcelColumn(name = "支付宝账户名")
    private String alipayName; // 支付宝账户名
    @ExcelColumn(name = "开户行")
    private String bankName; // 开户行, 如中国银行、交通银行等
    @ExcelColumn(name = "开户行名称")
    private String subBranchName; // 开户行名称, 如中国银行九江路支行
    @ExcelColumn(name = "开户名")
    private String accountName; // 银行开户名
    @ExcelColumn(name = "银行卡号")
    private String bankAccount; // 银行卡号
    @ExcelColumn(name = "税务登记号")
    private String taxCode; // 税务登记号
    @ExcelColumn(name = "纳税人类型", defaultValue = "0")
    private Integer taxPayerType; // 纳税人类型, 包括 0增值税一般纳税人；1增值税小规模纳税人。默认为0
    @ExcelColumn(name = "出具发票类型", defaultValue = "0")
    private Integer invoiceType; // 出具发票类型, 包括 0增值税专用发票；1普通商品销售发票；2农产品收购发票；3货运发票。默认为0

}
