# User API Spec

## Register User

Endpoint: POST /api/users

Request Body:
```json
{
    "username" : 'johnson',
    "password" : 'rahasia',
    "name" : 'Johnson Mole'
}
```
Response Body (success):
```json
{
    "data":"OK"
}
```

Response Body (failed):
```json
{
    "errors":"Username must not blank, ???"
}
```
j
## Login User

Endpoint: POST /api/auth/login

Request Body:
```json
{
    "username" : 'johnson',
    "password" : 'rahasia'
}
```
Response Body (success):
```json
{
    "token":"TOKEN",
    "expiredAt": 129041248129 // milisecond
}
```

Response Body (failed, 401):
```json
{
    "errors":"Username or password wrong"
}
```

## Get User
Endpoint: GET api/users/current

Request Header:
    X-API-TOKEN: Token (Mandatory)

Response Body (success):
```json
{
    "data":{
        "username" : "johnson",
        "name" : "Johnson Mole"
    }
}
```

Response Body (failed, 401):
```json
{
    "errors":"Unauthorized"
}
```

## Update User
Endpoint: PATCH api/users/current

Request Header:
    X-API-TOKEN: Token (Mandatory)


Request Body:
```json
{
    "name" : 'johnson', // put if only want to update name
    "password" : 'new password' // put if only want to update password
}
```

Response Body (success):
```json
{
    "data":{
        "username" : "johnson",
        "name" : "Johnson Mole"
    }
}
```

Response Body (failed, 401):
```json
{
    "errors":"Unauthorized"
}
```

## Logout User
Endpoint: DELETE api/auth/logout

Request Header:
    X-API-TOKEN: Token (Mandatory)

Response Body (success):
```json
{
    "data":"OK"
}
```