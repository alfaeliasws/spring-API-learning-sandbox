# Address API Spec

## Create Address

Endpoint: POST /api/contacts/{idContact}/addresses

Request Header:
 - X-API-TOKEN: Token (Mandatory)

Request Body:
```json
{
    "street" : 'Jalan apa',
    "city" : 'Cairo',
    "province" : 'Cairo City',
    "country" :  'Egypt',
    "postalCode" :  '40112'
}
```

Response Body (Success):
```json
{
    "data":{
        "id" : 'random-string',
        "street" : 'Jalan apa',
        "city" : 'Cairo',
        "province" : 'Cairo City',
        "country" :  'Egypt',
        "postalCode" :  '40112'
    }
}
```

Response Body (Failed):
```json
{
    "errors":"Contact Not Found"
}
```

## Get Address
Endpoint: GET api/contacts/{idContact}/addresses//{idAddress}

Request Header:
 - X-API-TOKEN: Token (Mandatory)

Response Body (Success):
```json
{
    "data":{
        "id" : 'random-string',
        "street" : 'Jalan apa',
        "city" : 'Cairo',
        "province" : 'Cairo City',
        "country" :  'Egypt',
        "postalCode" :  '40112'
    }
}
```

Response Body (Failed, 404):
```json
{
    "errors":"Contact Not Found, Address Not Found, ???"
}
```

## List Address
Endpoint: GET api/contacts/{idContact}/addresses/

Query Param:
- name : String, contact first name or last name, using like query, optional
- phone: String, contact phone, using like query, optional
- email : String, contact email, using like query, optional
- page : Integer, start from 0, default 0
- size : Integer, default 10


Request Header:
 - X-API-TOKEN: Token (Mandatory)

:Response Body (Success):
```json
{
    "data":[
       {
            "id" : 'random-string',
            "street" : 'Jalan apa',
            "city" : 'Cairo',
            "province" : 'Cairo City',
            "country" :  'Egypt',
            "postalCode" :  '40112'
        }
    ],
    "paging":{
        "currentPage" : 0,
        "totalPage" : 10,
        "size" : 10
    }
}
```


Response Body (Failed, 404):
```json
{
    "errors":"Contact Not Found"
}
```


## Update Address
Endpoint: PUT api/contacts/{idContact}/addresses/{idAddress}

Request Header:
 - X-API-TOKEN: Token (Mandatory)


Request Body:
```json
{
    "street" : 'Jalan apa',
    "city" : 'Cairo',
    "province" : 'Cairo City',
    "country" :  'Egypt',
    "postalCode" :  '40112'
}
```

Response Body (Success):
```json
{
    "data":{
        "id" : 'random-string',
        "street" : 'Jalan apa',
        "city" : 'Cairo',
        "province" : 'Cairo City',
        "country" :  'Egypt',
        "postalCode" :  '40112'
    }
}
```

Response Body (Failed, 404):
```json
{
    "errors":"Address Is Not Found"
}
```

## Remove Address
Endpoint: DELETE api/contacts/{idContact}/addresses/{idAddress}

Request Header:
 - X-API-TOKEN: Token (Mandatory)

Response Body (Success):
```json
{
    "data":"OK"
}
```

Response Body (Failed, 404):
```json
{
    "errors":"Contact Not Found"
}
```