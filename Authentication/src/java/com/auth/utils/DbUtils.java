package com.auth.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbUtils {
    
    public static Connection getConnection(){
        
        try {
            
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection("jdbc:postgresql://localhost:5432/authentication","postgres","YOUR_PASSWORD");                        
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        return null;
    }
    
}
