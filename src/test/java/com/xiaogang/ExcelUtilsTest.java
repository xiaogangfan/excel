package com.xiaogang;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.sun.tools.hat.internal.parser.ReadBuffer;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.fail;

/**
 * @author xiaogang
 * @date 2016-06-28
 */
public class ExcelUtilsTest {
    public static void main(String[] args) {
//        System.out.println("varchar(100)".toLowerCase().trim().indexOf("varchar") > -1);
//        Set<String> aa = new HashSet<>();
//        Set<String> bb = new HashSet<>();
//        aa.add("12");
//        aa.add("14");
//        bb.add("12");
//        bb.add("14");
//        bb.add("15");
//        System.out.println(CollectionUtils.subtract(bb,aa));;
//        Person p = new Person();
//        p.setName("121");
//        p.setAge(3);
//        Person p1 = new Person();
//        p1.setName(null);
//        p1.setAge(3);
//        BeanUtils.copyProperties(p1,p);
//        System.out.println(JSONObject.toJSONString(p));
        System.out.println(String.format("%s 2323 \t  %s",2323,"23dsf"));
    }

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
    public void excelToJavaBean() throws Exception{
        List<String> result = ExcelUtils.getDateHead(new FileInputStream("/Users/xiaogangfan/Downloads/inboundinit.xlsx"));
        for (int i = 0; i < result.size(); i++) {
            System.out.println("@ExcelColumn(name = \""+result.get(i)+"\")");
            System.out.println("private String " + result.get(i) + "; \n ");
        }
    }
    @Test
    public void collectInboundList() throws Exception{
        ExcelResolveResult<InboundEO> inboundeos = ExcelUtils.resolveExcel(new FileInputStream("/Users/xiaogangfan/Downloads/inboundinit.xlsx"), InboundEO.class,2);
        System.out.println(JSONObject.toJSONString(inboundeos));
    }


    @Test
    public void generateOdpsSql(){
        try {
            String tableName = "homs_sale_outbound_item";
            ExcelResolveResult<TableStructor> result = ExcelUtils.resolveExcel(new FileInputStream("/Users/xiaogangfan/Downloads/"+tableName+".xls"), TableStructor.class,1);

            List<TableStructor> data = result.getData();
            StringBuffer sql = new StringBuffer();
            sql.append("create table "+tableName+" (");
            for (int i = 0; i < data.size(); i++) {
                TableStructor tableStructor = data.get(i);
                addColumName(tableStructor, sql);
                addType(tableStructor,sql);
                addComment(tableStructor,sql);
                addDesc(tableStructor,sql);
                sql.append(i==(data.size()-1) ? "" : ", \n ");
                System.out.println();
            }
            sql.append(");");

            System.out.println("sql:"+sql.toString());
        } catch (Exception e){
            e.printStackTrace();
            fail("解析失败："+e.getMessage());
        }
    }

    private void addType(TableStructor tableStructor, StringBuffer sql) {
        tableStructor.setType(tableStructor.getType().toLowerCase().trim());
        String type = tableStructor.getType();
        if(type.indexOf("varchar") > -1){
            sql.append(" String ");
        }else if(type.indexOf("text") > -1){
            sql.append(" String ");
        }else if(type.indexOf("decimal") > -1){
            sql.append(" double ");
        }else if(type.indexOf("tinyint")> -1){
            sql.append(" int ");
        }else if(type.indexOf("int")> -1){
            sql.append(" int ");
        }else if(type.indexOf("bigint")> -1){
            sql.append(" bigint ");
        }else if(type.indexOf("datetime")> -1||
                type.indexOf("date")> -1 ||
                type.indexOf("timestamp")> -1){
            sql.append(" datetime ");
        }else {
            throw new RuntimeException("类型"+tableStructor.getType()+"还没有维护");
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



    @Test
    public void handlesql () throws IOException {

        FileReader fileReader = new FileReader(new File("/Users/xiaogangfan/Downloads/book.sql"));
        BufferedReader br = new BufferedReader(fileReader);
        StringBuffer sb = new StringBuffer();
        String str;
        while((str = br.readLine()) != null) {
            str = handleLine(str);
            if(StringUtils.isBlank(str)){
                continue;
            }
            if(str.indexOf("15307") > -1){
                System.out.println(str);
            }
            if(str.split(",").length == 28){
                sb.append(str + "\n");
            }
        }

        FileWriter fileWriter = new FileWriter(new File("/Users/xiaogangfan/Downloads/book2.sql"));
        fileWriter.write(sb.toString());


    }
    @Test
    public void initMeasuresql () throws IOException {
        FileReader fileReader = new FileReader(new File("/Users/xiaogangfan/Downloads/measure.txt"));
        BufferedReader br = new BufferedReader(fileReader);
        StringBuffer sb = new StringBuffer();
        String str;
        int id = 1;
        while((str = br.readLine()) != null) {
            sb.append("insert into homs_measureunit(id, gmt_create, gmt_modified, creator, modifier, name, status, remark) values("+id+", '2016-09-09 20:53:32', '2016-09-09 20:53:32', '妆卿', '妆卿', '"+str+"', 1, null); \n ");
                    id++;
        }
        FileWriter fileWriter = new FileWriter(new File("/Users/xiaogangfan/Downloads/measure.sql"));
        fileWriter.write(sb.toString());
        System.out.println(sb.toString());
    }

    private String handleLine(String str) {
        StringBuffer sb = new StringBuffer();
        String[] split = str.split(",");
        if(split.length != 28){
            return "";
        }
        for (int i = 0; i < split.length; i++) {
            if(i == 23){
                split[i] = "2016-07-01 00:00:00";
            }
            if(i != split.length-1){
                sb.append(split[i].trim()+",");
            }else{
                sb.append(split[i].trim());
            }
        }
        return sb.toString().replaceAll("null","0").replaceAll("'","");
    }

    private int getIndex(String str) {
        int num = 0;
        int index = 0;
        for (int i = 0; i < str.length(); i++){
            if(str.substring(i,(i+1)).indexOf(",") != -1){
                num = num+1;
                if(num ==23){
                    index = i;
                    break;
                }
            }
        }
        return index;
    }
}
class Person{
    private String name;
    private Integer age;

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}