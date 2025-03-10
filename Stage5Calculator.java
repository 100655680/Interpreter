import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Stage5Calculator {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Stage5Calculator <path-to-file>");
            return;
        }

        String filePath = args[0];
        Environment env = Environment.getInstance();

        // Read entire file into one string.
        StringBuilder sourceBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                sourceBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("File Error: " + e.getMessage());
            return;
        }
        String source = sourceBuilder.toString();

        // Lex and parse the entire program.
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);
        List<Stmt> statements;
        try {
            statements = parser.parseProgram();
        } catch (Parser.ParseException e) {
            System.err.println("Parse Error: " + e.getMessage());
            return;
        }

        // Execute each statement.
        for (Stmt stmt : statements) {
            try {
                stmt.execute(env);
            } catch (RuntimeException e) {
                System.err.println("Execution Error: " + e.getMessage());
            }
        }
    }
}
