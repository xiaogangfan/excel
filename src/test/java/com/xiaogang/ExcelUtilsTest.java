package com.xiaogang;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.io.FileInputStream;

import static org.junit.Assert.fail;

/**
 * @author xiaogang
 * @date 2016-06-28
 */
public class ExcelUtilsTest {

    @Test
    public void testExcel() throws Exception {

        try {
            ExcelResolveResult<FlexItemEO> result = ExcelUtils.resolveExcel(new FileInputStream("/Users/xiaogangfan/Downloads/item.xlsx"), FlexItemEO.class);
            System.out.println(JSONObject.toJSONString(result));
        } catch (Exception e) {
            e.printStackTrace();
            fail("excel 解析失败："+e.getMessage());
        }

    }

}
