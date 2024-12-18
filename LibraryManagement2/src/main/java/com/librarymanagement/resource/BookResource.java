package com.librarymanagement.resource;

import com.librarymanagement.Dao.BookDao;
import com.librarymanagement.Dao.UserDao;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.IssueBook;
import com.librarymanagement.model.User;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/books")
public class BookResource {
    private BookDao bookDao;

    @GET
    @Path("/getbooks")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getBooks(@Context HttpHeaders headers) {
        String referer = headers.getHeaderString("Referer");
        bookDao = new BookDao();
        List<Book> bookList = bookDao.getAllBooks();
        if (bookList.size() == 0) {
            if (referer != null && referer.contains("userpage.html")) {
                String redirectUrl = "/success.html?message=No%20Books%20found&redirectUrl=userpage.html";
                return Response.status(Response.Status.FOUND)
                        .header("Location", redirectUrl)
                        .build();
            }
            String redirectUrl = "/success.html?message=No%20Books%20found&redirectUrl=adminpage.html";
            return Response.status(Response.Status.FOUND)
                    .header("Location", redirectUrl)
                    .build();
        }
        return Response.ok(bookList).build();
    }

    @POST
    @Path("/addbook")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addBook(@FormParam("name") String name, @FormParam("author") String author, @FormParam("publisher") String publisher, @FormParam("quantity") String quantity) {
        bookDao = new BookDao();
        int result = bookDao.addBook(name.toUpperCase(), author.toUpperCase(), publisher.toUpperCase(), quantity);
        if (result == -1) {
            // Redirect to success.html with message and redirectUrl as query parameters
            String redirectUrl = "/success.html?message=Book%20already%20exist&redirectUrl=adminpage.html";
            return Response.status(Response.Status.FOUND) // HTTP 302 Found
                    .header("Location", redirectUrl)
                    .build();
        }
        // Redirect to admin page on successful login
        String redirectUrl = "/success.html?message=Book%20added%20successfully&redirectUrl=adminpage.html";
        return Response.status(Response.Status.FOUND) // HTTP 302 Found
                .header("Location", redirectUrl)
                .build();
    }

    @GET
    @Path("/getbookbyname")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookByName(@QueryParam("name") String bookName) {
        bookDao = new BookDao();
        if (bookName == null || bookName.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"Book name cannot be empty\"}")
                    .build();
        }

        List<Book> bookList = bookDao.findBooksByName(bookName.toUpperCase());
        System.out.println(bookList.size());

        if (bookList.size() == 0) {
            // Redirect to success page if no books are found
            String redirectUrl = "/success.html?message=No%20Books%20found&redirectUrl=userpage.html";
            return Response.status(Response.Status.FOUND)
                    .header("Location", redirectUrl)
                    .build();
        }

        return Response.ok(bookList).build();
    }

    @POST
    @Path("/issuebook")
    @Produces(MediaType.APPLICATION_JSON)
    public Response issueBook(@FormParam("bookId") String bookId) {
        bookDao = new BookDao();
        Book book = bookDao.issueBook(bookId);
        if (book == null) {
            // Redirect to success page if no books are found
            String redirectUrl = "/success.html?message=Cannot%20issue%20book&redirectUrl=userpage.html";
            return Response.status(Response.Status.FOUND)
                    .header("Location", redirectUrl)
                    .build();
        }
        User user = new UserDao().getUser();
        String subject = "Book Issued Successfully";
        String body = "Dear " + user.getName() + ",\n" +
                "\n" +
                "We are pleased to inform you that the book titled " + book.getName() + " has been successfully issued to your account.\n" +
                "\n" +
                "Thank you for using LibraryManagement2024. If you have any questions or need assistance, feel free to reach out to us.\n" +
                "\n" +
                "Happy reading!\n" +
                "\n" +
                "Best regards,\n" +
                "LibraryManagement2024 Support Team";
        //send email to registered email after getting book along with the book details in the pdf format
//        Database.getInstance().sendEmail(user.getEmail(), subject,body,book);
        String redirectUrl = "/success.html?message=Book%20issued%20successfully&redirectUrl=userpage.html";
        return Response.status(Response.Status.FOUND)
                .header("Location", redirectUrl)
                .build();
    }

    @POST
    @Path("/returnbook")
    @Produces(MediaType.APPLICATION_JSON)
    public Response returnBook(@FormParam("name") String bookName) {
        bookDao = new BookDao();
        int result = bookDao.returnBook(bookName);
        if (result == -1) {
            String redirectUrl = "/success.html?message=You%20have%20no%20book%20return&redirectUrl=userreturnbook.html";
            return Response.status(Response.Status.FOUND)
                    .header("Location", redirectUrl)
                    .build();
        }
        if (result == 1) {
            User user = new UserDao().getUser();
            String subject = "Book returned Successfully";
            String body = "Dear " + user.getName() + ",\n" +
                    "\n" +
                    "We are pleased to inform you that the book titled " + bookName.toUpperCase() + " has been successfully returned from your account.\n" +
                    "\n" +
                    "Thank you for using LibraryManagement2024. If you have any questions or need assistance, feel free to reach out to us.\n" +
                    "\n" +
                    "Happy reading!\n" +
                    "\n" +
                    "Best regards,\n" +
                    "LibraryManagement2024 Support Team";
            //send email to registered email after returning book
//           Database.getInstance().sendEmail(user.getEmail(), subject,body,null);
            String redirectUrl = "/success.html?message=Book%20returned%20succesfully&redirectUrl=userpage.html";
            return Response.status(Response.Status.FOUND)
                    .header("Location", redirectUrl)
                    .build();
        }
        String redirectUrl = "/success.html?message=Book%20name%20doesn't%20match&redirectUrl=userreturnbook.html";
        return Response.status(Response.Status.FOUND)
                .header("Location", redirectUrl)
                .build();
    }

    @GET
    @Path("/getissuedbooks")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getIsuuedBooks() {
        bookDao = new BookDao();
        List<IssueBook> bookList = bookDao.getIssuedBooks();
        if (bookList.size() == 0) {
            String redirectUrl = "/success.html?message=No%20Books%20is%20issued%20yet&redirectUrl=adminpage.html";
            return Response.status(Response.Status.FOUND)
                    .header("Location", redirectUrl)
                    .build();
        }
        return Response.ok(bookList).build();
    }
}
