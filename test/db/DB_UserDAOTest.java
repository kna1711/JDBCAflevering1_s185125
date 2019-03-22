package db;

import model.M_UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DB_UserDAOTest {
    DB_IUserDAO DAO;
    M_UserDTO user;
    M_UserDTO oldUser;
    M_UserDTO updatedUser;

    @BeforeEach
    void setup() {
        this.DAO = new DB_UserDAO();
    }

    @Test
    void getUser() {
        String ini = "KNA", name = "Kristian Andersen", cpr = "123456-1234", password = "SikkertPassword";
        int id = 11;
        List<String> roles = new ArrayList<>();
        roles.add("operator");
        roles.add("administrator");

        try {
            user = DAO.getUser(11);
        } catch (DB_IUserDAO.DALException e) {
            e.printStackTrace();
        }

        assertEquals(name, user.getUserName());
        assertEquals(ini, user.getIni());
        assertEquals(cpr, user.getCpr());
        assertEquals(password, user.getPassword());
        assertEquals(id, user.getUserId());
        assertTrue(roles.containsAll(user.getRoles()));
    }

    @Test
    void updateUser() {
        List<String> roles = new ArrayList<>();
        roles.add("operator");
        roles.add("foreman");
        M_UserDTO updateUser = new M_UserDTO(12, "654321-4321", "PJ", "Peder Andersen", "MilitærtPassword", roles);

        try {
            oldUser = DAO.getUser(12);
            DAO.updateUser(updateUser);
            updatedUser = DAO.getUser(12);
        } catch (DB_IUserDAO.DALException e) {
            e.printStackTrace();
        }

        assertEquals(updateUser.getUserName(), updatedUser.getUserName());
        assertEquals(updateUser.getIni(), updatedUser.getIni());
        assertEquals(updateUser.getCpr(), updatedUser.getCpr());
        assertEquals(updateUser.getPassword(), updatedUser.getPassword());
        assertEquals(updateUser.getUserId(), updatedUser.getUserId());
        assertTrue(updatedUser.getRoles().containsAll(roles));
        //reset User
        try {
            DAO.updateUser(oldUser);
        } catch (DB_IUserDAO.DALException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getUserList() {
    }

    @Test
    void createUser() {
        List<String> roles = new ArrayList<>();
        roles.add("pedel");
        roles.add("OperATor");
        M_UserDTO newUser = new M_UserDTO(51, "132948-9715", "ABC", "Smølf", "SmølfePassword", roles);
        try {
            DAO.createUser(newUser);
            user = DAO.getUser(51);
        } catch (DB_IUserDAO.DALException e) {
            e.printStackTrace();
        }
        assertNotNull(user);
    }

    @Test
    void deleteUser() {
        try {
            DAO.deleteUser(51);
            user = DAO.getUser(51);
        } catch (DB_IUserDAO.DALException e) {
            e.printStackTrace();
        }
        assertNull(user);
    }
}