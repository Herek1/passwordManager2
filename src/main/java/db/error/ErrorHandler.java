package db.error;


public class ErrorHandler {

    public String returnStackStraceAsString(Exception exception) {
        StringBuilder stackTraceBuilder = new StringBuilder();

        stackTraceBuilder.append("Exception: ").append(exception.getClass().getName());
        stackTraceBuilder.append(" - ").append(exception.getMessage()).append("\n");

        for (StackTraceElement element : exception.getStackTrace()) {
            stackTraceBuilder.append("\tat ").append(element.toString()).append("\n");
        }

        return stackTraceBuilder.toString();
    }
}