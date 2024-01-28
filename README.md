# CustomerOnboaring

This is a Spring Boot Project for Customer Onboarding. 

Steps to run:
1. Ensure you have the environment for Spring Boot on your system. This project uses version 3.2.2.
2. This project uses the maven setup, thus has a pom.xml file.
3. To run the tests, just run the command `mvn test`.
4. All APIs require a JWT, without which they throw a 403 Error. See below steps to obtain JWT token.

Steps to obtain JWT:
1. Hit the API: POST http://localhost:8080/rest/auth/login, with the request body
`
{
    "email": "abcd@gmail.com",
    "password": "123456"
}
`
2. This API returns a JWT token. Use it for further requests. The Authorization type is "Bearer Token".
3. To hit the APIs using Postman, go to the "Authorization" Tab, select Type as "Bearer Token", and enter the above obtained JWT.
4. Additionally, here is the Postman Collection: [https://winter-sunset-672310.postman.co/workspace/Agilysis~6f280553-4eb4-40ef-a8fc-0f5880ddf792/collection/26282259-42fd858b-b2ab-4a52-a082-6d92f6fb6126?action=share&creator=26282259](https://winter-sunset-672310.postman.co/workspace/Agilysis~6f280553-4eb4-40ef-a8fc-0f5880ddf792/collection/26282259-42fd858b-b2ab-4a52-a082-6d92f6fb6126?action=share&creator=26282259)https://winter-sunset-672310.postman.co/workspace/Agilysis~6f280553-4eb4-40ef-a8fc-0f5880ddf792/collection/26282259-42fd858b-b2ab-4a52-a082-6d92f6fb6126?action=share&creator=26282259
