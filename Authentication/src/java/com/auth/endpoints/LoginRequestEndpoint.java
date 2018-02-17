package com.auth.endpoints;

import com.auth.dao.UserDAO;
import com.auth.dataobjects.Request;
import com.auth.dataobjects.Response;
import com.auth.utils.JSONUtils;
import com.auth.utils.TokenUtils;
import com.auth.utils.Utils;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginRequestEndpoint extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        Response endpointResponse = new Response();
        
        //Get Input Json
        String requestString    = Utils.getRequestBody(request);

        //Parse Json
        Request parsedRequest   = JSONUtils.parse(requestString);
        if(parsedRequest != null ) {
        
            //Authenticate User
            if( parsedRequest.getAttribute("uid") != null && parsedRequest.getAttribute("pwd") != null){
                
                String username = (String) parsedRequest.getAttribute("uid"); 
                String password = (String) parsedRequest.getAttribute("pwd");                 
                
                boolean result = UserDAO.verifyUserCredentials(username, password);
                
                if(result){
                    
                    // Check Login Type
                    if( parsedRequest.getAttribute("type") != null ){
                        
                        String type = (String) parsedRequest.getAttribute("type");                        
                        switch (type) {
                            case "cookies":
                            {
                                //Create Session
                                HttpSession session = request.getSession();
                                session.setAttribute("username", username);
                                
                                endpointResponse.setMsg("Login Successfull");
                                endpointResponse.resultCode = 0;
                                break;
                            }
                            case "jwt":
                            {
                                //Create Tokens
                                
                                String refreshToken = TokenUtils.generateOpaqueToken();
                                String userId       = UserDAO.getUserIdFromUserName(username);
                                int rid             = UserDAO.insertRefreshTokenForUser(userId, refreshToken);
                                
                                if(rid > 0){
                                    
                                    String accessToken = TokenUtils.createRSA256Token(username, rid);
                                    
                                    endpointResponse.addParameter("accessToken", accessToken);
                                    endpointResponse.addParameter("refreshToken", refreshToken);

                                    endpointResponse.setMsg("Login Successfull");
                                    endpointResponse.resultCode = 0;
                                }
                                else {
                                    endpointResponse.setMsg("Internal Server Error");
                                    endpointResponse.resultCode = 1;
                                }
                                
                                break;
                            }
                            default:
                                endpointResponse.setMsg("Invalid login type");
                                endpointResponse.resultCode = 1;
                                break;
                        }
                    }
                    else {
                        endpointResponse.setMsg("Login type not present");
                        endpointResponse.resultCode = 1;
                    }                                                                                                 
                }
                else{
                    endpointResponse.setMsg("Incorrect Username or Password");
                    endpointResponse.resultCode = 1;
                }
            }
            else{
                endpointResponse.setMsg("Username or Password not present");
                endpointResponse.resultCode = 1;
            }
        }
        else {
            endpointResponse.setMsg("Invalid JSON");
            endpointResponse.resultCode = 1;
        }
        
        //Write Response                    
        Utils.writeResponse(response, endpointResponse);
        
    }
    
    

}
