package com.auth.dataobjects;

import java.util.ArrayList;

public class Request {
    
    private ArrayList<NameValuePair> argList = new ArrayList<>();
    
    public void addAttribute(String name, Object value){
        argList.add(new NameValuePair(name, value));
    }
    
    public Object getAttribute(String name){
        
        for(NameValuePair pair : argList){
            if(pair.name.equals(name)){
                return pair.value;
            }
        }
        
        return null;
    }
}
