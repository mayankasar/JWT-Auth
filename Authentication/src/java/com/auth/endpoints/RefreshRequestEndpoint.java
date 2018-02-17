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

public class RefreshRequestEndpoint extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        Response endpointResponse = new Response();
        
        //Get Input Json
        String requestString    = Utils.getRequestBody(request);

        //Parse Json
        Request parsedRequest   = JSONUtils.parse(requestString);
        if(parsedRequest != null){
            
            if(parsedRequest.getAttribute("refreshToken") != null ){
                
                String refreshToken = (String) parsedRequest.getAttribute("refreshToken");
                
                //check token is valid
                boolean valid = UserDAO.verifyRefreshToken(refreshToken);
                if(valid){
                    
                    //Get User details from refresh token
                    String username = UserDAO.getUserNameFromRefreshToken(refreshToken);
                    int rid         = UserDAO.getRefreshTokenId(refreshToken);
                    if( username != null && rid > 0 ){
                        
                        //create new access token
                        String accessToken = TokenUtils.createRSA256Token(username, rid);
                
                        //update last accessed time of refresh token
                        boolean success = UserDAO.updateLastAccesedForRefreshToken(refreshToken);
                        if(success){
                            
                            endpointResponse.addParameter("accessToken", accessToken);                        
                            endpointResponse.setMsg("Refresh Successfull");
                            endpointResponse.resultCode = 0;
                        }   
                        else{
                            endpointResponse.setMsg("Internal Server Error");
                            endpointResponse.resultCode = 1;
                        }                                            
                    }
                    else{
                        endpointResponse.setMsg("Internal Server Error");
                        endpointResponse.resultCode = 1;                
                    }                    
                }
                else{
                    endpointResponse.setMsg("Forbidden");
                    endpointResponse.resultCode = 403;                
                }                                
            }
            else{
                endpointResponse.setMsg("Invalid Refresh Request");
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
