@User @Update
Feature: Basic User update functionality
### Verify different cases for updating users

@TC021001
@Negative @Update
Scenario: update users password
### create a new user and update its password

Given a "user" with
	| status   | string | ACTIVE             |
	| username | string | updatePasswordName |
	| password | string | updatePasswordPass |
And the request was successful
And the response has a status code of 201
And the response matches
	| status   | string | ACTIVE             |
	| username | string | updatePasswordName |
When update latest users password to "updatedPasswordPass"
And the request was successful
And the response has a status code of 204

@TC021002
@Negative @Update
Scenario: update users username
### create a new user and update its username

Given a "user" with
	| status   | string | ACTIVE             |
	| username | string | updateUsernameName |
	| password | string | updateUsernamePass |
And the request was successful
And the response has a status code of 201
And the response matches
	| status   | string | ACTIVE             |
	| username | string | updateUsernameName |
When update previously created "user"
	| username | string | updatedUsernameName |
And the request was successful
And the response has a status code of 204
Then request previously created "user"
And the request was successful
And the response has a status code of 200
And the response matches
	| id       | key    | user                |
	| status   | string | ACTIVE              |
	| username | string | updatedUsernameName |

@TC021003
@Negative @Update
Scenario: update users status
### create a new user and update its status

Given a "user" with
	| status   | string | ACTIVE             |
	| username | string | updateUsernameName |
	| password | string | updateUsernamePass |
And the request was successful
And the response has a status code of 201
And the response matches
	| status   | string | ACTIVE             |
	| username | string | updateUsernameName |
When update previously created "user"
	| status   | string | SUSPENDED |
And the request was successful
And the response has a status code of 204
Then request previously created "user"
And the request was successful
And the response has a status code of 200
And the response matches
	| id       | key    | user               |
	| status   | string | SUSPENDED          |
	| username | string | updateUsernameName |