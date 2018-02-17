package com.auth.utils;

import com.auth.dataobjects.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Utils {
    
    
    public static String getRequestBody(HttpServletRequest request) {
        
        String reqString = "";
        
        try {
        
            int bytesRead;
            StringBuilder sb;
            char[] buffer       = new char[128];                    
            InputStream is      = request.getInputStream();
                        
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
                
                sb = new StringBuilder();
                while((bytesRead = br.read(buffer)) > 0){
                    sb.append(buffer, 0, bytesRead);
                }
            }   
            
            reqString = sb.toString();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
        return reqString;
    }
    
    public static void writeResponse(HttpServletResponse httpResponse, Response response){
        
        try{
            
            String responseJson = JSONUtils.toJson(response);
            
            
            PrintWriter out = httpResponse.getWriter();
            out.println(responseJson);
            out.flush();
            out.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }        
    }
    
}
