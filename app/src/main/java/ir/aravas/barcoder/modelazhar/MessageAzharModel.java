package ir.aravas.barcoder.modelazhar;

public class MessageAzharModel {
    public static final String STATUS_FAILED = "failed";
    public static final String STATUS_SUCCESS = "success";

    private String status;
    private String message;
    private String error;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
