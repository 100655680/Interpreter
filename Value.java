import java.util.List;
import java.util.Map;

public class Value {
    public enum ValueType { NUMBER, BOOLEAN, TEXT, ARRAY, DICTIONARY, FUNCTION }

    private final ValueType type;
    private final Double numberValue;
    private final Boolean boolValue;
    private final String textValue;
    private final List<Value> arrayValue;
    private final Map<Value, Value> dictValue;
    private final FunctionValue functionValue;
    private final String originalNumberLiteral;

    private Value(ValueType type, Double numberValue, Boolean boolValue, String textValue,
                  List<Value> arrayValue, Map<Value, Value> dictValue, FunctionValue functionValue,
                  String originalNumberLiteral) {
        this.type = type;
        this.numberValue = numberValue;
        this.boolValue = boolValue;
        this.textValue = textValue;
        this.arrayValue = arrayValue;
        this.dictValue = dictValue;
        this.functionValue = functionValue;
        this.originalNumberLiteral = originalNumberLiteral;
    }

    public static Value ofNumber(double d) {
        return new Value(ValueType.NUMBER, d, null, null, null, null, null, null);
    }

    public static Value ofNumber(double d, String literal) {
        return new Value(ValueType.NUMBER, d, null, null, null, null, null, literal);
    }

    public static Value ofBoolean(boolean b) {
        return new Value(ValueType.BOOLEAN, null, b, null, null, null, null, null);
    }

    public static Value ofText(String s) {
        return new Value(ValueType.TEXT, null, null, s, null, null, null, null);
    }

    public static Value ofArray(List<Value> list) {
        return new Value(ValueType.ARRAY, null, null, null, list, null, null, null);
    }

    public static Value ofDictionary(Map<Value, Value> dict) {
        return new Value(ValueType.DICTIONARY, null, null, null, null, dict, null, null);
    }

    public static Value ofFunction(FunctionValue func) {
        return new Value(ValueType.FUNCTION, null, null, null, null, null, func, null);
    }

    public boolean isNumber() { return type == ValueType.NUMBER; }
    public boolean isBoolean() { return type == ValueType.BOOLEAN; }
    public boolean isText() { return type == ValueType.TEXT; }
    public boolean isArray() { return type == ValueType.ARRAY; }
    public boolean isDictionary() { return type == ValueType.DICTIONARY; }
    public boolean isFunction() { return type == ValueType.FUNCTION; }

    public double asNumber() {
        if (!isNumber()) throw new RuntimeException("Value is not a number.");
        return numberValue;
    }

    public boolean asBoolean() {
        if (!isBoolean()) throw new RuntimeException("Value is not a boolean.");
        return boolValue;
    }

    public String asText() {
        if (!isText()) throw new RuntimeException("Value is not text.");
        return textValue;
    }

    public List<Value> asArray() {
        if (!isArray()) throw new RuntimeException("Value is not an array.");
        return arrayValue;
    }

    public Map<Value, Value> asDictionary() {
        if (!isDictionary()) throw new RuntimeException("Value is not a dictionary.");
        return dictValue;
    }

    public FunctionValue asFunction() {
        if (!isFunction()) throw new RuntimeException("Value is not a function.");
        return functionValue;
    }

    @Override
    public String toString() {
        switch (type) {
            case NUMBER:
                if (originalNumberLiteral != null) {
                    if (originalNumberLiteral.contains(".")) {
                        return originalNumberLiteral;
                    } else {
                        return originalNumberLiteral;
                    }
                } else {
                    if (numberValue % 1.0 == 0.0) {
                        return String.valueOf(numberValue.longValue());
                    } else {
                        return numberValue.toString();
                    }
                }
            case BOOLEAN:
                return boolValue.toString();
            case TEXT:
                return textValue;
            case ARRAY:
                return arrayValue.toString();
            case DICTIONARY:
                return dictValue.toString();
            case FUNCTION:
                return "<function>";
            default:
                return "Unknown";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Value)) return false;
        Value value = (Value) o;
        if (type != value.type) return false;
        switch (type) {
            case NUMBER:
                return Double.compare(numberValue, value.numberValue) == 0;
            case BOOLEAN:
                return boolValue.equals(value.boolValue);
            case TEXT:
                return textValue.equals(value.textValue);
            case ARRAY:
                return arrayValue.equals(value.arrayValue);
            case DICTIONARY:
                return dictValue.equals(value.dictValue);
            case FUNCTION:
                return functionValue.equals(value.functionValue);
            default:
                return false;
        }
    }

    @Override
    public int hashCode() {
        switch (type) {
            case NUMBER:
                return Double.hashCode(numberValue);
            case BOOLEAN:
                return boolValue.hashCode();
            case TEXT:
                return textValue.hashCode();
            case ARRAY:
                return arrayValue.hashCode();
            case DICTIONARY:
                return dictValue.hashCode();
            case FUNCTION:
                return functionValue.hashCode();
            default:
                return 0;
        }
    }
}
