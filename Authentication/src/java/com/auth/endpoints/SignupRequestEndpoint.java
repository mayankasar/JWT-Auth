package com.auth.endpoints;

import com.auth.dao.UserDAO;
import com.auth.dataobjects.Request;
import com.auth.dataobjects.Response;
import com.auth.utils.JSONUtils;
import com.auth.utils.Utils;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SignupRequestEndpoint extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        Response endpointResponse = new Response();
        
        //1) Get Input
        String requestString    = Utils.getRequestBody(request);

        //2) Parse Json
        Request parsedRequest   = JSONUtils.parse(requestString);
        if(parsedRequest != null ) {
            
            if(parsedRequest.getAttribute("username") != null && parsedRequest.getAttribute("password") != null){
                
                String username = (String) parsedRequest.getAttribute("username");
                String password = (String) parsedRequest.getAttribute("password");                
                if(username.length() >= 6 && username.length() <= 50 && Pattern.matches("[a-zA-Z]+", username)){
                    
                    if(UserDAO.isUsernameAvailable(username)){

                        if(password.length() >= 6 && password.length() <= 50){
                            
                            String userId = UserDAO.createUser(username, password);
                            if(userId != null){
                                endpointResponse.setMsg("Signup Success");
                                endpointResponse.resultCode = 0;
                            }
                            else{
                                endpointResponse.setMsg("Internal Server Error");
                                endpointResponse.resultCode = 1;
                            }
                        }
                        else{
                            endpointResponse.setMsg("Incorrect password");
                            endpointResponse.resultCode = 1;
                        }                                
                    }
                    else{
                        endpointResponse.setMsg("Username not available");
                        endpointResponse.resultCode = 1;
                    }
                }
                else{
                    endpointResponse.setMsg("Incorrect username");
                    endpointResponse.resultCode = 1;
                }                
            }
            else{
                endpointResponse.setMsg("Username or password not found");
                endpointResponse.resultCode = 1;
            }
        }
        else{
            endpointResponse.setMsg("Invalid JSON");
            endpointResponse.resultCode = 1;
        }
        
        //Write Response                    
        Utils.writeResponse(response, endpointResponse);
    }

}
