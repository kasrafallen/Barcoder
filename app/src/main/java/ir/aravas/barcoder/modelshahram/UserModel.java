package ir.aravas.barcoder.modelshahram;

public class UserModel {

    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private boolean admin;
    private boolean status;
    private String _id;
    private String token;
    private ProductModel[] checkedProductsByBarcode;
    private String[] inviteList;

    public ProductModel[] getCheckedProductsByBarcode() {
        return checkedProductsByBarcode;
    }

    public void setCheckedProductsByBarcode(ProductModel[] checkedProductsByBarcode) {
        this.checkedProductsByBarcode = checkedProductsByBarcode;
    }

    public String[] getInviteList() {
        return inviteList;
    }

    public void setInviteList(String[] inviteList) {
        this.inviteList = inviteList;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
