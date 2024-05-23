package com.EquiFarm.EquiFarm.utils;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FieldUpdateUtil {
    public static void updateFieldIfNotNullAndChanged(String newValue, Consumer<String> fieldUpdater, Supplier<String> fieldGetter) {
        if (newValue != null && !newValue.isEmpty() && !Objects.equals(fieldGetter.get(), newValue)) {
            fieldUpdater.accept(newValue);
        }
    }
    public static void updateFieldIfNotNullAndChanged(Double newValue, Consumer<Double> fieldUpdater, Supplier<Double> fieldGetter) {
        if (newValue != null && !Objects.equals(fieldGetter.get(), newValue)) {
            fieldUpdater.accept(newValue);
        }
    }

    public static <T> void updateFieldIfNotNullAndChanged(List<T> newValue, Consumer<List<T>> fieldUpdater, Supplier<List<T>> fieldGetter) {
        if (newValue != null && !Objects.equals(fieldGetter.get(), newValue)) {
            fieldUpdater.accept(newValue);
        }
    }
    
    public static <T extends Enum<T>> void updateFieldIfNotNullAndChanged(T newValue, Consumer<T> fieldUpdater, Supplier<T> fieldGetter) {
        if (newValue != null && !Objects.equals(fieldGetter.get(), newValue)) {
            fieldUpdater.accept(newValue);
        }
    }

    public static void updateFieldIfNotNullAndChanged(Boolean newValue, Consumer<Boolean> fieldUpdater, Supplier<Boolean> fieldGetter) {
        if (newValue != null && !Objects.equals(fieldGetter.get(), newValue)) {
            fieldUpdater.accept(newValue);
        }
    }

}
