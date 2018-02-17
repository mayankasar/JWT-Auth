package com.auth.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

public class TokenUtils {

    private static final String KEYS_PATH = System.getenv("CATALINA_HOME");
    private static final String PRIVATE_KEY_FILE = "jwt.key";
    private static final String PUBLIC_KEY_FILE = "jwt.pub";
    
    private static RSAPrivateKey PRIVATE_KEY = null;
    private static RSAPublicKey PUBLIC_KEY = null;
    
    public static final String ISSUER = "authentication";
 
    
    public static String generateOpaqueToken() {
        
        try {
        
            String uuid = UUID.randomUUID().toString();
 
            uuid = uuid.replaceAll("-", "");
  
            SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
            String random = prng.nextInt(Integer.MAX_VALUE)+"";            
            
            String currentTime = System.nanoTime()+"";
            
            String input = random + currentTime;
            
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            sha.update(input.getBytes());
 
            byte[] result =  sha.digest();

            Base64.Encoder encoder = Base64.getEncoder();
            String encoded = encoder.encodeToString(result);
            
            return uuid + encoded;
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        return null;
    }
    
    
    public static String createRSA256Token(String username, int rid) {
        String token = null;
        try {
            
            initRSAKeys();
            
            Algorithm algorithm = Algorithm.RSA256(PUBLIC_KEY, PRIVATE_KEY);
            
            token = JWT.create()
                    .withIssuer(ISSUER)
                    .withClaim("username", username)
                    .withClaim("rid",rid)
                    .withExpiresAt(new Date(new Date().getTime() +15 * 60 * 1000) ) //15mins
                    .withJWTId(UUID.randomUUID().toString())
                    .withIssuedAt(new Date())
                    
                    .sign(algorithm);

        } catch (JWTCreationException e) {
            e.printStackTrace();
        }
        return token;
    }
    
    public static DecodedJWT verifyRSA256Token(String token) {
   
        try {
            
            initRSAKeys();
            
            Algorithm algorithm = Algorithm.RSA256(PUBLIC_KEY, PRIVATE_KEY);
            
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)                    
                    .build(); 
            
            DecodedJWT jwt = verifier.verify(token);                                    
            return jwt;
            
        } catch (JWTVerificationException e) {            
            e.printStackTrace();
        }
        
        return null;        
    }
    
    public static void initRSAKeys(){
        if(PRIVATE_KEY == null || PUBLIC_KEY == null){
            if(new File(KEYS_PATH+ File.separator + PRIVATE_KEY_FILE ).exists()
                && new File(KEYS_PATH+ File.separator + PUBLIC_KEY_FILE ).exists()){
                
                loadRSAKeys();
            }
            else{
                generateRSAKeys();
            }
        }
    }
    
    public static void generateRSAKeys() {
        
        try {            

            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.generateKeyPair();

            PUBLIC_KEY  = (RSAPublicKey) kp.getPublic();
            PRIVATE_KEY = (RSAPrivateKey) kp.getPrivate();
            
            FileOutputStream out;
            out = new FileOutputStream(KEYS_PATH + File.separator + PRIVATE_KEY_FILE );
            out.write(PRIVATE_KEY.getEncoded());
            out.close();
            
            out = new FileOutputStream(KEYS_PATH + File.separator + PUBLIC_KEY_FILE );
            out.write(PUBLIC_KEY.getEncoded());
            out.close();                        

        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    public static void loadRSAKeys(){
        
        try {            

            Path privateKeyPath = Paths.get(KEYS_PATH + File.separator + PRIVATE_KEY_FILE);
            byte[] privateKeyBytes = Files.readAllBytes(privateKeyPath);
            
            Path publicKeyPath = Paths.get(KEYS_PATH + File.separator + PUBLIC_KEY_FILE);
            byte[] publicKeyBytes = Files.readAllBytes(publicKeyPath);
            
            PKCS8EncodedKeySpec ks1 = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory kf1 = KeyFactory.getInstance("RSA");
            PRIVATE_KEY = (RSAPrivateKey) kf1.generatePrivate(ks1);
            
            X509EncodedKeySpec ks2 = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory kf2 = KeyFactory.getInstance("RSA");
            PUBLIC_KEY = (RSAPublicKey) kf2.generatePublic(ks2);                        

        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
}
