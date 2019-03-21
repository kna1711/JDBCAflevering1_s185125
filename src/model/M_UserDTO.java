package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class M_UserDTO implements Serializable {

    private static final long serialVersionUID = 4545864587995944260L;
    private int	userId;
    private String cpr, ini, userName, password;
//    private String role;
    private List<String> roles;

//    public M_UserDTO(int userId, String cpr, String ini, String userName, String password, String role) {
    public M_UserDTO(int userId, String cpr, String ini, String userName, String password, List roles) { //TODO Implement so it takes a list
        this.userId = userId;
        this.cpr = cpr;
        this.ini = ini;
        this.userName = userName;
        this.password = password;
//        this.role = role;
        this.roles = roles;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCpr() {
        return cpr;
    }
    public void setCpr(String cpr) {
        this.cpr = cpr;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIni() {
        return ini;
    }
    public void setIni(String ini) {
        this.ini = ini;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

//    public String getRole() {
//        return role;
//    }
//    public void setRole(String role) {
//        this.role = role;
//    }

    public List<String> getRoles() {
        return roles;
    }
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
    public void addRole(String role){
        this.roles.add(role);
    }
    /**
     *
     * @param role
     * @return true if role existed, false if not
     */
    public boolean removeRole(String role){
        return this.roles.remove(role);
    }

    @Override
    public String toString() {
//        return "UserDTO [userId=" + userId + ", userName=" + userName + ", ini=" + ini + ", role=" + role + "]";
        return "UserDTO [userId=" + userId + ", userName=" + userName + ", ini=" + ini + ", roles=" + roles + "]";
    }
}