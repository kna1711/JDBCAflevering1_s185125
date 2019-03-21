package db;

import model.M_UserDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DB_UserDAO implements DB_IUserDAO {

    //TODO Kontroller at det fungerer korrekt med 'roles', altså List. Ryd op i de udkommenterede 'role' kald hvis det fungerer med List

    private final String dbURL = "jdbc:mysql://ec2-52-30-211-3.eu-west-1.compute.amazonaws.com/s185125?";
    private final String dbUsername = "s185125";
    private final String dbPassword = "rhMBx6R8LwiFriAQfbkLz";

    private Connection getConnection() throws DALException {
        try {
            return DriverManager.getConnection(dbURL, dbUsername, dbPassword);
        } catch(SQLException e) {
            throw new DALException(e.getMessage());
        }
    }

    @Override
    public M_UserDTO getUser(int userId) throws DALException {
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM UsersDB WHERE userID = " + userId);

            if(resultSet.next()) {
                return new M_UserDTO(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4),
                        resultSet.getString(5),
//                        resultSet.getString(6));
//                        getUserRoles(resultSet, 6));
                        getUserRoles(connection, userId));
            }

            connection.close();
        } catch (SQLException e) {
            //Remember to handle Exceptions gracefully! Connection might be Lost....
            e.printStackTrace();
        } return null;
    }

    @Override
    public List<M_UserDTO> getUserList() throws DALException {
        List<M_UserDTO> list = new ArrayList<M_UserDTO>();

        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM User");

            while(resultSet.next()){
                M_UserDTO tempUser = new M_UserDTO(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5),
//                        getUserRoles(resultSet, 6));
                        getUserRoles(connection, resultSet.getInt(0)));
                list.add(tempUser);
            }

            connection.close();
            return list;

        } catch (SQLException e) {
            //Remember to handle Exceptions gracefully! Connection might be Lost....
            e.printStackTrace();
        }
        return null;
    }

//    private List<String> getUserRoles(ResultSet rs, int index) throws SQLException {
//        List<String> roles = new ArrayList<>();
//        for(int i = index ; rs.next() ; i++) {
//            roles.add(rs.getString(i));
//        }
//        return roles;
//    }

    private List<String> getUserRoles(Connection c, int userId) throws SQLException {
        List<String> roles = new ArrayList<>();
        ResultSet rs = c.createStatement().executeQuery("SELECT * FROM UserRoles WHERE UserId = " + userId);
        for (int i = 1 ; rs.next() ; i++) {
            switch(i) {
                case 1: //Admin
                    if(rs.getInt(i) == 1) {
                        roles.add("Admin");
                    }
                    break;
                case 2: //Pharmacist
                    if(rs.getInt(i) == 1) {
                        roles.add("Pharmacist");
                    }
                    break;
                case 3: //Foreman
                    if(rs.getInt(i) == 1) {
                        roles.add("Foreman");
                    }
                    break;
                case 4: //Operator
                    if(rs.getInt(i) == 1) {
                        roles.add("Operator");
                    }
                    break;
            }
        }
        return roles;
    }

    @Override
    public void createUser(M_UserDTO user) throws DALException {
        try {
            Connection connection = getConnection();
            String query = "INSERT INTO User VALUES(?,?,?,?,?,?)";

            PreparedStatement preparedStmt = connection.prepareStatement(query);
            preparedStmt.setInt(1,user.getUserId());
            preparedStmt.setString(2,user.getCpr());
            preparedStmt.setString(3,user.getIni());
            preparedStmt.setString(4,user.getUserName());
            preparedStmt.setString(5,user.getPassword());
//            preparedStmt.setString(6,user.getRole());
            setUserRoles(preparedStmt, user.getRoles(), 6);
            preparedStmt.execute();

            connection.close();
        } catch (SQLException e) {
            //Remember to handle Exceptions gracefully! Connection might be Lost....
            e.printStackTrace();
        }
    }

    @Override
    public void updateUser(M_UserDTO user) throws DALException {
        try {
            Connection connection = getConnection();
            String query = "UPDATE User SET cpr = ?, ini = ?, username = ?, password = ? WHERE userID = ? "; //TODO Kontroller at man ikke kan skrifte ID, ellers skal der være en db_id
            PreparedStatement preparedStmt = connection.prepareStatement(query);

            preparedStmt.setString(1, user.getCpr());
            preparedStmt.setString(2, user.getIni());
            preparedStmt.setString(3, user.getUserName());
            preparedStmt.setString(4,user.getPassword());
//            preparedStmt.setString(5, user.getRole());
            setUserRoles(preparedStmt, user.getRoles(), 5);
            preparedStmt.setInt(6, user.getUserId());
            preparedStmt.executeUpdate();

            connection.close();
        } catch (SQLException e) {
            //Remember to handle Exceptions gracefully! Connection might be Lost....
            e.printStackTrace();
        }
    }

    private void setUserRoles(PreparedStatement preparedStmt, List<String> roleList, int index) throws SQLException {
        StringBuilder roles = new StringBuilder();
        for (String role : roleList) {
            roles.append(role).append(" ");
        }
        preparedStmt.setString(index, roles.toString());
    }

    private void setUserRoles(Connection c, List<String> roleList, int userId, boolean newUser) throws SQLException {
        List<Integer> roles = new ArrayList<>();
        if(roleList.contains("Admin")) {
            roles.add(1);
        } else {
            roles.add(0);
        }
        if(roleList.contains("Pharmacist")) {
            roles.add(1);
        } else {
            roles.add(0);
        }
        if(roleList.contains("Foreman")) {
            roles.add(1);
        } else {
            roles.add(0);
        }
        if(roleList.contains("Operator")) {
            roles.add(1);
        } else {
            roles.add(0);
        }

        if(newUser) {
            createUserRoles(c, roles, userId);
        } else {
            updateUserRoles(c, roles, userId);
        }
    }

    private void createUserRoles(Connection c, List<Integer> roles, int userId) throws SQLException {
        String query = "INSERT INTO UserRoles VALUES(?,?,?,?,?)";
        PreparedStatement preparedStmt = c.prepareStatement(query);

        preparedStmt.setInt(1, userId);
        preparedStmt.setInt(2, roles.get(0));
        preparedStmt.setInt(3, roles.get(1));
        preparedStmt.setInt(4, roles.get(2));
        preparedStmt.setInt(5, roles.get(3));

        preparedStmt.execute();
    }

    private void updateUserRoles(Connection c, List<Integer> roles, int userId) throws SQLException {
        String query = "UPDATE UserRoles SET administrator = ?, pharmacist = ?, foreman = ?, operator= ? WHERE userID = ? " + userId;
        PreparedStatement preparedStmt = c.prepareStatement(query);

        preparedStmt.setInt(1, roles.get(0));
        preparedStmt.setInt(2, roles.get(1));
        preparedStmt.setInt(3, roles.get(2));
        preparedStmt.setInt(4, roles.get(3));
        preparedStmt.setInt(5, userId);

        preparedStmt.executeUpdate();
    }

    @Override
    public void deleteUser(int userId) throws DALException {
        try {
            Connection connection = getConnection();
            String query = "DELETE FROM User WHERE userID = ?";
            PreparedStatement preparedStmt = connection.prepareStatement(query);
            preparedStmt.setInt(1, userId);

            preparedStmt.execute();

            connection.close();
        } catch (SQLException e) {
            //Remember to handle Exceptions gracefully! Connection might be Lost....
            e.printStackTrace();
        }
    }
}
