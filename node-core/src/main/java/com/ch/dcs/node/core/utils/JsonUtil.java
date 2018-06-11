package com.ch.dcs.node.core.utils;

import com.google.gson.Gson;

public class JsonUtil {

    public static <T> String toString(T obj) {
        return new Gson().toJson(obj);
    }

    public static <T> T toObject(String json, Class<T> cla) {
        return new Gson().fromJson(json, cla);
    }

}
