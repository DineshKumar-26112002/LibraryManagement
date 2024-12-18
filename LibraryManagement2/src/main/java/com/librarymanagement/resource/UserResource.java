package com.librarymanagement.resource;

import com.librarymanagement.Dao.UserDao;
import com.librarymanagement.Database;
import com.librarymanagement.model.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;;

@Path("/users")
public class UserResource {
    private UserDao userDao;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validUSer(@FormParam("email") String email, @FormParam("password") String password, @Context HttpServletRequest request) {
        if (email == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Name And Email Not be Empty").build();
        }
        userDao = new UserDao();
        String result = userDao.checkCredentials(email, password);
        HttpSession session = request.getSession(true);
        Database.getInstance().setSession(session);
        HttpSession session1 = Database.getInstance().getSession();
        if (result != null) {
            String otp = Database.getInstance().generateOtp(5);
            System.out.println(otp + "otp");
            // Create a new session if one doesn't exist
            session1.setAttribute("email", email);
            session1.setAttribute("mobileotp", otp);
            System.out.println(email);
//            Database.getInstance().sendOtpToMobile(result,otp);
            String redirectUrl = "/verifyotp.html";
            return Response.status(Response.Status.FOUND)
                    .header("Location", redirectUrl)
                    .build();

        }
        System.out.println("null");
        // Redirect to success.html with message and redirectUrl as query parameters
        String redirectUrl = "/success.html?message=Invalid%20email%20or%20password&redirectUrl=index.html";
        return Response.status(Response.Status.FOUND) // HTTP 302 Found
                .header("Location", redirectUrl)
                .build();

//        }
    }

    @POST
    @Path("/newuser")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUser(@FormParam("name") String name, @FormParam("email") String email, @FormParam("password") String password, @FormParam("mobile") String mobile) {
        System.out.println("in");
        if (email == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Name And Email Not be Empty").build();
        }
        userDao = new UserDao();
        int result = userDao.addUser(name, email, password, mobile);
        if (result == -1) {
            // Redirect to success.html with message and redirectUrl as query parameters
            String redirectUrl = "/success.html?message=Email%20or%20Phone%20No%20already%20exist&redirectUrl=index.html";
            return Response.status(Response.Status.FOUND) // HTTP 302 Found
                    .header("Location", redirectUrl)
                    .build();
        }
        // Redirect to admin page on successful login
        String redirectUrl = "/success.html?message=User%20registered%20successfully&redirectUrl=index.html";
        return Response.status(Response.Status.FOUND) // HTTP 302 Found
                .header("Location", redirectUrl)
                .build();
    }

    @GET
    @Path("/getusers")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUsers() {
        userDao = new UserDao();
        List<User> userList = userDao.getAllUsers();
        System.out.println(userList.size());
        if (userList.size() == 0) {
            String redirectUrl = "/success.html?message=No%20Users%20found&redirectUrl=adminpage.html";
            return Response.status(Response.Status.FOUND)
                    .header("Location", redirectUrl)
                    .build();
        }
        return Response.ok(userList).build();
    }


    @GET
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request, @Context HttpHeaders headers) {
        HttpSession session = Database.getInstance().getSession();

        // Get the referer header to determine the source page
        String referer = headers.getHeaderString("Referer");

        if (referer != null && referer.contains("userpage.html")) {
            if (session != null && session.getAttribute("admin") == null) {
                Database.getInstance().setSession(null);
                session.invalidate();
            } else if (session != null) {
                session.removeAttribute("user");
            }
        } else if (referer != null && referer.contains("adminpage.html")) {
            if (session != null && session.getAttribute("user") == null) {
                session.invalidate();
                Database.getInstance().setSession(null);
            } else if (session != null) {
                session.removeAttribute("admin");
            }
        }
        String redirectUrl = "/success.html?message=Logout%20Successfull...!&redirectUrl=index.html";

        // Clear caching
        return Response.status(Response.Status.FOUND)
                .header("Cache-Control", "no-store") // HTTP 1.1
                .header("Pragma", "no-cache")      // HTTP 1.0
                .header("Expires", 0)               // Proxies
                .header("Location", redirectUrl)
                .build();
    }

    @POST
    @Path("/verifyotp")
    public Response verifyOtp(@FormParam("otp") String otp, @Context HttpServletRequest request) {
        userDao = new UserDao();
        HttpSession session = Database.getInstance().getSession();
        String sessionOtp = (String) session.getAttribute("mobileotp");
        String email = (String) session.getAttribute("email");
        if (sessionOtp.equals(otp)) {
            String name = userDao.getNameByEmail(email);
            String emailOtp = Database.getInstance().generateOtp(5);
            System.out.println(emailOtp + "emailotp");
            String subject = "Your One-Time Password (OTP) for Authentication";
            String body = "Dear " + name + ",\n" +
                    "\n" +
                    "We have received a request to log in to your account on LibraryManagement2024. Please use the following One-Time Password (OTP) to complete your authentication:\n" +
                    "\n" +
                    "Your OTP:" + emailOtp + "\n" +
                    "\n" +
                    "This OTP is valid for 10 minutes. If you did not initiate this request, please ignore this email or contact our support team immediately.\n" +
                    "\n" +
                    "Thank you for choosing LibraryManagement2024.\n" +
                    "\n" +
                    "Best regards,\n" +
                    "[Your Library Name]\n" +
                    "LibraryManagement2024 Support Team";
//                Database.getInstance().sendEmail(email,subject,body, null);
            Database.getInstance().getSession().setAttribute("emailotp", emailOtp);
            String redirectUrl = "/verifyemailotp.html";
            return Response.status(Response.Status.FOUND) // HTTP 302 Found
                    .header("Location", redirectUrl)
                    .build();
        }
        String redirectUrl = "/success.html?message=Invalid%20OTP%20,Please%20try%20again%20later&redirectUrl=index.html";
        return Response.status(Response.Status.FOUND) // HTTP 302 Found
                .header("Location", redirectUrl)
                .build();
    }

    @POST
    @Path("/verifyemailotp")
    public Response verifyEmailOtp(@FormParam("otp") String otp, @Context HttpServletRequest request) {
        userDao = new UserDao();
        HttpSession session = Database.getInstance().getSession();
        String sessionOtp = (String) session.getAttribute("emailotp");
        String email = (String) session.getAttribute("email");
        if (sessionOtp.equals(otp)) {
            int role = userDao.getRole(email);
            String redirectUrl = null;
            if (role == 0) {
                session.setAttribute("admin", email);
                redirectUrl = "/success.html?message=Login%20Successfull...!&redirectUrl=adminpage.html";
            } else {
                session.setAttribute("user", email);
                int userId = userDao.getUserId(email);
                session.setAttribute("userid", userId);
                redirectUrl = "/success.html?message=Login%20Successfull...!&redirectUrl=userpage.html";
            }
            return Response.status(Response.Status.FOUND) // HTTP 302 Found
                    .header("Location", redirectUrl)
                    .build();
        }
        String redirectUrl = "/success.html?message=Invalid%20OTP%20,Please%20try%20again%20later&redirectUrl=index.html";
        return Response.status(Response.Status.FOUND) // HTTP 302 Found
                .header("Location", redirectUrl)
                .build();
    }


}
