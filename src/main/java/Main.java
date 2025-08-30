import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2 || !args[0].equals("-E")) {
            System.out.println("Usage: ./your_program.sh -E <pattern>");
            System.exit(1);
        }

        String pattern = args[1];
        Scanner scanner = new Scanner(System.in);
        String inputLine = scanner.nextLine();

        // You can use print statements as follows for debugging, they'll be visible when running tests.
        Grep grep = new Grep();

        if (grep.match(inputLine, pattern)) {
            System.out.println("Success");
            System.exit(0);
        } else {
            System.exit(1);
        }
    }
}
