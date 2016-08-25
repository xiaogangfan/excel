package com.xiaogang;

import java.io.FileInputStream;

/**
 * @author 'xiaogang'
 * @date 2016-06-28
 */
public class ExcelUtilsTest {

    public static void main(String[] args) throws Exception {
//        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
//        loggerContext.getLogger(ExcelUtils.class).setLevel(Level.DEBUG);
        ExcelResolveResult<FlexItemEO> result = ExcelUtils.resolveExcel(new FileInputStream("/Users/xiaogangfan/Downloads/item.xlsx"), FlexItemEO.class);
//        ExcelResolveResult<FlexItemEO> result = ExcelUtils.resolveExcel(new FileInputStream("/Users/'xiaogang'/Downloads/test.xlsx"), FlexItemEO.class);
//        ExcelResolveResult<InPriceSheetItemEO> result = ExcelUtils.resolveExcel(new FileInputStream("/Users/'xiaogang'/Downloads/进价改价excel模版.xlsx"), InPriceSheetItemEO.class);
        System.out.println(result.getData().size());
        System.out.println(result);
    }

}
