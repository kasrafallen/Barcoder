package ir.aravas.barcoder.modelazhar;

public class ActivateAzharModel {
    private int userId;
    private String password;

    public ActivateAzharModel(int userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
