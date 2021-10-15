# 2Play REST API


## Contents
#### 1. [Developer](#developer)
#### 2. [Technical specifications](#technical-specifications:)
#### 3. [Hosting](#hosting)
#### 4. [API Documentation](#api-documentation)
#### 5. [Endpoints](#endpoints)
#### 6. [API URL](#api-url)
#### 7. [Contact](#contact)

### Developer:

*Damjan Miloshevski on behalf of [2Play Technologies Limited](https://2playlabs.com/)*

### Technical specifications:

* Framework
    - KTOR by JetBrains
* Programming language
    - Kotlin
* Database
    - MongoDB
    - Firebase Storage
* Sports data fetched from: [The SportsDB API](https://www.thesportsdb.com/api.php)

### Hosting

Hosted by Heroku

### API Documentation

- Coming soon

### Endpoints

1. Betting tips
    - @POST^ **{{api_url}}/betting-tips** Save betting tip (requires JSON object in the BODY)
    - @GET  **{{api_url}}/betting-tips** Get betting tips
    - @GET **{{api_url}}/betting-tips/{id}** Get betting tip by id
    - @GET **{{api_url}}/betting-tips/{sport}/upcoming** Get upcoming tips by sport
    - @GET **{{api_url}}/betting-tips/{sport}/older** Get older tips by sport
    - @PUT^ **{{api_url}}/betting-tips** Update betting tip (requires JSON object in the BODY)
    - @DELETE^ **{{api_url}}/betting-tips/{id}** Delete betting tip by id
    - @DELETE^ **{{api_url}}/betting-tips** Delete all betting tips
   

2. Users
    - @POST **{{api_url}}/users/signin** Sign in user (requires JSON object in the BODY)
    - @POST **{{api_url}}/users/register** Register user (requires JSON object in the BODY)
    - @POST **{{api_url}}/users/signout** Signout a user (requires JSON object in the BODY)
    - @POST **{{api_url}}/users/feedback** Send a feedback (requires JSON object in the BODY)
    - @POST^ **{{api_url}}/users/notifications/new-tips** Send a push notification to a topic
    - @PUT^ **{{api_url}}/users/{id}/change_password** Change user's password (requires JSON object in the BODY)
    - @GET^ **{{api_url}}/users** Get all users
    - @GET^ **{{api_url}}/users/{email}** Get user by email
    - @GET **{{api_url}}/users/verify/{id}** Verify user (generated automatically after user registers)
    - @GET **{{api_url}}/users/tokens/refresh-token/{refresh_token}** Refresh user's token
    
>Clarification
>
> ^ - requires Bearer token (authenticated endpoint)

### API URL

This API is public and available to be consumed.
> Note: Some routes are authenticated and require a Bearer token to return data otherwise the response will be 401 Unauthorized.
> Please reach out the developer for more details

The api can be consumed in the following url:

https://betting-doctor.herokuapp.com/api/v1/{endpoint}

### Contact:

1. Email: d.miloshevski@gmail.com
2. Skype: damjan.milosevski
3. [LinkedIn](https://www.linkedin.com/in/damjanmiloshevski/)
4. Phone number (please request this in a private message via one of the above options)


