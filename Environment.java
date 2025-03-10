import java.util.HashMap;
import java.util.Map;

public class Environment {
    private Map<String, Value> values = new HashMap<>();
    private static Environment instance;

    private Environment() { }

    public static Environment getInstance() {
        if (instance == null) {
            instance = new Environment();
        }
        return instance;
    }

    public void define(String name, Value value) {
        values.put(name, value);
    }

    public Value get(String name) {
        if (values.containsKey(name)) {
            return values.get(name);
        }
        throw new RuntimeException("Undefined variable: " + name);
    }

    public void assign(String name, Value value) {
        if (values.containsKey(name)) {
            values.put(name, value);
        } else {
            throw new RuntimeException("Undefined variable: " + name);
        }
    }
}
