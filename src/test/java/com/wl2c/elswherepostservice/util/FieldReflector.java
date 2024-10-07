package com.wl2c.elswherepostservice.util;

import java.lang.reflect.Field;

public class FieldReflector {
    public static <T> void inject(Class<T> clazz, T obj, String fieldName, Object value) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
