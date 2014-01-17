package myApp.trainingdiary.utils;

/**
 * Created by Lenovo on 09.12.13.
 */
public class BackupException extends Exception {
    public BackupException() {
    }

    public BackupException(String detailMessage) {
        super(detailMessage);
    }

    public BackupException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public BackupException(Throwable throwable) {
        super(throwable);
    }
}
