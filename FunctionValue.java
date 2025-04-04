import java.util.List;

public class FunctionValue {
    public final List<String> parameters;
    public final Stmt.Block body;
    public final Environment closure;

    public FunctionValue(List<String> parameters, Stmt.Block body, Environment closure) {
        this.parameters = parameters;
        this.body = body;
        this.closure = closure;
    }

    @Override
    public String toString() {
        return "<function>";
    }
}
