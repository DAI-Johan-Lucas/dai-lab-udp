package orchestra;

public class Logger {

    private static final String RESET = "\033[0m";
    private static final String INFO = "\033[0;34m"; // BLUE
    private static final String WARNING = "\033[0;33m"; // YELLOW
    private static final String SUCCESS = "\033[0;32m"; // GREEN

    public static void log(String type, String message) {
        switch (type.toUpperCase()) {
            case "INFO":
                System.out.println(INFO + "[INFO] " + RESET + message);
                break;
            case "WARNING":
                System.out.println(WARNING + "[WARNING] " + RESET + message);
                break;
            case "SUCCESS":
                System.out.println(SUCCESS + "[SUCCESS] " + RESET + message);
                break;
            case "ERROR":
                System.err.println(message);
                break;
            default:
                System.out.println(message);
        }
    }
}
