# Neueda Assessment - ATM Service
Bill MacNamara
October 2021

## Running Instructions

An installation of Maven is the only pre-requisite to run this application.

To run the application, navigate to the project base directory (named neueda-assessment) in a terminal/command line and
run the following command:

`mvn spring-boot:run`

## APIs

The application has two API endpoints:

### Balance Check

Method: POST
* URL: http://localhost:8080/api/v1/atm/balance
* Body (JSON):
    - accountNumber (integer)
    - pin (integer)
    
Example body:

```json
{
    "accountNumber": 987654321,
    "pin": 4321
}
```

### Withdrawal Request

Method: POST
* URL: http://localhost:8080/api/v1/atm/withdraw
* Body (JSON):
    - accountNumber (integer)
    - pin (integer)
    - withdrawalAmount (integer)
    - atmId (integer)

Example body:

```json
{
  "accountNumber": 987654321,
  "pin": 4321,
  "withdrawalAmount": 1200,
  "atmId": 1
}
```