# JWT-Auth

This toy project provides implementation of JWT using java in backend API and AngularJS in frontend

It displays how to do authentication and authorization using JWT access tokens.
It also demonstrates, how to maintain session and do revokation of JWT tokens. 

Two types of tokens are used:
1) access token :(JWT token): for stateless api access having 15 min lifetime.
2) refresh token :(Opaque token): for refreshing / reissueing accesstokens. This also helps in maintaining user session in authserver. There is lastAccessed time maintained in server db for expiry of refresh token. It's expiry time is 2 days from lastAccessed time. Last Accessed time is updated in DB on every refresh token request which will be after every 15 min if user is logged in. On Logout, refresh token is invalidated ie it marks the end of usersession. 
  
How to setup:
1) Install postgresql 9.3
2) Create username and password for postgresql DB
3) Create database "authentication" and run Authentication/db/db.sql
3) Open Authentication project in netbeans
4) Add postgresql credentials in Authentication/src/java/com/auth/utils/DbUtils.java
5) Clean and Build Netbeans project to generate war file.
6) Deploy the war file to your server with context path /auth
NOTE: if you want to change context path, then change services.js(xhr url) and  index.html(base HREF) file too. 


