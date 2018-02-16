# JWT-Auth

This toy project provides implementation of JWT using java in backend API and AngularJS in frontend

It displays how to do authentication and authorization using JWT access tokens.
It also demonstrates, how to maintain session and do revokation of JWT tokens. 

Two types of tokens are used:
1) access token :(JWT token): for stateless api access having 15 min lifetime.
2) refresh token :(Opaque token): for refreshing / reissueing accesstokens. This also helps in maintaining user session in authserver. There is lastAccessed time maintained in server db for expiry of refresh token. It's expiry time is 2 days from lastAccessed time. Last Accessed time is updated in DB on every refresh token request which will be after every 15 min if user is logged in. On Logout, refresh token is invalidated ie it marks the end of usersession. 
  

