package com.auth.endpoints;

import com.auth.dao.UserDAO;
import com.auth.dataobjects.Response;
import com.auth.utils.TokenUtils;
import com.auth.utils.Utils;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LogoutRequestEndpoint extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Response endpointResponse = new Response();
        
        if(request.getHeader("Authorization") != null && request.getHeader("Authorization").startsWith("Bearer ")){
                        
            String authHeader = request.getHeader("Authorization");
            String token = authHeader.split(" ")[1];

            DecodedJWT decodedToken = TokenUtils.verifyRSA256Token(token);
            if(decodedToken != null){
                
                Integer rid = decodedToken.getClaim("rid").asInt();
                if(rid != null && rid > 0){
                    
                    boolean result = UserDAO.invalidateRefreshToken(rid);
                    if(result){
                        endpointResponse.setMsg("Logout Success");
                        endpointResponse.resultCode = 0;
                    }
                    else{
                        endpointResponse.setMsg("Internal Server Error");
                        endpointResponse.resultCode = 1;
                    }                            
                }
                else{
                    endpointResponse.setMsg("Invalid Access Token");
                    endpointResponse.resultCode = 1;
                }                                
            }
            else{
                endpointResponse.setMsg("Forbidden");
                endpointResponse.resultCode = 403;    
            }
        }
        else{
            endpointResponse.setMsg("Invalid Authorization Header");
            endpointResponse.resultCode = 1;
        }        
        
        //Write Response                    
        Utils.writeResponse(response, endpointResponse);
    }
}
