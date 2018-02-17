package com.auth.utils;

import com.auth.dataobjects.NameValueType;
import com.auth.dataobjects.Request;
import com.auth.dataobjects.Response;
import java.util.Iterator;
import org.json.JSONObject;

public class JSONUtils {
    
    public static Request parse(String json){
        
        try {
            
            Request request = new Request();            
            JSONObject requestJsonObject = new JSONObject(json);
            
            Iterator itr = requestJsonObject.keys();
            while(itr.hasNext()){
                String key = (String) itr.next();
                Object val = requestJsonObject.get(key);
                
                if(key.equals("uid")){
                    request.addAttribute(key, String.valueOf(val));
                }                
                else{
                    //Default: Treat each key:value as String:String
                    request.addAttribute(key, String.valueOf(val));
                }
            }
            
            return request;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }                
    }
    
    public static String toJson(Response response){
        
        String resultJson = "";
        try
        {
            if(response != null){
                JSONObject jsonObj = new JSONObject();

                jsonObj.put("resultCode", response.resultCode);
                
                for(NameValueType triplet : response.argList){
                    
                    if(triplet.type.equals("String")){
                        jsonObj.put(triplet.name, triplet.value);
                    }                
                }
                
                resultJson = jsonObj.toString();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        return resultJson;
    }
}
