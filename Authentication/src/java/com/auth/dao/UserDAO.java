package com.auth.dao;

import com.auth.utils.DbUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

public class UserDAO {
    
    public static final String REFRESH_TOKEN_TIMEOUT = "2 days";
    
    public static boolean verifyUserCredentials(String username, String passwd){
        
        Connection con = null;
        
        try{        
            con = DbUtils.getConnection();
            
            PreparedStatement ps = con.prepareStatement("select count(*) from login where username=? and password=?");
            ps.setString(1, username);
            ps.setString(2, passwd);
            
            ResultSet rs = ps.executeQuery();
            if(rs.next() && rs.getInt("count") > 0 ){
                return true;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                if(con != null)
                    con.close();
            }
            catch(Exception e){}
        }
        return false;
    }
    
    public static String getUserIdFromUserName(String username){
        
        Connection con = null;
        try{
        
            con = DbUtils.getConnection();
            
            PreparedStatement ps = con.prepareStatement("select userid from login where username=?");
            ps.setString(1, username);
            
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getString("userid");
            }
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                if(con != null)
                    con.close();
            }
            catch(Exception e){}
        }
        return null;
    }
    
    public static int insertRefreshTokenForUser(String userId, String token){
        
        Connection con = null;
        try{        
            con = DbUtils.getConnection();
            
            PreparedStatement ps = con.prepareStatement("insert into refreshtokens(token, userid) values(?,?) returning refreshtokensid");
            ps.setString(1, token);
            ps.setString(2, userId);
            
            ResultSet rs = ps.executeQuery();
            if( rs.next()){
                return rs.getInt("refreshtokensid");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                if(con != null)
                    con.close();
            }
            catch(Exception e){}
        }
        return -1;
    }
    
    public static boolean verifyRefreshToken(String token){
        
        Connection con = null;
        try{        
            con = DbUtils.getConnection();
            
            PreparedStatement ps = con.prepareStatement("select count(*) from refreshtokens where token = ? and  lastaccessed > ( now() - interval '"+REFRESH_TOKEN_TIMEOUT+"' )");
            ps.setString(1, token);            
            
            ResultSet rs = ps.executeQuery();
            if( rs.next() && rs.getInt("count") > 0){
                return true;                
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                if(con != null)
                    con.close();
            }
            catch(Exception e){}
        }
        return false;
    }
    
    public static boolean updateLastAccesedForRefreshToken(String token){
        
        Connection con = null;
        try{
        
            con = DbUtils.getConnection();
            
            PreparedStatement ps = con.prepareStatement("update refreshtokens set lastaccessed = now() where token = ?");                        
            ps.setString(1, token);
                       
            if( ps.executeUpdate() > 0 ){
                return true;                
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                if(con != null)
                    con.close();
            }
            catch(Exception e){}
        }
        return false;
    }
    
    public static String getUserNameFromRefreshToken(String token){
        
        Connection con = null;
        try{        
            con = DbUtils.getConnection();
            
            PreparedStatement ps = con.prepareStatement("select username from login, refreshtokens where login.userid = refreshtokens.userid and token=?");
            ps.setString(1, token);
            
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getString("username");
            }
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                if(con != null)
                    con.close();
            }
            catch(Exception e){}
        }
        return null;
    }
    
    public static int getRefreshTokenId(String token){
        
        Connection con = null;
        try{        
            con = DbUtils.getConnection();
            
            PreparedStatement ps = con.prepareStatement("select refreshtokensid from refreshtokens where token=? ");
            ps.setString(1, token);
            
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt("refreshtokensid");
            }
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                if(con != null)
                    con.close();
            }
            catch(Exception e){}
        }
        return -1;
    }
    
    public static String createUser(String username, String password){
        
        Connection con = null;
        try{        
            con = DbUtils.getConnection();
            
            String userId = UUID.randomUUID().toString();
            
            PreparedStatement ps = con.prepareStatement("insert into login(username, password, userid) values(?,?,?)");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, userId);
            
            if( ps.executeUpdate() > 0){
                return userId;                
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                if(con != null)
                    con.close();
            }
            catch(Exception e){}
        }
        return null;        
    }

    public static boolean isUsernameAvailable(String username){
        
        Connection con = null;
        try{
        
            con = DbUtils.getConnection();
            
            PreparedStatement ps = con.prepareStatement("select count(*) from login where username=?");
            ps.setString(1, username);
            
            ResultSet rs = ps.executeQuery();
            if(rs.next() && rs.getInt("count") > 0){
                return false;
            }            
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                if(con != null)
                    con.close();
            }
            catch(Exception e){}
        }
        return true;
    }
    
    public static boolean invalidateRefreshToken(int rid){
        
        Connection con = null;
        try{
        
            con = DbUtils.getConnection();
            
            PreparedStatement ps = con.prepareStatement("delete from refreshtokens where refreshtokensid=?");
            ps.setInt(1, rid);
            
            if(ps.executeUpdate() > 0){
                return true;
            }            
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                if(con != null)
                    con.close();
            }
            catch(Exception e){}
        }
        return false;
    }
}
