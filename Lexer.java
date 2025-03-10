import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", null));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(TokenType.LEFT_PAREN);
                break;
            case ')':
                addToken(TokenType.RIGHT_PAREN);
                break;
            case '{':
                addToken(TokenType.LEFT_BRACE);
                break;
            case '}':
                addToken(TokenType.RIGHT_BRACE);
                break;
            case '[':
                addToken(TokenType.LEFT_BRACKET);
                break;
            case ']':
                addToken(TokenType.RIGHT_BRACKET);
                break;
            case ',':
                addToken(TokenType.COMMA);
                break;
            case '+':
                addToken(TokenType.PLUS);
                break;
            case '-':
                addToken(TokenType.MINUS);
                break;
            case '*':
                addToken(TokenType.STAR);
                break;
            case '/':
                addToken(TokenType.SLASH);
                break;
            case '!':
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case '=':
                addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '<':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
            case '"':
                string();
                break;
            case ' ':
            case '\r':
            case '\t':
            case '\n':
                break;
            default:
                if (isDigit(c) || c == '.') {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    System.err.println("Lexer Error: Unexpected character: " + c);
                }
                break;
        }
    }

    private char advance() {
        return source.charAt(current++);
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Double value) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, value));
    }

    private void number() {
        while (!isAtEnd() && (isDigit(peek()) || peek() == '.')) {
            advance();
        }
        String text = source.substring(start, current);
        try {
            Double val = Double.parseDouble(text);
            addToken(TokenType.NUMBER, val);
        } catch (NumberFormatException e) {
            System.err.println("Lexer Error: Invalid number format: " + text);
        }
    }

    private void identifier() {
        while (!isAtEnd() && isAlphaNumeric(peek())) {
            advance();
        }
        String text = source.substring(start, current);
        switch (text) {
            case "true":
                addToken(TokenType.TRUE);
                break;
            case "false":
                addToken(TokenType.FALSE);
                break;
            case "and":
                addToken(TokenType.AND);
                break;
            case "or":
                addToken(TokenType.OR);
                break;
            case "print":
                addToken(TokenType.PRINT);
                break;
            case "if":
                addToken(TokenType.IF);
                break;
            case "else":
                addToken(TokenType.ELSE);
                break;
            case "while":
                addToken(TokenType.WHILE);
                break;
            case "input":
                addToken(TokenType.INPUT);
                break;
            default:
                addToken(TokenType.IDENTIFIER);
                break;
        }
    }

    private void string() {
        while (!isAtEnd() && peek() != '"') {
            advance();
        }
        if (isAtEnd()) {
            System.err.println("Lexer Error: Unterminated string.");
            return;
        }
        advance(); // consume closing "
        String value = source.substring(start + 1, current - 1);
        tokens.add(new Token(TokenType.STRING, value, null));
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
}
