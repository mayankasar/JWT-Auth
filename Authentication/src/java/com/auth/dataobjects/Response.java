package com.auth.dataobjects;

import java.util.ArrayList;

public class Response {
   
    public int resultCode;
    public ArrayList<NameValueType> argList = new ArrayList<>();
    
    public void addAttribute(String name, String type, Object value){
        argList.add(new NameValueType(name, type, value));
    }
    
    public void setMsg(String msg){
        argList.add(new NameValueType("message", "String", msg));
    }
    
    public void addParameter(String name, String value){
        argList.add(new NameValueType(name, "String", value));
    }   
}
