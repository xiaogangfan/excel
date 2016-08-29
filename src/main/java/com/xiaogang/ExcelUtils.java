package com.xiaogang;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.KeyValue;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.validator.HibernateValidator;
import org.springframework.ui.ModelMap;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Excel文件导入工具类
 */
@Slf4j
public final class ExcelUtils {

    private static ExecutorService executor = Executors.newFixedThreadPool(20);

    /**
     * 导入后默认返回的VIEW名称, 因为为了解决跨域问题, 需要返回给前端一个页面, 而不只是导入结果
     */
    public static final String DEFAULT_VIEW = "importResult";

    /**
     * 导入后把结果放到model中给渲染视图用
     *
     * @param model 视图模型
     * @param result 结果实体
     */
    public static void fillModel(ModelMap model, BaseResultVO result) {
        model.put("jsonResult", JSON.toJSON(result));
    }

    /**
     * 返回导入成功的结果
     *
     * @param records 记录数
     * @return 导入成功的结果实体
     */
    public static BaseResultVO importSucceeded(int records) {
        BaseResultVO.Data<String> data = new BaseResultVO.Data<String>();
        data.setSuccessTotal(records);
        data.setFailedTotal(0);
        return new BaseResultVO<String>(true, data, "导入成功");
    }

    /**
     * 返回导入失败的结果
     *
     * @param records 记录数
     * @param errorMessage 错误消息
     * @return 导入失败的结果实体
     */
    public static BaseResultVO importFailed(int records, String errorMessage) {
        BaseResultVO.Data<String> data = new BaseResultVO.Data<>();
        data.setSuccessTotal(0);
        data.setFailedTotal(records);
        data.setFailedDetail(errorMessage);
        return new BaseResultVO<>(false, data, "导入失败");
    }

    /**
     *
     * @param stream
     * @param clazz
     * @param excelType 1:xls  2:xlsx
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> ExcelResolveResult<T> resolveExcel(InputStream stream, Class<T> clazz,Integer excelType) throws Exception {
        if(excelType == 1){
            return resolveXlsExcel(stream, clazz, false);
        }else  if(excelType == 2){
            return resolveExcel(stream, clazz, false);
        }
        return null;
    }

    /**
     * 把指定的IO流解析成实体, 支持日期、枚举、Number及其子类型、基本类型, 其中
     * 日期类型只支持三种格式:
     * <ul>
     * <li>yyyy-MM-dd</li>
     * <li>yyyy-MM-dd HH:mm:ss</li>
     * <li>yyyy/M/d</li>
     * </ul>
     * 注意: 如果excel中定义成日期类型, 实体中对应的类型也应该是<code>java.util.Date</code>类型, 否则转换结果是一个数值
     * 如果实体定义成String类型, 那excel中的类型也应该是文本类型; <p>
     * 枚举类型: 如果有of或parse方法, 优先使用这两个方法进行转换, 否则使用默认的valueOf方法进行转换
     * <p>
     * excel中第一行表头必须与实体上的ExcelLine注解的name属性对应, 表头可以是'xxx(yyy)'格式, 在解析时只取'xxx'部分,
     * 否则必须与ExcelLine注解的name属性完全相等, 如果ExcelLine注解没有设置name属性, 默认使用实体字段名作为与表头对应,
     * 数据从第二行进行解析
     * </p>
     *
     * @param stream EXCEL文件流
     * @param clazz 实体class
     * @param doValidate 是否进行字段值的验证(JSR 303), 验证失败会时不会立即失败, 只记录失败信息, 继续向下解析
     * @param <T> 实体类型
     * @return 解析结果, 如果开启验证, 那么验证结果不影响解析过程, 只提供验证发生错误行的错误信息,
     * 错误格式为: 'column1 error|column2 error|columnX error,column1 error|column2 error|columnX error......'
     * @throws Exception
     */
    public static <T> ExcelResolveResult<T> resolveExcel(InputStream stream, Class<T> clazz, boolean doValidate) throws Exception {

        XSSFWorkbook workbook = new XSSFWorkbook(stream);

        XSSFSheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            log.warn("Worksheet not found.");
            return ExcelResolveResult.fail("Worksheet not found.");
        }
        int maxRow = sheet.getLastRowNum();

        Map<Integer, Field> fieldMap = columnIndexFieldInfo(sheet.getRow(0), clazz);

        StringBuilder validateMessage = new StringBuilder();
        Validator validator = Validation.byProvider(HibernateValidator.class).configure().failFast(false).buildValidatorFactory().getValidator();

        List<Callable<Optional<KeyValue<T, String>>>> list = Lists.newArrayListWithExpectedSize(maxRow);

        for (int line = 1; line <= maxRow; line++) {
            log.info("resolve row[{}]", line);
            XSSFRow row = sheet.getRow(line);
            if (row == null) {
                continue;
            }

            int rowIndex = line + 1;

            list.add(() -> {
                T entity = resolveRow(row, clazz, fieldMap);
                if (entity == null) {
                    return Optional.empty();
                }

                // 设置行号
                setRowIndex(entity, rowIndex);

                DefaultKeyValue<T, String> keyValue = new DefaultKeyValue<>();
                keyValue.setKey(entity);

                // 做数据校验
                if (doValidate) {
                    Set<ConstraintViolation<T>> validateResult = validator.validate(entity);
                    if (!validateResult.isEmpty()) {
                        // 错误信息格式为: 'column1 error|column2 error|columnX error,column1 error|column2 error|columnX error......'
                        String msg = validateResult.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("|"));
                        keyValue.setValue(msg);
                    }
                }
                return Optional.of(keyValue);
            });
        }

        List<Future<Optional<KeyValue<T, String>>>> futureList = executor.invokeAll(list);
        List<T> resultList = Lists.newArrayList();
        futureList.forEach((future) -> {
            try {
                Optional<KeyValue<T, String>> optional = future.get();
                if (optional.isPresent()) {
                    KeyValue<T, String> kv = optional.get();
                    resultList.add(kv.getKey());
                    String msg = kv.getValue();
                    if (StringUtils.isNotBlank(msg)) {
                        validateMessage.append(msg).append(",");
                    }
                }
            } catch (Exception e) {
                log.error("handle error:", e);
            }
        });

        ExcelResolveResult<T> result = new ExcelResolveResult<>();
        result.setData(resultList);
        int length = validateMessage.length();
        if (length > 0) {
            result.setMessage(validateMessage.deleteCharAt(length - 1).toString());
        }

        return result;
    }

    private static <T> T resolveRow(XSSFRow row, Class<T> clazz, Map<Integer, Field> fieldMap) throws Exception {
        T instance = clazz.newInstance();

        // 整行是否为空的标志, 如果整行都为空, 那么cell都为null
        boolean nullRowFlag = true;

        int lastCellNum = row.getLastCellNum();
        for (int index = 0; index <= lastCellNum; index++) {
            Field field = fieldMap.get(index);
            if (field == null) {
                log.warn("column [index={}] has no mapped field info", index + 1);
                continue;
            }

            String columnName = field.getName();
            log.debug("resolve column[name={}, columnIndex={}]", columnName, index + 1);

            Object value = null;
            Class<?> paramType = field.getType();
            XSSFCell cell = row.getCell(index);

            if (cell != null) {
                int cellType = cell.getCellType();
                switch (cellType) {
                    case Cell.CELL_TYPE_NUMERIC:
                        value = convertNumericValue(cell.getNumericCellValue(), paramType);
                        break;
                    case Cell.CELL_TYPE_STRING:
                        value = convertStringValue(cell.getStringCellValue(), paramType);
                        break;
                    case Cell.CELL_TYPE_BOOLEAN:
                        value = convertBooleanValue(cell.getBooleanCellValue(), paramType);
                        break;
                    case Cell.CELL_TYPE_BLANK:
                        log.info("column [name={}] is blank type, will set default value", columnName);
                        break;
                    case Cell.CELL_TYPE_ERROR:
                        log.error("column [name={}] is error type, error string value is [{}], will set default value", columnName, cell.getErrorCellString());
                        break;
                }
            } else {
                log.error("column [name={}] cell is null, will set default value.", columnName);
            }

            if (value == null) {
                // 设置默认值
                if (field.isAnnotationPresent(ExcelColumn.class)) {
                    value = getDefaultValue(field);
                    if (value != null) {
                        log.info("column [{}] value is null, set default value [{}]", columnName, value);
                    }
                }
            } else {
                nullRowFlag = false;
            }

            if (value != null) {
                field.set(instance, value);
            }
        }
        return nullRowFlag ? null : instance;
    }
    private static <T> T resolveRowXls(HSSFRow row, Class<T> clazz, Map<Integer, Field> fieldMap) throws Exception {
        T instance = clazz.newInstance();

        // 整行是否为空的标志, 如果整行都为空, 那么cell都为null
        boolean nullRowFlag = true;

        int lastCellNum = row.getLastCellNum();
        for (int index = 0; index <= lastCellNum; index++) {
            Field field = fieldMap.get(index);
            if (field == null) {
                log.warn("column [index={}] has no mapped field info", index + 1);
                continue;
            }

            String columnName = field.getName();
            log.debug("resolve column[name={}, columnIndex={}]", columnName, index + 1);

            Object value = null;
            Class<?> paramType = field.getType();
            HSSFCell cell = row.getCell(index);

            if (cell != null) {
                int cellType = cell.getCellType();
                switch (cellType) {
                    case Cell.CELL_TYPE_NUMERIC:
                        value = convertNumericValue(cell.getNumericCellValue(), paramType);
                        break;
                    case Cell.CELL_TYPE_STRING:
                        value = convertStringValue(cell.getStringCellValue(), paramType);
                        break;
                    case Cell.CELL_TYPE_BOOLEAN:
                        value = convertBooleanValue(cell.getBooleanCellValue(), paramType);
                        break;
                    case Cell.CELL_TYPE_BLANK:
                        log.info("column [name={}] is blank type, will set default value", columnName);
                        break;
                    case Cell.CELL_TYPE_ERROR:
                        log.error("column [name={}] is error type, error string value is [{}], will set default value", columnName, cell.getErrorCellValue());
                        break;
                }
            } else {
                log.error("column [name={}] cell is null, will set default value.", columnName);
            }

            if (value == null) {
                // 设置默认值
                if (field.isAnnotationPresent(ExcelColumn.class)) {
                    value = getDefaultValue(field);
                    if (value != null) {
                        log.info("column [{}] value is null, set default value [{}]", columnName, value);
                    }
                }
            } else {
                nullRowFlag = false;
            }

            if (value != null) {
                field.set(instance, value);
            }
        }
        return nullRowFlag ? null : instance;
    }

    /**
     * 獲取指定行的值默认值
     *
     * @param field 字段
     * @return 默认值, 如果没有设置默认值, 返回null
     * @throws Exception
     */
    private static Object getDefaultValue(Field field) throws Exception {
        String defaultValue = field.getDeclaredAnnotation(ExcelColumn.class).defaultValue();
        if (StringUtils.isNotBlank(defaultValue)) {
            return convertStringValue(defaultValue, field.getType());
        } else {
            log.info("column [{}] value is null, and has no default value", field.getName());
        }
        return null;
    }

    /**
     * 如果实体继承ExcelRowIndexSupport, 那么设置行号
     *
     * @param entity 实体
     * @param rowIndex 行号
     * @param <T> 实体类型
     * @throws Exception
     */
    private static <T> void setRowIndex(T entity, int rowIndex) throws Exception {
        Class<?> clazz = entity.getClass();
        if (ExcelRowIndexSupport.class.isAssignableFrom(clazz)) {
            clazz.getMethod("setRowIndex", Integer.class).invoke(entity, rowIndex);
        }
    }

    private static Object convertNumericValue(Double value, Class<?> paramType) throws Exception {
        if (value == null) {
            return null;
        }
        Class<?> wrappedClass = getWrapperClass(paramType);

        Object resultValue = null;

        if (Number.class.isAssignableFrom(wrappedClass) || wrappedClass == Boolean.class) {
            resultValue = getNumericValueFromDouble(wrappedClass, value);
        } else if (wrappedClass == String.class) {
            resultValue = new BigDecimal(value).toPlainString();
        } else if (Enum.class.isAssignableFrom(wrappedClass)) {
            resultValue = getEnumTypeValue(value.toString(), wrappedClass);
        } else if (wrappedClass == Date.class) {
            resultValue = HSSFDateUtil.getJavaDate(value);
        }

        if (resultValue == null) {
            log.error("numeric cell value[{}] can not convert to type [{}]", value, paramType.getCanonicalName());
            throw new IllegalArgumentException(String.format("数值类型的值[%s]不能转换成[%s]类型", value, paramType.getCanonicalName()));
        }

        return resultValue;
    }

    private static Object convertStringValue(String value, Class<?> paramType) throws Exception {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        Class<?> wrappedClass = getWrapperClass(paramType);
        value = value.trim();

        Object resultValue = null;

        if (wrappedClass == String.class) {
            resultValue = value;
        } else if (Number.class.isAssignableFrom(wrappedClass)) {
            if (NumberUtils.isNumber(value)) {
                if (wrappedClass == BigDecimal.class || wrappedClass == BigInteger.class) {
                    if (NumberUtils.isDigits(value)) {
                        // 纯数字直接使用long参数类型的valueOf方法
                        resultValue = wrappedClass.getMethod("valueOf", long.class).invoke(null, Long.valueOf(value));
                    } else {
                        // 带小数的值, BigDecimal使用double参数类型的valueOf方法, BigInteger需要使用long参数类型的valueOf方法
                        if (wrappedClass == BigDecimal.class) {
                            resultValue = wrappedClass.getMethod("valueOf", double.class).invoke(null, Double.valueOf(value));
                        } else {
                            // BigInteger没有double参数类型的valueOf方法, 只能先转换成double再转换成long
                            resultValue = wrappedClass.getMethod("valueOf", long.class).invoke(null, Double.valueOf(value).longValue());
                        }
                    }
                } else {
                    // 其他Number的子类型都有string参数类型的valueOf方法
                    resultValue = wrappedClass.getMethod("valueOf", String.class).invoke(null, value);
                }
            }
        } else if (paramType == Date.class) {
            // 匹配yyyy/M/d格式的日期
            Matcher matcher = Pattern.compile("(\\d{4})/(0?[1-9]|1[012])/(0?[1-9]|[12]\\d|3[01])").matcher(value);
            if (value.length() == 10) {
                resultValue = DateFormatUtils.ISO_DATE_FORMAT.parse(value);
            } else if (value.length() == 19) {
                resultValue = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").parse(value);
            } else if (matcher.matches()) {
                // 把yyyy/M/d格式的日期转换成yyyy-MM-dd
                value = matcher.group(1) + "-" + StringUtils.leftPad(matcher.group(2), 2, "0") + "-" + StringUtils.leftPad(matcher.group(3), 2, "0");
                resultValue = DateFormatUtils.ISO_DATE_FORMAT.parse(value);
            }
        } else if (Enum.class.isAssignableFrom(wrappedClass)) {
            resultValue = getEnumTypeValue(value, wrappedClass);
        }

        if (resultValue == null) {
            log.error("string cell value[{}] can not convert to type [{}]", value, paramType.getCanonicalName());
            throw new IllegalArgumentException(String.format("字符类型的值[%s]不能转换成[%s]类型", value, paramType.getCanonicalName()));
        }

        return resultValue;
    }

    private static Object getEnumTypeValue(String value, Class<?> enumType) throws Exception {
        // 获取枚举类型的of或parse方法
        Method enumConvertMethod = getNumericEnumConvertMethod(enumType);
        Object resultValue = null;
        if (enumConvertMethod == null) {
            try {
                resultValue = enumType.getMethod("valueOf", String.class).invoke(null, value);
            } catch (Exception e) {
                // ignore it.
                log.warn("enum type has no [of] or [parse] method with one parameter, and invoke [valueOf] method error, skip it.");
            }
        } else {
            // 有of或parse方法, 先把参数转换成对应的类型
            Class<?> enumParamType = enumConvertMethod.getParameterTypes()[0];
            if (enumParamType == String.class) {
                resultValue = enumConvertMethod.invoke(null, value);
            } else if (Number.class.isAssignableFrom(enumParamType)
                    || (enumParamType.isPrimitive() && enumParamType != char.class)
                    || enumParamType == Boolean.class) {
                if (NumberUtils.isNumber(value)) {
                    Double doubleValue = Double.valueOf(value);
                    Object numericValue = getNumericValueFromDouble(enumParamType, doubleValue);
                    if (numericValue != null) {
                        resultValue = enumConvertMethod.invoke(null, numericValue);
                    }
                }
            }
        }
        return resultValue;
    }

    private static Object getNumericValueFromDouble(Class<?> enumParamType, Double doubleValue) {
        Object numericValue = null;
        if (enumParamType == Double.class) {
            numericValue = doubleValue;
        } else if (enumParamType == Float.class) {
            numericValue = doubleValue.floatValue();
        } else if (enumParamType == Long.class) {
            numericValue = doubleValue.longValue();
        } else if (enumParamType == Integer.class) {
            numericValue = doubleValue.intValue();
        } else if (enumParamType == Short.class) {
            numericValue = doubleValue.shortValue();
        } else if (enumParamType == Byte.class) {
            numericValue = doubleValue.byteValue();
        } else if (enumParamType == Boolean.class) {
            numericValue = doubleValue > 0;
        } else if (enumParamType == BigDecimal.class) {
            numericValue = BigDecimal.valueOf(doubleValue);
        } else if (enumParamType == BigInteger.class) {
            numericValue = BigInteger.valueOf(doubleValue.longValue());
        }
        return numericValue;
    }

    private static Object convertBooleanValue(Boolean value, Class<?> paramType) throws Exception {
        if (value == null) {
            return null;
        }
        Class<?> wrapperClass = getWrapperClass(paramType);
        Object resultValue;
        if (wrapperClass == Boolean.class) {
            resultValue = value;
        } else if (wrapperClass == String.class) {
            resultValue = value.toString();
        } else if (Number.class.isAssignableFrom(wrapperClass)) {
            resultValue = wrapperClass.getMethod("valueOf", String.class).invoke(null, value ? "1" : "0");
        } else {
            log.error("boolean cell value[{}] can not convert to type [{}]", value, paramType.getCanonicalName());
            throw new IllegalArgumentException(String.format("布尔类型的值[%s]不能转换成[%s]类型", value, wrapperClass.getCanonicalName()));
        }
        return resultValue;
    }

    private static Method getNumericEnumConvertMethod(Class<?> enumType) {
        Method[] methods = enumType.getMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (("of".equals(name) || "parse".equals(name)) && method.getParameterCount() == 1) {
                return method;
            }
        }
        return null;
    }

    private static Class<?> getWrapperClass(Class<?> clazz) {
        return clazz.isPrimitive() ? primitiveTypes.get(clazz) : clazz;
    }

    private static Map<Integer, Field> columnIndexFieldInfo(XSSFRow row, Class<?> clazz) {
        Map<Integer, Field> map = Maps.newHashMap();

        Map<String, Field> stringFieldMap = fieldInfo(clazz);

        // 表头字段值的格式: 表头(备注)
        Pattern pattern = Pattern.compile("(.*?)(\\s*[(（].*?[）)]?)?");

        int lastCellNum = row.getLastCellNum();
        for (int index = 0; index <= lastCellNum; index++) {
            XSSFCell cell = row.getCell(index);
            if (cell == null) {
                break;
            }
            String stringCellValue = cell.getStringCellValue();
            if (StringUtils.isBlank(stringCellValue)) {
                log.info("skip blank header [index={}]", index + 1);
                continue;
            }
            Matcher matcher = pattern.matcher(stringCellValue.trim());
            if (matcher.matches()) {
                Field field = stringFieldMap.get(matcher.group(1));
                if (field == null) {
                    log.error("column [{}] has no filed info of type [{}]", stringCellValue, clazz.getCanonicalName());
                    continue;
                }

                map.put(index, field);
            } else {
                log.error("header value [{}] not match the rule [header(comments)]", stringCellValue);
            }
        }

        return map;
    }

    private static Map<Integer, Field> columnIndexFieldInfoXls(HSSFRow row, Class<?> clazz) {
        Map<Integer, Field> map = Maps.newHashMap();

        Map<String, Field> stringFieldMap = fieldInfo(clazz);

        // 表头字段值的格式: 表头(备注)
        Pattern pattern = Pattern.compile("(.*?)(\\s*[(（].*?[）)]?)?");

        int lastCellNum = row.getLastCellNum();
        for (int index = 0; index <= lastCellNum; index++) {
            HSSFCell cell = row.getCell(index);
            if (cell == null) {
                break;
            }
            String stringCellValue = cell.getStringCellValue();
            if (StringUtils.isBlank(stringCellValue)) {
                log.info("skip blank header [index={}]", index + 1);
                continue;
            }
            Matcher matcher = pattern.matcher(stringCellValue.trim());
            if (matcher.matches()) {
                Field field = stringFieldMap.get(matcher.group(1));
                if (field == null) {
                    log.error("column [{}] has no filed info of type [{}]", stringCellValue, clazz.getCanonicalName());
                    continue;
                }

                map.put(index, field);
            } else {
                log.error("header value [{}] not match the rule [header(comments)]", stringCellValue);
            }
        }

        return map;
    }

    private static Map<String, Field> fieldInfo(Class<?> clazz) {
        Map<String, Field> map = Maps.newHashMap();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ExcelColumn.class)) {
                ExcelColumn excelColumn = field.getDeclaredAnnotation(ExcelColumn.class);
                field.setAccessible(true);
                String name = excelColumn.name();
                if (StringUtils.isBlank(name)) {
                    name = field.getName();
                }
                map.put(name, field);
            }
        }

        return map;
    }


    public static <T> ExcelResolveResult<T> resolveXlsExcel(InputStream stream, Class<T> clazz, boolean doValidate) throws Exception {

        HSSFWorkbook workbook = new HSSFWorkbook(stream);

        HSSFSheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            log.warn("Worksheet not found.");
            return ExcelResolveResult.fail("Worksheet not found.");
        }
        int maxRow = sheet.getLastRowNum();

        Map<Integer, Field> fieldMap = columnIndexFieldInfoXls(sheet.getRow(0), clazz);

        StringBuilder validateMessage = new StringBuilder();
        Validator validator = Validation.byProvider(HibernateValidator.class).configure().failFast(false).buildValidatorFactory().getValidator();

        List<Callable<Optional<KeyValue<T, String>>>> list = Lists.newArrayListWithExpectedSize(maxRow);

        for (int line = 1; line <= maxRow; line++) {
            log.info("resolve row[{}]", line);
            HSSFRow row = sheet.getRow(line);
            if (row == null) {
                continue;
            }

            int rowIndex = line + 1;

            list.add(() -> {
                T entity = resolveRowXls(row, clazz, fieldMap);
                if (entity == null) {
                    return Optional.empty();
                }

                // 设置行号
                setRowIndex(entity, rowIndex);

                DefaultKeyValue<T, String> keyValue = new DefaultKeyValue<>();
                keyValue.setKey(entity);

                // 做数据校验
                if (doValidate) {
                    Set<ConstraintViolation<T>> validateResult = validator.validate(entity);
                    if (!validateResult.isEmpty()) {
                        // 错误信息格式为: 'column1 error|column2 error|columnX error,column1 error|column2 error|columnX error......'
                        String msg = validateResult.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("|"));
                        keyValue.setValue(msg);
                    }
                }
                return Optional.of(keyValue);
            });
        }

        List<Future<Optional<KeyValue<T, String>>>> futureList = executor.invokeAll(list);
        List<T> resultList = Lists.newArrayList();
        futureList.forEach((future) -> {
            try {
                Optional<KeyValue<T, String>> optional = future.get();
                if (optional.isPresent()) {
                    KeyValue<T, String> kv = optional.get();
                    resultList.add(kv.getKey());
                    String msg = kv.getValue();
                    if (StringUtils.isNotBlank(msg)) {
                        validateMessage.append(msg).append(",");
                    }
                }
            } catch (Exception e) {
                log.error("handle error:", e);
            }
        });

        ExcelResolveResult<T> result = new ExcelResolveResult<>();
        result.setData(resultList);
        int length = validateMessage.length();
        if (length > 0) {
            result.setMessage(validateMessage.deleteCharAt(length - 1).toString());
        }

        return result;
    }

    private static final Map<Class<?>, Class<?>> primitiveTypes = Maps.newHashMap();

    static {
        primitiveTypes.put(int.class, Integer.class);
        primitiveTypes.put(short.class, Short.class);
        primitiveTypes.put(char.class, Character.class);
        primitiveTypes.put(byte.class, Byte.class);
        primitiveTypes.put(boolean.class, Boolean.class);
        primitiveTypes.put(long.class, Long.class);
        primitiveTypes.put(double.class, Double.class);
        primitiveTypes.put(float.class, Float.class);
    }

}
