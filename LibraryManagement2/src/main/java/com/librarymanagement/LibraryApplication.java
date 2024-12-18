package com.librarymanagement;

import com.librarymanagement.filter.SessionValidationFilter;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class LibraryApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(SessionValidationFilter.class); // Register the filter
        // Add other resource classes here
        return classes;
    }
}

