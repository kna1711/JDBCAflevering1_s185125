package db;

import model.M_UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DB_UserDAOTest {

    DB_UserDAO userDAO;


    @BeforeEach
    void setUp() {
        userDAO = new DB_UserDAO();
    }

    @Test
    void getUser() {
//        M_UserDTO acceptedUser = new M_UserDTO(20, "571325-6481", "WWW", "acceptedUserTest", "aaaAAA111!!!", testRoles);
    }

    @Test
    void getUserList() {
//        M_UserDTO foundUser1 = new M_UserDTO(20, "571325-6481", "WWW", "acceptedUserTest", "aaaAAA111!!!", testRoles);
//        M_UserDTO foundUser2 = new M_UserDTO(20, "571325-6481", "WWW", "acceptedUserTest", "aaaAAA111!!!", testRoles);
//        M_UserDTO foundUser3 = new M_UserDTO(20, "571325-6481", "WWW", "acceptedUserTest", "aaaAAA111!!!", testRoles);
    }

    @Test
    void createUser() {
        List<String> testRoles = new ArrayList<>();
        testRoles.add("TestUser");
        M_UserDTO acceptedUser = new M_UserDTO(20, "571325-6481", "WWW", "acceptedUserTest", "aaaAAA111!!!", testRoles);
        M_UserDTO failedUser = new M_UserDTO(1, "416865-5816", "WWW", "failedUserTest", "aaaAAA111!!!", testRoles);
        M_UserDTO acceptUserFound = null, failUserFound = null;

        try {
            userDAO.createUser(acceptedUser);
            userDAO.createUser(acceptedUser);
            acceptUserFound = userDAO.getUser(20);
            failUserFound = userDAO.getUser(1);
        } catch (DB_IUserDAO.DALException e) {        }

        assertEquals(acceptedUser, acceptUserFound);
        assertNull(failUserFound);
//        assertNotEquals(failedUser, failUserFound);
    }

    @Test
    void updateUser() {
//        M_UserDTO acceptedUser = new M_UserDTO(20, "571325-6481", "WWW", "acceptedUserTest", "aaaAAA111!!!", testRoles);
    }

    @Test
    void deleteUser() {

    }
}