import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Interpreter {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Interpreter <path-to-file>");
            return;
        }

        String filePath = args[0];
        Environment globalEnv = new Environment();

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

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        for (Token token : tokens) {
            System.out.println(token);
        }

        Parser parser = new Parser(tokens);
        List<Stmt> statements;
        try {
            statements = parser.parseProgram();
        } catch (Parser.ParseException e) {
            System.err.println("Parse Error: " + e.getMessage());
            return;
        }

        for (Stmt stmt : statements) {
            try {
                stmt.execute(globalEnv);
            } catch (RuntimeException e) {
                System.err.println("Execution Error: " + e.getMessage());
            }
        }
    }
}
