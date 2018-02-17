package com.auth.endpoints;

import com.auth.dataobjects.Request;
import com.auth.dataobjects.Response;
import com.auth.utils.JSONUtils;
import com.auth.utils.TokenUtils;
import com.auth.utils.Utils;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class PingRequestEndpoint extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        Response endpointResponse = new Response();
        
        //1) Get Input
        String requestString    = Utils.getRequestBody(request);

        //2) Parse Json
        Request parsedRequest   = JSONUtils.parse(requestString);
        if(parsedRequest != null ) {
            
            if(parsedRequest.getAttribute("type") != null){
                String type = (String) parsedRequest.getAttribute("type");
                
                if(type.equals("cookies")){
                    HttpSession session = request.getSession(false);
                    
                    if(session != null && session.getAttribute("username") != null){                        
                        String userName = (String) session.getAttribute("username");                        
                        endpointResponse.setMsg("Pong : " + userName);
                        endpointResponse.resultCode = 0;
                    } 
                    else{
                        endpointResponse.setMsg("Pong : Unknown");
                        endpointResponse.resultCode = 0;    
                    }
                }
                else if(type.equals("jwt")){
                    if(request.getHeader("Authorization") != null && request.getHeader("Authorization").startsWith("Bearer ")){
                        
                        String authHeader = request.getHeader("Authorization");
                        String token = authHeader.split(" ")[1];
                        
                        DecodedJWT decodedToken = TokenUtils.verifyRSA256Token(token);
                        if(decodedToken != null){
                            String userName = decodedToken.getClaim("username").asString();
                            endpointResponse.setMsg("Pong : " + userName);
                            endpointResponse.resultCode = 0;
                        }
                        else{
                            endpointResponse.setMsg("Pong : Forbidden");
                            endpointResponse.resultCode = 403;    
                        }
                    }
                    else{
                        endpointResponse.setMsg("Invalid Authorization Header");
                        endpointResponse.resultCode = 1;
                    }                    
                }
                else{
                    endpointResponse.setMsg("Invalid Ping Type");
                    endpointResponse.resultCode = 1;
                }
            }
            else {
                endpointResponse.setMsg("Invalid Ping Request");
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
