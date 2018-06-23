package com.ch.dcs.node.core.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtil {

    private static final Gson GSON = new GsonBuilder().setLenient()// json宽松
            .enableComplexMapKeySerialization()//支持Map的key为复杂对象的形式
            .serializeNulls() //智能null,支持输出值为null的属性
            .setPrettyPrinting()//格式化输出（序列化）
            .setDateFormat("yyyy-MM-dd HH:mm:ss") //序列化日期格式化输出
            .excludeFieldsWithoutExposeAnnotation() //不序列化与反序列化没有@Expose标注的字段
            .disableHtmlEscaping() //默认是Gson把HTML转义的
            .create();

    public static <T> String toString(T obj) {
        return GSON.toJson(obj);
    }

    public static <T> T toObject(String json, Class<T> cla) {
        return GSON.fromJson(json, cla);
    }

}
