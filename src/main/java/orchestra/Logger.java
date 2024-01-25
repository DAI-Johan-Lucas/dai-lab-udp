package orchestra;

public class Logger {

    private static final String COLOR_RESET = "\033[0m";
    private static final String COLOR_INFO = "\033[0;34m"; // BLUE
    private static final String COLOR_WARNING = "\033[0;33m"; // YELLOW
    private static final String COLOR_SUCCESS = "\033[0;32m"; // GREEN

    public enum LogType {
        INFO, WARNING, SUCCESS, ERROR
    }

    public static void log(LogType type, String message) {
        switch (type) {
            case INFO:
                System.out.println(COLOR_INFO + "[INFO] " + COLOR_RESET + message);
                break;
            case WARNING:
                System.out.println(COLOR_WARNING + "[WARNING] " + COLOR_RESET + message);
                break;
            case SUCCESS:
                System.out.println(COLOR_SUCCESS + "[SUCCESS] " + COLOR_RESET + message);
                break;
            case ERROR:
                System.err.println(message);
                break;
            default:
                System.out.println(message);
        }
    }
}
