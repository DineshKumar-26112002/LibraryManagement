package com.librarymanagement.filter;

import com.librarymanagement.Database;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Provider
public class SessionValidationFilter implements ContainerRequestFilter {
    @Context
    private HttpServletRequest httpRequest;

    @Context
    private UriInfo uriInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String path = requestContext.getUriInfo().getPath();
//        String path1=
        HttpSession session = Database.getInstance().getSession();
        System.out.println(path);
        String fullUri = httpRequest.getRequestURI();
        System.out.println(fullUri+"sssssssssssssssss");

        // List of paths that do not require authentication
        if (path.equals("index.html") || path.startsWith("public") || path.startsWith("users/login") || path.startsWith("api/users/newuser") || path.equals("users/verifyotp") || path.equals("users/verifyemailotp")) {
            return; // Skip validation for public endpoints
        }

        // Check session attributes for admin or user login
        boolean isAdminLoggedIn = session != null && session.getAttribute("admin") != null;
        boolean isUserLoggedIn = session != null && session.getAttribute("user") != null;

        if (!isAdminLoggedIn && !isUserLoggedIn) {
            // Redirect to the login page if no one is logged in
            String redirectUrl = "/success.html?message=Session%20expired%20or%20not%20logged%20in&redirectUrl=index.html";
            requestContext.abortWith(
                    Response.status(Response.Status.FOUND)
                            .header("Location", redirectUrl)
                            .build()
            );
        }
    }
}


