package com.marcusslover.plus.lib.common;

import org.bukkit.persistence.PersistentDataType;

public record DataType() {
    public static PersistentDataType<Byte, Byte> BYTE = PersistentDataType.BYTE;
    public static PersistentDataType<Short, Short> SHORT = PersistentDataType.SHORT;
    public static PersistentDataType<Integer, Integer> INTEGER = PersistentDataType.INTEGER;
    public static PersistentDataType<Long, Long> LONG = PersistentDataType.LONG;
    public static PersistentDataType<Float, Float> FLOAT = PersistentDataType.FLOAT;
    public static PersistentDataType<Double, Double> DOUBLE = PersistentDataType.DOUBLE;
    public static PersistentDataType<String, String> STRING = PersistentDataType.STRING;
    public static PersistentDataType<byte[], byte[]> BYTE_ARRAY = PersistentDataType.BYTE_ARRAY;
    public static PersistentDataType<int[], int[]> INTEGER_ARRAY = PersistentDataType.INTEGER_ARRAY;
    public static PersistentDataType<long[], long[]> LONG_ARRAY = PersistentDataType.LONG_ARRAY;
}
