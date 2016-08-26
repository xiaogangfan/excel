package com.xiaogang;

import java.io.FileInputStream;
import static org.junit.Assert.*;
/**
 * @author xiaogang
 * @date 2016-06-28
 */
public class ExcelUtilsTest {

    public static void main(String[] args) throws Exception {

        try {
            ExcelResolveResult<FlexItemEO> result = ExcelUtils.resolveExcel(new FileInputStream("/Users/xiaogangfan/Downloads/item.xlsx"), FlexItemEO.class);
        } catch (Exception e) {
            e.printStackTrace();
            fail("excel 解析失败："+e.getMessage());
        }

    }

}
