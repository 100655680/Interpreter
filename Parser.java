    /*
        Grammar with precedence:
        expression   -> orExpr
        orExpr       -> andExpr ( "or" andExpr )*
        andExpr      -> equality ( "and" equality )*
        equality     -> comparison ( ( "==" | "!=" ) comparison )*
        comparison   -> addition ( ( "<" | "<=" | ">" | ">=" ) addition )*
        addition     -> multiplication ( ( "+" | "-" ) multiplication )*
        multiplication -> unary ( ( "*" | "/" ) unary )*
        unary        -> ( "!" | "-" | "+" ) unary | primary
        primary      -> NUMBER | STRING | TRUE | FALSE | "(" expression ")"

        program      → statement* ;
        statement    → printStmt | varStmt | expressionStmt ;
        printStmt    → "print" expression ;
        varStmt      → IDENTIFIER "=" expression ;
        expressionStmt → expression ;

    */

    import java.util.ArrayList;
    import java.util.List;
    
    public class Parser {
        public static class ParseException extends Exception {
            public ParseException(String message) {
                super(message);
            }
        }
    
        private final List<Token> tokens;
        private int current = 0;
    
        public Parser(List<Token> tokens) {
            this.tokens = tokens;
        }
    
        // Parse an entire program (list of statements)
        public List<Stmt> parseProgram() throws ParseException {
            List<Stmt> statements = new ArrayList<>();
            while (!isAtEnd()) {
                statements.add(statement());
            }
            return statements;
        }
    
        // statement → ifStmt | whileStmt | printStmt | varStmt | block | expressionStmt ;
        private Stmt statement() throws ParseException {
            if (match(TokenType.IF)) return ifStmt();
            if (match(TokenType.WHILE)) return whileStmt();
            if (match(TokenType.LEFT_BRACE)) return new Stmt.Block(block());
            if (match(TokenType.PRINT)) return printStmt();
            if (check(TokenType.IDENTIFIER) && peekNext().type == TokenType.EQUAL) return varStmt();
            return expressionStmt();
        }
    
        private Stmt ifStmt() throws ParseException {
            consume(TokenType.LEFT_PAREN, "Expect '(' after 'if'.");
            Expr condition = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after if condition.");
            Stmt thenBranch = statement();
            Stmt elseBranch = null;
            if (match(TokenType.ELSE)) {
                elseBranch = statement();
            }
            return new Stmt.If(condition, thenBranch, elseBranch);
        }
    
        private Stmt whileStmt() throws ParseException {
            consume(TokenType.LEFT_PAREN, "Expect '(' after 'while'.");
            Expr condition = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after while condition.");
            Stmt body = statement();
            return new Stmt.While(condition, body);
        }
    
        private List<Stmt> block() throws ParseException {
            List<Stmt> statements = new ArrayList<>();
            while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
                statements.add(statement());
            }
            consume(TokenType.RIGHT_BRACE, "Expect '}' after block.");
            return statements;
        }
    
        private Stmt printStmt() throws ParseException {
            Expr expr = expression();
            return new Stmt.Print(expr);
        }
    
        private Stmt varStmt() throws ParseException {
            Token nameToken = advance(); // identifier
            String name = nameToken.lexeme;
            consume(TokenType.EQUAL, "Expect '=' after variable name.");
            Expr expr = expression();
            return new Stmt.Var(name, expr);
        }
    
        private Stmt expressionStmt() throws ParseException {
            Expr expr = expression();
            return new Stmt.Expression(expr);
        }
    
        // Expression grammar
        // expression → orExpr ;
        private Expr expression() throws ParseException {
            return orExpr();
        }
    
        private Expr orExpr() throws ParseException {
            Expr expr = andExpr();
            while (match(TokenType.OR)) {
                Token operator = previous();
                Expr right = andExpr();
                expr = new Binary(expr, operator, right);
            }
            return expr;
        }
    
        private Expr andExpr() throws ParseException {
            Expr expr = equality();
            while (match(TokenType.AND)) {
                Token operator = previous();
                Expr right = equality();
                expr = new Binary(expr, operator, right);
            }
            return expr;
        }
    
        private Expr equality() throws ParseException {
            Expr expr = comparison();
            while (match(TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL)) {
                Token operator = previous();
                Expr right = comparison();
                expr = new Binary(expr, operator, right);
            }
            return expr;
        }
    
        private Expr comparison() throws ParseException {
            Expr expr = addition();
            while (match(TokenType.LESS, TokenType.LESS_EQUAL, TokenType.GREATER, TokenType.GREATER_EQUAL)) {
                Token operator = previous();
                Expr right = addition();
                expr = new Binary(expr, operator, right);
            }
            return expr;
        }
    
        private Expr addition() throws ParseException {
            Expr expr = multiplication();
            while (match(TokenType.PLUS, TokenType.MINUS)) {
                Token operator = previous();
                Expr right = multiplication();
                expr = new Binary(expr, operator, right);
            }
            return expr;
        }
    
        private Expr multiplication() throws ParseException {
            Expr expr = unary();
            while (match(TokenType.STAR, TokenType.SLASH)) {
                Token operator = previous();
                Expr right = unary();
                expr = new Binary(expr, operator, right);
            }
            return expr;
        }
    
        private Expr unary() throws ParseException {
            if (match(TokenType.BANG, TokenType.MINUS, TokenType.PLUS)) {
                Token operator = previous();
                Expr right = unary();
                return new Unary(operator, right);
            }
            return call();
        }
    
        // call → primary ( "(" arguments? ")" | "[" expression "]" )* ;
        private Expr call() throws ParseException {
            Expr expr = primary();
            while (true) {
                if (match(TokenType.LEFT_PAREN)) {
                    expr = finishCall(expr);
                } else if (match(TokenType.LEFT_BRACKET)) {
                    Expr indexExpr = expression();
                    consume(TokenType.RIGHT_BRACKET, "Expect ']' after index.");
                    expr = new ArrayAccess(expr, indexExpr);
                } else {
                    break;
                }
            }
            return expr;
        }
    
        private Expr finishCall(Expr callee) throws ParseException {
            List<Expr> arguments = new ArrayList<>();
            if (!check(TokenType.RIGHT_PAREN)) {
                do {
                    arguments.add(expression());
                } while (match(TokenType.COMMA));
            }
            consume(TokenType.RIGHT_PAREN, "Expect ')' after arguments.");
            if (callee instanceof Variable && ((Variable)callee).name.equals("input")) {
                if (arguments.size() != 1) {
                    throw new ParseException("input expects exactly one argument.");
                }
                return new InputExpr(arguments.get(0));
            }
            throw new ParseException("Only built-in function 'input' is supported.");
        }
    
        private Expr primary() throws ParseException {
            if (match(TokenType.NUMBER)) {
                return new Literal(Value.ofNumber(previous().value));
            }
            if (match(TokenType.STRING)) {
                return new Literal(Value.ofText(previous().lexeme));
            }
            if (match(TokenType.TRUE)) {
                return new Literal(Value.ofBoolean(true));
            }
            if (match(TokenType.FALSE)) {
                return new Literal(Value.ofBoolean(false));
            }
            if (match(TokenType.LEFT_BRACKET)) {
                // Array literal: parse elements separated by commas.
                List<Expr> elements = new ArrayList<>();
                if (!check(TokenType.RIGHT_BRACKET)) {
                    do {
                        elements.add(expression());
                    } while (match(TokenType.COMMA));
                }
                consume(TokenType.RIGHT_BRACKET, "Expect ']' after array elements.");
                return new ArrayLiteral(elements);
            }
            if (match(TokenType.IDENTIFIER)) {
                return new Variable(previous().lexeme);
            }
            if (match(TokenType.LEFT_PAREN)) {
                Expr expr = expression();
                consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
                return expr;
            }
            throw new ParseException("Expect expression at token: " + peek().lexeme);
        }
    
        private Token peekNext() {
            if (current + 1 >= tokens.size()) return tokens.get(tokens.size() - 1);
            return tokens.get(current + 1);
        }
    
        // Utility methods
        private boolean match(TokenType... types) {
            for (TokenType type : types) {
                if (check(type)) {
                    advance();
                    return true;
                }
            }
            return false;
        }
    
        private boolean check(TokenType type) {
            if (isAtEnd()) return false;
            return peek().type == type;
        }
    
        private Token advance() {
            if (!isAtEnd()) current++;
            return previous();
        }
    
        private boolean isAtEnd() {
            return peek().type == TokenType.EOF;
        }
    
        private Token peek() {
            return tokens.get(current);
        }
    
        private Token previous() {
            return tokens.get(current - 1);
        }
    
        private Token consume(TokenType type, String message) throws ParseException {
            if (check(type)) return advance();
            throw new ParseException(message + " Found: " + peek().lexeme);
        }
    }
    