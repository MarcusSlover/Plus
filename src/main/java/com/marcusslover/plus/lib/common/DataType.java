package com.marcusslover.plus.lib.common;

import org.bukkit.persistence.PersistentDataType;

public record DataType() {
    public static final PersistentDataType<Byte, Byte> BYTE = PersistentDataType.BYTE;
    public static final PersistentDataType<Short, Short> SHORT = PersistentDataType.SHORT;
    public static final PersistentDataType<Integer, Integer> INTEGER = PersistentDataType.INTEGER;
    public static final PersistentDataType<Long, Long> LONG = PersistentDataType.LONG;
    public static final PersistentDataType<Float, Float> FLOAT = PersistentDataType.FLOAT;
    public static final PersistentDataType<Double, Double> DOUBLE = PersistentDataType.DOUBLE;
    public static final PersistentDataType<String, String> STRING = PersistentDataType.STRING;
    public static final PersistentDataType<byte[], byte[]> BYTE_ARRAY = PersistentDataType.BYTE_ARRAY;
    public static final PersistentDataType<int[], int[]> INTEGER_ARRAY = PersistentDataType.INTEGER_ARRAY;
    public static final PersistentDataType<long[], long[]> LONG_ARRAY = PersistentDataType.LONG_ARRAY;
}
