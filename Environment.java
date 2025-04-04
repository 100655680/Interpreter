import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, Value> values = new HashMap<>();
    private final Environment parent;

    public Environment() {
        this.parent = null;
    }

    public Environment(Environment parent) {
        this.parent = parent;
    }

    public void define(String name, Value value) {
        values.put(name, value);
    }

    public Value get(String name) {
        if (values.containsKey(name)) {
            return values.get(name);
        } else if (parent != null) {
            return parent.get(name);
        }
        throw new RuntimeException("Undefined variable: " + name);
    }

    public void assign(String name, Value value) {
        if (values.containsKey(name)) {
            values.put(name, value);
        } else if (parent != null) {
            parent.assign(name, value);
        } else {
            throw new RuntimeException("Undefined variable: " + name);
        }
    }
}
