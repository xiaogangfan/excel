package com.xiaogang;

import com.alibaba.fastjson.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.List;

import static org.junit.Assert.fail;

/**
 * @author xiaogang
 * @date 2016-06-28
 */
public class ExcelUtilsTest {

//    @Ignore
    @Test
    public void testExcel() throws Exception {

        try {
            ExcelResolveResult<FlexItemEO> result = ExcelUtils.resolveExcel(new FileInputStream("/Users/xiaogangfan/Downloads/item.xlsx"), FlexItemEO.class,2);
            System.out.println(JSONObject.toJSONString(result));
        } catch (Exception e) {
            e.printStackTrace();
            fail("excel 解析失败："+e.getMessage());
        }

    }


    @Test
    public void generateOdpsSql(){
        try {
            ExcelResolveResult<TableStructor> result = ExcelUtils.resolveExcel(new FileInputStream("/Users/xiaogangfan/Downloads/table1.xls"), TableStructor.class,1);

            List<TableStructor> data = result.getData();
            StringBuffer sql = new StringBuffer();
            sql.append("create table homs_daily_summary (");
            for (int i = 0; i < data.size(); i++) {
                TableStructor tableStructor = data.get(i);
                addColumName(tableStructor, sql);
                addType(tableStructor,sql);
                addComment(tableStructor,sql);
                addDesc(tableStructor,sql);
                sql.append(i==(data.size()-1) ? "" : ",");
            }
            sql.append(");");

            System.out.println("sql:"+sql.toString());
        } catch (Exception e){
            e.printStackTrace();
            fail("解析失败："+e.getMessage());
        }
    }

    private void addType(TableStructor tableStructor, StringBuffer sql) {
        if("varchar".equals(tableStructor.getType().toLowerCase())){
            sql.append(" String ");
        }else if("decimal".equals(tableStructor.getType().toLowerCase())){
            sql.append(" double ");
        }else if("tinyint".equals(tableStructor.getType().toLowerCase())){
            sql.append(" int ");
        }else if("bigint unsigned".equals(tableStructor.getType().toLowerCase())){
            sql.append(" bigint ");
        }else if("datetime".equals(tableStructor.getType().toLowerCase())){
            sql.append(" datetime ");
        }
    }
    private void addComment(TableStructor tableStructor, StringBuffer sql) {
            sql.append("  COMMENT ");
    }
    private void addDesc(TableStructor tableStructor, StringBuffer sql) {
        sql.append( " \' " + tableStructor.getDesc()+" \' ");
    }
    private void addColumName(TableStructor tableStructor, StringBuffer sql) {
        sql.append(" " + tableStructor.getColumName() + " ");
    }

}
