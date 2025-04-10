public enum TokenType {
    // Single-character tokens
    LEFT_PAREN, RIGHT_PAREN,
    LEFT_BRACE, RIGHT_BRACE,
    LEFT_BRACKET, RIGHT_BRACKET,  // New tokens for array literals and access
    COMMA, COLON, SEMICOLON,
    PLUS, MINUS, STAR, SLASH,
    
    // One or two character tokens
    BANG,           // !
    BANG_EQUAL,     // !=
    EQUAL,          // =   (assignment)
    EQUAL_EQUAL,    // ==
    LESS,           // <
    LESS_EQUAL,     // <=
    GREATER,        // >
    GREATER_EQUAL,  // >=

    // Literals
    NUMBER,
    STRING,
    IDENTIFIER,
    
    // Keywords
    FUN, 
    RETURN,
    DICT,
    TRUE,
    FALSE,
    AND,
    OR,
    PRINT,
    PRINTUPPER,
    IF,
    ELSE,
    WHILE,
    INPUT,
    
    EOF
}
