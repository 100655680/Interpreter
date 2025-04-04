import java.util.List;

public abstract class Stmt {
    public abstract void execute(Environment env);

    public static class Print extends Stmt {
        public final Expr expression;

        public Print(Expr expression) {
            this.expression = expression;
        }

        @Override
        public void execute(Environment env) {
            Value value = expression.evaluate(env);
            System.out.println(value);
        }
    }

    public static class Var extends Stmt {
        public final String name;
        public final Expr expression;

        public Var(String name, Expr expression) {
            this.name = name;
            this.expression = expression;
        }

        @Override
        public void execute(Environment env) {
            Value value = expression.evaluate(env);
            try {
                env.get(name);
                env.assign(name, value);
            } catch (RuntimeException ex) {
                env.define(name, value);
            }
        }
    }

    public static class Expression extends Stmt {
        public final Expr expression;

        public Expression(Expr expression) {
            this.expression = expression;
        }

        @Override
        public void execute(Environment env) {
            expression.evaluate(env);
        }
    }

    public static class Block extends Stmt {
        public final List<Stmt> statements;

        public Block(List<Stmt> statements) {
            this.statements = statements;
        }

        @Override
        public void execute(Environment env) {
            Environment localEnv = new Environment(env);
            for (Stmt stmt : statements) {
                stmt.execute(localEnv);
            }
        }
    }

    public static class If extends Stmt {
        public final Expr condition;
        public final Stmt thenBranch;
        public final Stmt elseBranch;

        public If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        @Override
        public void execute(Environment env) {
            if (condition.evaluate(env).asBoolean()) {
                thenBranch.execute(env);
            } else if (elseBranch != null) {
                elseBranch.execute(env);
            }
        }
    }

    public static class While extends Stmt {
        public final Expr condition;
        public final Stmt body;

        public While(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        public void execute(Environment env) {
            while (condition.evaluate(env).asBoolean()) {
                body.execute(env);
            }
        }
    }

    public static class Function extends Stmt {
        public final String name;
        public final List<String> parameters;
        public final Block body;

        public Function(String name, List<String> parameters, Block body) {
            this.name = name;
            this.parameters = parameters;
            this.body = body;
        }

        @Override
        public void execute(Environment env) {
            FunctionValue function = new FunctionValue(parameters, body, env);
            env.define(name, Value.ofFunction(function));
        }
    }

    public static class Return extends Stmt {
        public final Expr value;

        public Return(Expr value) {
            this.value = value;
        }

        @Override
        public void execute(Environment env) {
            Value returnValue = (value != null) ? value.evaluate(env) : Value.ofText("");
            throw new ReturnException(returnValue);
        }
    }

    public static class ReturnException extends RuntimeException {
        public final Value value;

        public ReturnException(Value value) {
            this.value = value;
        }
    }
}
