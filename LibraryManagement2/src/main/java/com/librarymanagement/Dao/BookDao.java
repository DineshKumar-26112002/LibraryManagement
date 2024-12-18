package com.librarymanagement.Dao;

import com.librarymanagement.Database;
import com.librarymanagement.DbConnection;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.IssueBook;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BookDao {
    private Connection connection;

    public List<Book> getAllBooks() {
        String getBooks = "SELECT * FROM book;";
        List<Book> books = new ArrayList<>();
        try {
            connection = DbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(getBooks);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setName(rs.getString("name"));
                book.setAuthor(rs.getString("author"));
                book.setPublisher(rs.getString("publisher"));
                book.setQuantity(rs.getInt("quantity"));
                books.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }

    public int addBook(String name, String author, String publisher, String quantity) {
        String isBook = "Select * from book where name=? AND author=? AND publisher=?";
        try {
            connection = DbConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(isBook);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, author);
            preparedStatement.setString(3, publisher);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) return -1;
            String addBook = "INSERT INTO book (name, author, publisher,quantity) VALUES (?, ?, ?,?)";
            preparedStatement = connection.prepareStatement(addBook);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, author);
            preparedStatement.setString(3, publisher);
            preparedStatement.setInt(4, Integer.parseInt(quantity));
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    public List<Book> findBooksByName(String bookName) {
        String getBooksByname = "SELECT * FROM book where quantity>0 and name like ?;";
        List<Book> books = new ArrayList<>();
        try {
            connection = DbConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(getBooksByname);
            preparedStatement.setString(1, "%" + bookName + "%");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setName(rs.getString("name"));
                book.setAuthor(rs.getString("author"));
                book.setPublisher(rs.getString("publisher"));
                book.setQuantity(rs.getInt("quantity"));
                books.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }

    public Book issueBook(String bookId) {
        int userId = (int) Database.getInstance().getSession().getAttribute("userid");
        String getBookname = "SELECT * from Book where id=?";
        try {
            connection = DbConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(getBookname);
            preparedStatement.setString(1, bookId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                String bookname = rs.getString("name");
                Book book = new Book();
                book.setName(bookname);
                book.setAuthor(rs.getString("author"));
                book.setPublisher(rs.getString("publisher"));
                String issueBookSql = "INSERT INTO issuebook (userid, bookid,status,bookname) VALUES (?, ?,?,?)";
                preparedStatement = connection.prepareStatement(issueBookSql);
                preparedStatement.setInt(1, userId);
                preparedStatement.setInt(2, Integer.parseInt(bookId));
                preparedStatement.setString(3, "Not returned");
                preparedStatement.setString(4, bookname);
                preparedStatement.executeUpdate();

                String updateUserSql = "UPDATE user SET bookcount = bookcount + 1 WHERE id = ?";
                preparedStatement = connection.prepareStatement(updateUserSql);
                preparedStatement.setInt(1, userId);
                preparedStatement.executeUpdate();

                String updateBookSql = "UPDATE Book SET quantity = quantity - 1 WHERE id = ?";
                preparedStatement = connection.prepareStatement(updateBookSql);
                preparedStatement.setInt(1, Integer.parseInt(bookId));
                preparedStatement.executeUpdate();
                return book;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int returnBook(String bookName) {
        int userId = (int) Database.getInstance().getSession().getAttribute("userid");
        String getBookCount = "Select bookcount from user where id=?";
        try {
            connection = DbConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(getBookCount);
            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next() && rs.getInt("bookcount") <= 0)
                return -1;
            String checkQuantitySql = "Select bookid from issuebook WHERE userid=? AND bookname LIKE ? AND status=?";
            preparedStatement = connection.prepareStatement(checkQuantitySql);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, "%" + bookName + "%");
            preparedStatement.setString(3, "Not returned");
            rs = preparedStatement.executeQuery();
            if (rs.next()) {
                int bookId = rs.getInt("bookid");

                String updateStatus = "UPDATE issuebook set status='Returned'  WHERE userid=? AND bookid=? AND status=?";
                preparedStatement = connection.prepareStatement(updateStatus);
                preparedStatement.setInt(1, userId);
                preparedStatement.setInt(2, bookId);
                preparedStatement.setString(3, "Not returned");
                preparedStatement.executeUpdate();

                String updateUserSql = "UPDATE user SET bookcount = bookcount - 1 WHERE id = ?";
                preparedStatement = connection.prepareStatement(updateUserSql);
                preparedStatement.setInt(1, userId);
                preparedStatement.executeUpdate();

                String updateBookSql = "UPDATE Book SET quantity = quantity + 1 WHERE id = ?";
                preparedStatement = connection.prepareStatement(updateBookSql);
                preparedStatement.setInt(1, bookId);
                preparedStatement.executeUpdate();
                return 1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<IssueBook> getIssuedBooks() {
        String getBooks = "SELECT * FROM issuebook;";
        List<IssueBook> books = new ArrayList<>();
        try {
            connection = DbConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(getBooks);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt("userid");
                String sql1 = "SELECT email FROM user where id=?";
                preparedStatement = connection.prepareStatement(sql1);
                preparedStatement.setInt(1, userId);
                ResultSet rs1 = preparedStatement.executeQuery();
                if (rs1.next()) {
                    String email = rs1.getString("email");
                    IssueBook book = new IssueBook();
                    book.setId(rs.getInt("id"));
                    book.setEmail(email);
                    book.setBookName(rs.getString("bookname"));
                    book.setStatus(rs.getString("status"));
                    books.add(book);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }
}
