import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public abstract class Expr {
    public abstract Value evaluate(Environment env);
}

class Binary extends Expr {
    final Expr left;
    final Token operator;
    final Expr right;

    public Binary(Expr left, Token operator, Expr right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Value evaluate(Environment env) {
        Value leftVal = left.evaluate(env);
        Value rightVal = right.evaluate(env);
        switch (operator.type) {
            case PLUS:
                if (leftVal.isText() || rightVal.isText()) {
                    String leftText = leftVal.isText() ? leftVal.asText() : leftVal.toString();
                    String rightText = rightVal.isText() ? rightVal.asText() : rightVal.toString();
                    return Value.ofText(leftText + rightText);
                } else if (leftVal.isNumber() && rightVal.isNumber()) {
                    return Value.ofNumber(leftVal.asNumber() + rightVal.asNumber());
                }
                throw new RuntimeException("Operator + cannot be applied to " + leftVal + " and " + rightVal);
            case MINUS:
                return Value.ofNumber(leftVal.asNumber() - rightVal.asNumber());
            case STAR:
                return Value.ofNumber(leftVal.asNumber() * rightVal.asNumber());
            case SLASH:
                return Value.ofNumber(leftVal.asNumber() / rightVal.asNumber());
            case LESS:
                return Value.ofBoolean(leftVal.asNumber() < rightVal.asNumber());
            case LESS_EQUAL:
                return Value.ofBoolean(leftVal.asNumber() <= rightVal.asNumber());
            case GREATER:
                return Value.ofBoolean(leftVal.asNumber() > rightVal.asNumber());
            case GREATER_EQUAL:
                return Value.ofBoolean(leftVal.asNumber() >= rightVal.asNumber());
            case EQUAL_EQUAL:
                return Value.ofBoolean(equalsValue(leftVal, rightVal));
            case BANG_EQUAL:
                return Value.ofBoolean(!equalsValue(leftVal, rightVal));
            case AND:
                return Value.ofBoolean(leftVal.asBoolean() && rightVal.asBoolean());
            case OR:
                return Value.ofBoolean(leftVal.asBoolean() || rightVal.asBoolean());
            default:
                throw new RuntimeException("Unknown operator: " + operator.type);
        }
    }

    private boolean equalsValue(Value a, Value b) {
        if (a.isBoolean() && b.isBoolean()) {
            return a.asBoolean() == b.asBoolean();
        }
        if (a.isNumber() && b.isNumber()) {
            return a.asNumber() == b.asNumber();
        }
        if (a.isText() && b.isText()) {
            return a.asText().equals(b.asText());
        }
        if (a.isArray() && b.isArray()) {
            return a.asArray().equals(b.asArray());
        }
        return false;
    }
}

class Unary extends Expr {
    final Token operator;
    final Expr right;

    public Unary(Token operator, Expr right) {
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Value evaluate(Environment env) {
        Value val = right.evaluate(env);
        switch (operator.type) {
            case MINUS:
                return Value.ofNumber(-val.asNumber());
            case PLUS:
                return val;
            case BANG:
                return Value.ofBoolean(!val.asBoolean());
            default:
                throw new RuntimeException("Unknown unary operator: " + operator.type);
        }
    }
}

class Literal extends Expr {
    final Value value;

    public Literal(Value value) {
        this.value = value;
    }

    @Override
    public Value evaluate(Environment env) {
        return value;
    }
}

class Variable extends Expr {
    final String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public Value evaluate(Environment env) {
        return env.get(name);
    }
}

class ArrayLiteral extends Expr {
    final List<Expr> elements;

    public ArrayLiteral(List<Expr> elements) {
        this.elements = elements;
    }

    @Override
    public Value evaluate(Environment env) {
        List<Value> evaluated = new ArrayList<>();
        for (Expr element : elements) {
            evaluated.add(element.evaluate(env));
        }
        return Value.ofArray(evaluated);
    }
}

class ArrayAccess extends Expr {
    final Expr arrayExpr;
    final Expr indexExpr;

    public ArrayAccess(Expr arrayExpr, Expr indexExpr) {
        this.arrayExpr = arrayExpr;
        this.indexExpr = indexExpr;
    }

    @Override
    public Value evaluate(Environment env) {
        Value arrayVal = arrayExpr.evaluate(env);
        if (!arrayVal.isArray()) {
            throw new RuntimeException("Attempted to index a non-array value.");
        }
        Value indexVal = indexExpr.evaluate(env);
        int index = (int) indexVal.asNumber();
        List<Value> list = arrayVal.asArray();
        if (index < 0 || index >= list.size()) {
            throw new RuntimeException("Array index out of bounds.");
        }
        return list.get(index);
    }
}

class InputExpr extends Expr {
    final Expr prompt;

    public InputExpr(Expr prompt) {
        this.prompt = prompt;
    }

    @Override
    public Value evaluate(Environment env) {
        // For non-interactive testing, simply return an empty string.
        return Value.ofText("");
    }
}

// Built-in function to append an element to a list.
class AppendExpr extends Expr {
    final Expr listExpr;
    final Expr elementExpr;

    public AppendExpr(Expr listExpr, Expr elementExpr) {
        this.listExpr = listExpr;
        this.elementExpr = elementExpr;
    }

    @Override
    public Value evaluate(Environment env) {
        Value listVal = listExpr.evaluate(env);
        if (!listVal.isArray()) {
            throw new RuntimeException("append expects first argument to be a list.");
        }
        Value elementVal = elementExpr.evaluate(env);
        listVal.asArray().add(elementVal);
        return listVal;
    }
}

// Built-in function to remove an element from a list.
class RemoveExpr extends Expr {
    final Expr listExpr;
    final Expr indexExpr;

    public RemoveExpr(Expr listExpr, Expr indexExpr) {
        this.listExpr = listExpr;
        this.indexExpr = indexExpr;
    }

    @Override
    public Value evaluate(Environment env) {
        Value listVal = listExpr.evaluate(env);
        if (!listVal.isArray()) {
            throw new RuntimeException("remove expects first argument to be a list.");
        }
        Value indexVal = indexExpr.evaluate(env);
        int index = (int) indexVal.asNumber();
        List<Value> list = listVal.asArray();
        if (index < 0 || index >= list.size()) {
            throw new RuntimeException("remove: index out of bounds.");
        }
        return Value.ofText(list.remove(index).toString());
    }
}

// Built-in function to put a key-value pair into a dictionary.
class PutExpr extends Expr {
    final Expr dictExpr;
    final Expr keyExpr;
    final Expr valueExpr;

    public PutExpr(Expr dictExpr, Expr keyExpr, Expr valueExpr) {
        this.dictExpr = dictExpr;
        this.keyExpr = keyExpr;
        this.valueExpr = valueExpr;
    }

    @Override
    public Value evaluate(Environment env) {
        Value dictVal = dictExpr.evaluate(env);
        if (!dictVal.isDictionary()) {
            throw new RuntimeException("put expects first argument to be a dictionary.");
        }
        Value keyVal = keyExpr.evaluate(env);
        Value valueVal = valueExpr.evaluate(env);
        dictVal.asDictionary().put(keyVal, valueVal);
        return dictVal;
    }
}

// Built-in function to remove a key from a dictionary.
class DictRemoveExpr extends Expr {
    final Expr dictExpr;
    final Expr keyExpr;

    public DictRemoveExpr(Expr dictExpr, Expr keyExpr) {
        this.dictExpr = dictExpr;
        this.keyExpr = keyExpr;
    }

    @Override
    public Value evaluate(Environment env) {
        Value dictVal = dictExpr.evaluate(env);
        if (!dictVal.isDictionary()) {
            throw new RuntimeException("dict_remove expects first argument to be a dictionary.");
        }
        Value keyVal = keyExpr.evaluate(env);
        if (!dictVal.asDictionary().containsKey(keyVal)) {
            throw new RuntimeException("dict_remove: key not found.");
        }
        return dictVal.asDictionary().remove(keyVal);
    }
}

// Function call expression.
class Call extends Expr {
    final Expr callee;
    final List<Expr> arguments;

    public Call(Expr callee, List<Expr> arguments) {
        this.callee = callee;
        this.arguments = arguments;
    }

    @Override
    public Value evaluate(Environment env) {
        Value function = callee.evaluate(env);
        if (!function.isFunction()) {
            throw new RuntimeException("Attempted to call a non-function.");
        }
        FunctionValue func = function.asFunction();
        if (arguments.size() != func.parameters.size()) {
            throw new RuntimeException("Expected " + func.parameters.size() + " arguments but got " + arguments.size());
        }
        Environment localEnv = new Environment(func.closure);
        for (int i = 0; i < func.parameters.size(); i++) {
            Value argVal = arguments.get(i).evaluate(env);
            localEnv.define(func.parameters.get(i), argVal);
        }
        try {
            func.body.execute(localEnv);
        } catch (Stmt.ReturnException returnException) {
            return returnException.value;
        }
        return Value.ofText("");
    }
}

// Make DictionaryLiteral public so that Parser.java can see it.
class DictionaryLiteral extends Expr {
    final Map<Expr, Expr> pairs;

    public DictionaryLiteral(Map<Expr, Expr> pairs) {
        this.pairs = pairs;
    }

    @Override
    public Value evaluate(Environment env) {
        Map<Value, Value> evaluated = new HashMap<>();
        for (Map.Entry<Expr, Expr> entry : pairs.entrySet()) {
            Value key = entry.getKey().evaluate(env);
            Value value = entry.getValue().evaluate(env);
            evaluated.put(key, value);
        }
        return Value.ofDictionary(evaluated);
    }
}
