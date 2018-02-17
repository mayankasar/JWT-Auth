package com.auth.dataobjects;

public class NameValueType {
    
    public String name;
    public String type;
    public Object value;
    
    
    public NameValueType(String name, String type, Object value){
        this.name   = name;
        this.value  = value;
        this.type   = type;
    }
    
}
