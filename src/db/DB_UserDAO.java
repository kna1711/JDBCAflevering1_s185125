package db;

import model.M_UserDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DB_UserDAO implements DB_IUserDAO {

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
            ResultSet resultSet = statement.executeQuery("SELECT * FROM User WHERE userID = " + userId);

            if(resultSet.next()) {
                return new M_UserDTO(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4),
                        resultSet.getString(5),
                        getUserRoles(connection, userId));
            }

            connection.close();
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        } return null; //If user doesn't exists
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
                        getUserRoles(connection, resultSet.getInt(0))); //FixMe Roles
                list.add(tempUser);
            }

            connection.close();
            return list;

        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }

    private List<String> getUserRoles(Connection c, int userId) throws SQLException {
        List<String> roles = new ArrayList<>();
        ResultSet rs = c.createStatement().executeQuery("SELECT * FROM UserRoles WHERE userId = " + userId);
        if(rs.next()) {
            for (int i = 2 ; i <= rs.getMetaData().getColumnCount() ; i++) {
                if(rs.getBoolean(i)) {
                    roles.add(rs.getMetaData().getColumnName(i));
                }
            }
        }
        return roles;
    }

    @Override
    public void createUser(M_UserDTO user) throws DALException {
        try {
            Connection connection = getConnection();
            String query = "INSERT INTO User VALUES(?,?,?,?,?)";

            PreparedStatement preparedStmt = connection.prepareStatement(query);
            preparedStmt.setInt(1,user.getUserId());
            preparedStmt.setString(2,user.getCpr());
            preparedStmt.setString(3,user.getIni());
            preparedStmt.setString(4,user.getUserName());
            preparedStmt.setString(5,user.getPassword());
            preparedStmt.execute();
            setUserRoles(connection, user.getRoles(), user.getUserId(), true);

            connection.close();
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }

    @Override
    public void updateUser(M_UserDTO user) throws DALException {
        try {
            Connection connection = getConnection();
            String query = "UPDATE User SET cpr = ?, initials = ?, userName = ?, password = ? WHERE userID = ? "; //TODO Kontroller at man ikke kan skrifte ID, ellers skal der være en db_id
            PreparedStatement preparedStmt = connection.prepareStatement(query);

            preparedStmt.setString(1, user.getCpr());
            preparedStmt.setString(2, user.getIni());
            preparedStmt.setString(3, user.getUserName());
            preparedStmt.setString(4,user.getPassword());
            preparedStmt.setInt(5, user.getUserId());
            preparedStmt.executeUpdate();

            setUserRoles(connection, user.getRoles(), user.getUserId(), false);

            connection.close();
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }

    private void setUserRoles(Connection c, List<String> roles, int userId, boolean newUser) throws SQLException {
        //Start query statement
        String query;
        if(newUser) {
            query = "INSERT INTO UserRoles VALUES(?";
        } else {
            query = "UPDATE UserRoles SET ";
        }

        //Get number of columns in current userRole-table
        ResultSet rsColumns = c.createStatement().executeQuery("SELECT COUNT(*) AS NUMBEROFCOLUMNS FROM information_schema.columns WHERE table_name ='UserRoles';");
        int columnCount = 0;
        if(rsColumns.next()) {
            columnCount = rsColumns.getInt(1);
        }

        //Create list of possibleRoles
        List<String> possibleRoles = new ArrayList<>();
        ResultSet rsRoles = c.createStatement().executeQuery("SELECT COLUMN_NAME FROM information_schema.columns WHERE table_name = 'UserRoles' order by ORDINAL_POSITION");
        while(rsRoles.next()) {
            String possibleRole = rsRoles.getString(1);
            possibleRoles.add(possibleRole);
        }

        //Finish query statement
        if(newUser) {
            for(int i = 2 ; i <= columnCount ; i++) { //Skips 1st column, which is 'userId'
                query += ", ?"; //FixMe Stringbuilder
            }
            query += ")";
        } else {
            for(int i = 0 ; i < columnCount ; i++) {
                if(i == 0) {
                    query += possibleRoles.get(i) + " = ?"; //FixMe Stringbuilder
                } else {
                    query += ", " + possibleRoles.get(i) + " = ?"; //FixMe Stringbuilder
                }
            }
            query += " WHERE userId = ?";
        }

        //Setup prepared statement and execute
        PreparedStatement preparedStmt = c.prepareStatement(query);
        preparedStmt.setInt(1, userId);
        for(int i = 2 ; i <= columnCount ; i++) { //Skips 1st column, which is 'userId'
            for(String newRole : roles) {
                if(newRole.equalsIgnoreCase(possibleRoles.get(i-1))) {
                    preparedStmt.setBoolean(i, true);
                    break;
                } else {
                    preparedStmt.setBoolean(i, false);
                }
            }
        }
        if(!newUser) {
            preparedStmt.setInt(columnCount + 1, userId);
        }
        preparedStmt.execute();
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
            throw new DALException(e.getMessage());
        }
    }
}
