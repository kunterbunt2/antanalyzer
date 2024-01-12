package de.bushnaq.abdalla.ant.analyzer;

public class AntException extends Exception {
    public final String file;
    public final int lineNumber;
    public final int columnNumber;

    public AntException(String message, String file, int lineNumber, int columnNumber) {
        super(message);
        this.file = file;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    public AntException(String message) {
        super(message);
        this.file = null;
        this.lineNumber = 0;
        this.columnNumber = 0;
    }
}
