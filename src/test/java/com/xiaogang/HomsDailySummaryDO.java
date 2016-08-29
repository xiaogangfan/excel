package com.xiaogang;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * HOMS日常统计结果
 * @author zhibin.jzb 2016年8月6日
 */
@Data
public class HomsDailySummaryDO {

	
	/**
	 * 公司Id
	 */
	@ExcelColumn(name = "副标题")
	private Long companyId;
	
	/**
	 * 门店或仓库Id
	 */
	private Long storeId;
	
	/**
	 * 管理部门
	 */
	private Long deptId;
	
	/**
	 * 商品Id, scmItemId
	 */
	private Long itemId;
	
	/**
	 * 汇总日期
	 */
	private Date summaryDate;
	
	/**
	 * 销量
	 */
	private BigDecimal sale;

	/**
	 * 销退量
	 */
	private BigDecimal saleReturn;

	/**
	 * 调拔入库量
	 */
	private BigDecimal allocationInbound;

	/**
	 * 调拔出库量
	 */
	private BigDecimal allocationOutbound;

	/**
	 * 生产消耗量
	 */
	private BigDecimal productionConsumption;

	/**
	 * 库存调整出库量
	 */
	private BigDecimal inventoryAdjustOutbound;

	/**
	 * 库存调整入库量
	 */
	private BigDecimal inventoryAdjustInbound;
	
	/**
	 * 配货入库量
	 */
	private BigDecimal distributionInbound;
	
	/**
	 * 配货出库量
	 */
	private BigDecimal distributionOutbound;
	
	/**
	 * 采购入库量
	 */
	private BigDecimal purchaseInbound;
}

