package db;

import model.M_UserDTO;

import java.util.List;

public interface DB_IUserDAO {

    M_UserDTO getUser(int userId) throws DALException;
    List<M_UserDTO> getUserList() throws DALException;
    void createUser(M_UserDTO user) throws DALException;
    void updateUser(M_UserDTO user) throws DALException;
    void deleteUser(int userId) throws DALException;

    public class DALException extends Exception {

        /**
         *
         */
        private static final long serialVersionUID = 7355418246336739229L;

        public DALException(String msg, Throwable e) {
            super(msg,e);
        }

        public DALException(String msg) {
            super(msg);
        }
    }
}