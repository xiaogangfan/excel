package com.xiaogang;

import java.io.FileInputStream;

/**
 * @author xiaogang
 * @date 2016-06-28
 */
public class ExcelUtilsTest {

    public static void main(String[] args) throws Exception {
        ExcelResolveResult<FlexItemEO> result = ExcelUtils.resolveExcel(new FileInputStream("/Users/xiaogangfan/Downloads/item.xlsx"), FlexItemEO.class);
        System.out.println(result.getData().size());
        System.out.println(result);

    }

}
