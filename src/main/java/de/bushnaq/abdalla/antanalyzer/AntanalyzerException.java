package de.bushnaq.abdalla.antanalyzer;

public class AntanalyzerException extends Exception {
    public final String file;
    public final int lineNumber;
    public final int columnNumber;

    public AntanalyzerException(String message, String file, int lineNumber, int columnNumber) {
        super(message);
        this.file = file;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    public AntanalyzerException(String message) {
        super(message);
        this.file = null;
        this.lineNumber = 0;
        this.columnNumber = 0;
    }
}
