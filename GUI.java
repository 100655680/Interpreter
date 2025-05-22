import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.Iterator;

// Ensure these classes are in your project:
// Environment, Lexer, Parser, Token, and Stmt

public class GUI extends JFrame {
    private JTextArea outputArea;
    private JTextArea inputArea;
    private JButton submitButton;
    private JButton closeButton;

    public GUI() {
        super("Interpreter GUI");

        // Create the non-editable output area.
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        outputScrollPane.setBorder(BorderFactory.createTitledBorder("Output"));

        // Redirect System.out and System.err to the outputArea.
        redirectSystemStreams();

        // Create the multi-line input area.
        inputArea = new JTextArea(10, 50);
        JScrollPane inputScrollPane = new JScrollPane(inputArea);
        inputScrollPane.setBorder(BorderFactory.createTitledBorder("Input"));

        // Create the Submit button.
        submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                String code = inputArea.getText();
                if (code.trim().isEmpty()) {
                    return;  // Nothing to process if input is empty.
                }
                System.out.println("Submitted code:\n\n" + code);
                // Process the code through your interpreter.
                interpret(code);
                // Optionally clear the input area after submission.
                inputArea.setText("");
            }
        });
        
        // Create the Clear button to clear the output area.
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                outputArea.setText("");
            }
        });

        // Create the Close button.
        closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Panel to hold the buttons.
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(submitButton);
        buttonPanel.add(clearButton);  
        buttonPanel.add(closeButton);

        // Panel to hold the text areas.
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(outputScrollPane);
        textPanel.add(inputScrollPane);

        // Main panel.
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(textPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Configure the frame.
        add(mainPanel);
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Center on screen.
        setVisible(true);
    }

    /**
     * Redirects standard output and error streams to the outputArea.
     */
    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                // Append one character at a time.
                outputArea.append(String.valueOf((char) b));
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
            }
            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                String text = new String(b, off, len);
                outputArea.append(text);
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
            }
        };
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

    /**
     * Processes the multi-line code.
     * It tokenizes, prints token output, parses the tokens, and then executes
     * the statements—all printing output via System.out so that it appears in the outputArea.
     */
    private void interpret(String code) {
        // Create the environment.
        Environment env = new Environment();

        // Lexical analysis: Create a Lexer and scan tokens.
        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.scanTokens();
        for (Token token : tokens) {
            System.out.println(token);
        }
        System.out.println("################    END OF ANALYSIS       ################");
        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓    OUTPUT OF INPUT COMMANDS      ↓↓↓↓↓↓↓↓↓↓↓↓↓");
        System.out.println();

        // Parsing: Create a Parser and parse the program.
        Parser parser = new Parser(tokens);
        List<Stmt> statements;
        try {
            statements = parser.parseProgram();
        } catch (Parser.ParseException e) {
            System.err.println("Parse Error: " + e.getMessage());
            return;
        }

        // Execution: Execute each statement.
        for (Stmt stmt : statements) {
            try {
                stmt.execute(env);
            } catch (RuntimeException e) {
                System.err.println("Execution Error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        // Launch the GUI on the Event Dispatch Thread.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GUI();
            }
        });
    }
}
