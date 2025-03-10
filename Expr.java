import java.util.ArrayList;
import java.util.List;

public abstract class Expr {
    public abstract Value evaluate();
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
    public Value evaluate() {
        Value leftVal = left.evaluate();
        Value rightVal = right.evaluate();
        switch (operator.type) {
            case PLUS:
                if (leftVal.isNumber() && rightVal.isNumber()) {
                    return Value.ofNumber(leftVal.asNumber() + rightVal.asNumber());
                } else if (leftVal.isText() && rightVal.isText()) {
                    return Value.ofText(leftVal.asText() + rightVal.asText());
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
    public Value evaluate() {
        Value val = right.evaluate();
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
    public Value evaluate() {
        return value;
    }
}

class Variable extends Expr {
    final String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public Value evaluate() {
        return Environment.getInstance().get(name);
    }
}

// Array literal: e.g., ["eggs", "milk", "butter"]
class ArrayLiteral extends Expr {
    final List<Expr> elements;

    public ArrayLiteral(List<Expr> elements) {
        this.elements = elements;
    }

    @Override
    public Value evaluate() {
        List<Value> evaluated = new ArrayList<>();
        for (Expr element : elements) {
            evaluated.add(element.evaluate());
        }
        return Value.ofArray(evaluated);
    }
}

// Array access: e.g., ShoppingList[i]
class ArrayAccess extends Expr {
    final Expr arrayExpr;
    final Expr indexExpr;

    public ArrayAccess(Expr arrayExpr, Expr indexExpr) {
        this.arrayExpr = arrayExpr;
        this.indexExpr = indexExpr;
    }

    @Override
    public Value evaluate() {
        Value arrayVal = arrayExpr.evaluate();
        if (!arrayVal.isArray()) {
            throw new RuntimeException("Attempted to index a non-array value.");
        }
        Value indexVal = indexExpr.evaluate();
        int index = (int) indexVal.asNumber();
        List<Value> list = arrayVal.asArray();
        if (index < 0 || index >= list.size()) {
            throw new RuntimeException("Array index out of bounds.");
        }
        return list.get(index);
    }
}

// Built-in input function; for Stage 5 we already replaced interactive input,
// so we could have it return a default value. (Not needed in this test.)
class InputExpr extends Expr {
    final Expr prompt;

    public InputExpr(Expr prompt) {
        this.prompt = prompt;
    }

    @Override
    public Value evaluate() {
        // For non-interactive testing, simply return an empty string.
        return Value.ofText("");
    }
}
