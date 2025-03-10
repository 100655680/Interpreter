import java.util.List;

public class Value {
    public enum ValueType { NUMBER, BOOLEAN, TEXT, ARRAY }

    private final ValueType type;
    private final Double numberValue;
    private final Boolean boolValue;
    private final String textValue;
    private final List<Value> arrayValue;

    private Value(ValueType type, Double numberValue, Boolean boolValue, String textValue, List<Value> arrayValue) {
        this.type = type;
        this.numberValue = numberValue;
        this.boolValue = boolValue;
        this.textValue = textValue;
        this.arrayValue = arrayValue;
    }

    public static Value ofNumber(double d) {
        return new Value(ValueType.NUMBER, d, null, null, null);
    }

    public static Value ofBoolean(boolean b) {
        return new Value(ValueType.BOOLEAN, null, b, null, null);
    }

    public static Value ofText(String s) {
        return new Value(ValueType.TEXT, null, null, s, null);
    }

    public static Value ofArray(List<Value> list) {
        return new Value(ValueType.ARRAY, null, null, null, list);
    }

    public boolean isNumber() { return type == ValueType.NUMBER; }
    public boolean isBoolean() { return type == ValueType.BOOLEAN; }
    public boolean isText() { return type == ValueType.TEXT; }
    public boolean isArray() { return type == ValueType.ARRAY; }

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

    @Override
    public String toString() {
        switch (type) {
            case NUMBER:  return numberValue.toString();
            case BOOLEAN: return boolValue.toString();
            case TEXT:    return textValue;
            case ARRAY:   return arrayValue.toString();
            default:      return "Unknown";
        }
    }
}
