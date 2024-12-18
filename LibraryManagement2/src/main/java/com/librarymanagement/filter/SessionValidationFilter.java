package com.librarymanagement.filter;

import com.librarymanagement.Database;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*") // Applies to all requests
public class SessionValidationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = Database.getInstance().getSession();

        String path = httpRequest.getRequestURI();
        boolean isPublic=false;

        // Allow access to index.html and public resources
        if (path.endsWith("index.html") || path.endsWith("login") || path.endsWith("newuser.html") || path.contains("success") ) {
            isPublic=true;
        }
        if(path.contains("verifyotp") || path.contains("verifyemailotp") ){
            if(session!=null && session.getAttribute("email")!=null) {
                isPublic = true;
            }
            clearCaches(httpResponse);
        }
        if (!isPublic) {
            clearCaches(httpResponse);
        }

        // Check session attributes for admin or user login
        boolean isAdminLoggedIn = session != null && session.getAttribute("admin") != null;
        boolean isUserLoggedIn = session != null && session.getAttribute("user") != null;

        if (!isPublic && !isAdminLoggedIn && !isUserLoggedIn) {
            // Redirect to index.html if no session
            request.setAttribute("message", "Session expired or not logged in");
            request.getRequestDispatcher("/index.html").forward(request, response);
        } else {
            // Continue with the request
            chain.doFilter(request, response);
        }
    }

    private void clearCaches(HttpServletResponse httpResponse) {
        httpResponse.setHeader("Cache-Control", "no-store");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setDateHeader("Expires", 0);
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}


        }
    }
}


