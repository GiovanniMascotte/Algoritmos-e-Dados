package Parser;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Parser {
    private Scanner leitor;

    public Parser(File file) {
        try {
            leitor = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
            leitor = null;
        }
    }

    public Parser(String input) {
        leitor = new Scanner(input);
    }

    public boolean hasNext() {
        return leitor != null && leitor.hasNext();
    }

    public String nextLine() {
        return leitor.nextLine();
    }
}
