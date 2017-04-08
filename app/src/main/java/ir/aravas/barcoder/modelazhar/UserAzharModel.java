package ir.aravas.barcoder.modelazhar;

public class UserAzharModel extends MessageAzharModel {
    private int userid;
    private int userId;
    private int pincode;
    private boolean verified;
    private boolean signed;
    private String email;
    private ProductAzharModel[] device;

    public ProductAzharModel[] getDevice() {
        return device;
    }

    public void setDevice(ProductAzharModel[] device) {
        this.device = device;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isSigned() {
        return signed;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getPincode() {
        return pincode;
    }

    public void setPincode(int pincode) {
        this.pincode = pincode;
    }
}
