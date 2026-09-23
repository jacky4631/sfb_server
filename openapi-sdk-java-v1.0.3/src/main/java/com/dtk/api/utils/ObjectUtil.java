package com.dtk.api.utils;

import com.dtk.api.constant.DtkApiConstant;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * dtk api: ObjectUtil
 *
 * @author 1
 * @date 2020/11/10 14:16
 */
public class ObjectUtil {
    private static final Set<String> EXCLUDE_FIELDS = new HashSet<>();

    static {
        EXCLUDE_FIELDS.add(DtkApiConstant.RequestCommonParam.REQUEST_URL);
    }

    private ObjectUtil() {
    }

    private static String objToStr(Object value) {
        String strValue;

        if (value == null) {
            strValue = null;
        } else if (value instanceof String) {
            strValue = (String) value;
        } else if (value instanceof Integer) {
            strValue = ((Integer) value).toString();
        } else if (value instanceof Long) {
            strValue = ((Long) value).toString();
        } else if (value instanceof Float) {
            strValue = value.toString();
        } else if (value instanceof Double) {
            strValue = value.toString();
        } else if (value instanceof Boolean) {
            strValue = ((Boolean) value).toString();
        } else if (value instanceof Date) {
            DateFormat format = new SimpleDateFormat(DtkApiConstant.DATE_TIME_FORMAT);
            format.setTimeZone(TimeZone.getTimeZone(DtkApiConstant.DATE_TIMEZONE));
            strValue = format.format((Date) value);
        } else {
            strValue = value.toString();
        }

        return strValue;
    }

    public static TreeMap<String, String> objToMap(Object object) throws IllegalAccessException {
        TreeMap<String, String> txtParams = new TreeMap<>();
        Class<?> objectClass = object.getClass();
        Field[] declaredFields = getDeclaredFields(objectClass);
        getField(declaredFields, object, txtParams);
        getParentField(objectClass, object, txtParams);
        return txtParams;
    }

    private static Field[] getDeclaredFields(Class<?> objectClass) {
        return objectClass.getDeclaredFields();
    }

    private static void getField(Field[] declaredFields, Object object, TreeMap<String, String> fieldMap) throws IllegalAccessException {
        for (Field field : declaredFields) {
            String name = field.getName();
            if (EXCLUDE_FIELDS.contains(name)) {
                continue;
            }

            field.setAccessible(true);
            Object value = field.get(object);
            if (value == null) {
                continue;
            }

            fieldMap.put(name, ObjectUtil.objToStr(value));
        }
    }

    private static void getParentField(Class<?> clazz, Object object, TreeMap<String, String> fieldMap) throws IllegalAccessException {
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null) {
            Field[] superFields = getDeclaredFields(superClazz);
            getField(superFields, object, fieldMap);
            getParentField(superClazz, object, fieldMap);
        }
    }
}