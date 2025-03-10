public class Token {
    public final TokenType type;
    public final String lexeme;
    public final Double value;   // Only used for NUMBER tokens

    public Token(TokenType type, String lexeme, Double value) {
        this.type = type;
        this.lexeme = lexeme;
        this.value = value;
    }

    @Override
    public String toString() {
        if (value != null) {
            return type + " " + lexeme + " (" + value + ")";
        }
        return type + " " + lexeme;
    }
}
