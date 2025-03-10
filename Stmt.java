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
            Value value = expression.evaluate();
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
            Value value = expression.evaluate();
            env.define(name, value);
        }
    }

    public static class Expression extends Stmt {
        public final Expr expression;

        public Expression(Expr expression) {
            this.expression = expression;
        }

        @Override
        public void execute(Environment env) {
            expression.evaluate();
        }
    }

    public static class Block extends Stmt {
        public final List<Stmt> statements;

        public Block(List<Stmt> statements) {
            this.statements = statements;
        }

        @Override
        public void execute(Environment env) {
            for (Stmt stmt : statements) {
                stmt.execute(env);
            }
        }
    }

    public static class If extends Stmt {
        public final Expr condition;
        public final Stmt thenBranch;
        public final Stmt elseBranch; // may be null

        public If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        @Override
        public void execute(Environment env) {
            if (condition.evaluate().asBoolean()) {
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
            while (condition.evaluate().asBoolean()) {
                body.execute(env);
            }
        }
    }
}
