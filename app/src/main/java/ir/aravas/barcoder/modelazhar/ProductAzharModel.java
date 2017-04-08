package ir.aravas.barcoder.modelazhar;

public class ProductAzharModel {
    private String licenseKey;
    private String email;
    private String version;
    private String os = "Android";
    private String active = "Y";

    public ProductAzharModel(String licenseKey, String email, String version) {
        this.licenseKey = licenseKey;
        this.email = email;
        this.version = version;
    }

    public String getLicenseKey() {
        return licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }
}
