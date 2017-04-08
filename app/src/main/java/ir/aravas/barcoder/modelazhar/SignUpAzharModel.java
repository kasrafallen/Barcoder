package ir.aravas.barcoder.modelazhar;

public class SignUpAzharModel {

    private String email;
    private String first;
    private String last;
    private String company;
    private String phone;
    private String expectedDepSize;
    private String country;
    private String state;

    public SignUpAzharModel(String email, String first, String last, String company
            , String phone, String expectedDepSize, String country, String state) {
        this.email = email;
        this.first = first;
        this.last = last;
        this.company = company;
        this.phone = phone;
        this.expectedDepSize = expectedDepSize;
        this.country = country;
        this.state = state;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getExpectedDepSize() {
        return expectedDepSize;
    }

    public void setExpectedDepSize(String expectedDepSize) {
        this.expectedDepSize = expectedDepSize;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }
}
