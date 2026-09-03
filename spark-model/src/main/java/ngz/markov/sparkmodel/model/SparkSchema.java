package ngz.markov.sparkmodel.model;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

public final class SparkSchema {

    private SparkSchema() {}

    public static <T> StructType of(Class<T> clazz) {

        StructType schema = new StructType();

        for (Field field : clazz.getDeclaredFields()) {

            SparkColumn annotation = field.getAnnotation(SparkColumn.class);

            String columnName;

            if (annotation != null && !annotation.name().isBlank()) {
                columnName = annotation.name();
            } else {
                columnName = field.getName();
            }

            boolean nullable = annotation == null || annotation.nullable();

            schema = schema.add(columnName, toSparkType(field.getType()), nullable);
        }

        return schema;
    }

    private static DataType toSparkType(Class<?> type) {
        return switch (type.getName()) {
            case "java.lang.String" -> DataTypes.StringType;
            case "java.lang.Integer", "int" -> DataTypes.IntegerType;
            case "java.lang.Long", "long" -> DataTypes.LongType;
            case "java.lang.Short", "short" -> DataTypes.ShortType;
            case "java.lang.Byte", "byte" -> DataTypes.ByteType;
            case "java.lang.Double", "double" -> DataTypes.DoubleType;
            case "java.lang.Float", "float" -> DataTypes.FloatType;
            case "java.lang.Boolean", "boolean" -> DataTypes.BooleanType;
            case "java.sql.Date" -> DataTypes.DateType;
            case "java.sql.Timestamp" -> DataTypes.TimestampType;
            default ->
                    throw new IllegalArgumentException("Unsupported Spark type: " + type.getName());
        };
    }

    public static <T> Map<String, String> getSourceRenameMapping(Class<T> clazz) {

        Map<String, String> mapping = new LinkedHashMap<>();

        for (Field field : clazz.getDeclaredFields()) {

            SparkColumn annotation = field.getAnnotation(SparkColumn.class);

            String sourceName;
            if (annotation != null && !annotation.name().isBlank()) {
                sourceName = annotation.name();
            } else {
                sourceName = field.getName();
            }

            mapping.put(sourceName, field.getName());
        }

        return mapping;
    }
}
