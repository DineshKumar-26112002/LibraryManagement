package com.librarymanagement.Dao;

import com.librarymanagement.Database;
import com.librarymanagement.DbConnection;
import com.librarymanagement.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private Connection connection;


    public String checkCredentials(String username, String password) {
        String isValid = "SELECT * from Credentials where username=? and password=?";
        try {
            connection = DbConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(isValid);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                isValid = "SELECT mobile FROM user where email=?";
                preparedStatement = connection.prepareStatement(isValid);
                preparedStatement.setString(1, username);
                rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    String mobile = rs.getString("mobile");
                    System.out.println(mobile);
                    return mobile;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public int addUser(String name, String email, String password, String mobile) {
        String isUser = "Select * from user where email=? or mobile=?";
        try {
            connection = DbConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(isUser);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, mobile);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) return -1;
            String addUser = "INSERT INTO user (name, email, mobile,bookcount) VALUES (?, ?, ?,?)";
            preparedStatement = connection.prepareStatement(addUser);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setLong(3, Long.parseLong(mobile));
            preparedStatement.setInt(4, 0);
            preparedStatement.executeUpdate();
            String addCredential = "INSERT INTO credentials (username, password,role) VALUES (?, ?,?)";
            preparedStatement = connection.prepareStatement(addCredential);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            preparedStatement.setInt(3, 1);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    public List<User> getAllUsers() {
        String getStudents = "SELECT * FROM user;";
        List<User> users = new ArrayList<>();
        try {
            connection = DbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(getStudents);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setMobile(rs.getLong("mobile"));
                user.setBookCount(rs.getInt("bookcount"));
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public String getNameByEmail(String email) {
        String getNameByEmail = "Select name from user where email=?";
        try {
            connection = DbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(getNameByEmail);
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) return rs.getString("name");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getRole(String email) {
        String getRole = "Select role from credentials where username=?";
        try {
            connection = DbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(getRole);
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) return rs.getInt("role");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getUserId(String email) {
        String getRole = "Select id from user where email=?";
        try {
            connection = DbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(getRole);
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public User getUser() {
        User user = new User();
        String getRole = "Select name,email from user where id=?";
        try {
            connection = DbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(getRole);
            statement.setInt(1, (int) Database.getInstance().getSession().getAttribute("userid"));
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
