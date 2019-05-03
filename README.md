# Banking showcase project
## Introduction

This is am example/reference project to showcase my architecture design and coding abilities.

The following application will be a demo banking system, which is built with [thorntail](https://thorntail.io/) using maven.
The packaged application is a [UberJar](https://imagej.net/Uber-JAR) which is a jar which consists of the application code (war)
and in this case the wildfly swarm server ( which is basically thorntail [now](https://jaxenter.com/thorntail-version-2-1-0-final-148293.html) )

Please consider that this was initially a test project for an application to a company and I decided to make it public for future applications.
Some solutions built in this project are only workarounds to comply with the requirement and also stay in a considerate
time frame for completing of the project. 

## Features

The Main functions of this application are the following:

- Manage users ( Create, Update, Delete )
- Manage bank accounts ( Create, Update, Delete, Add user to account, Remove user from account, List users of account, List accounts of user )
- Manage Funds ( Deposit money in account, Withdraw money from account )
- Perform transactions ( Perform transaction between accounts )

## Implementation details

In the following section I will describe some implementation details, which would otherwise only be seen by inspecting the code.

If you don't want to inspect the code for such details continue reading otherwise you can jump to the section __installation__.

### Password handling
The passwords are not stored in clear text but are being salted and encrypted using BCrypt, but the usage of the BCrypt library is
encapsulated in a service to enable the ability for switching the library without affecting the code.

### Authentication and Authorization
Handling the authentication is done very lightly and with the knowledge that this is not the best practice nor a really good
solution, however given that this was initially a test project for an application, to stay within a considerate time frame
more work in this area was omitted.

The Authentication is handled using [Json Web Token](https://jwt.io/) and upon login a token for the subject will be created with
an expiration date set to 15 minutes in the future ( of the time the login happened ). However the expiration date will not be updated
if requests are made by user, so there is no idle session timeout but a general lifetime of 15 minutes per session.

Additionally the invalidation of the token is handled just as a workaround and needs rework for a real application, since the existing token will be set
to a expiration date some hours in the past and then returned to the user. 

__This is dangerous and opens an attack vector which should be considered!__

### Currency Exchange
In order to enable the ability to deposit or pay with any chosen currency [this](https://www.currencyconverterapi.com/) 3rd party API, by _Manny Vergel_,
was used to exchange the money between currencies. This means the mentioned API will be called via HTTP requests to validate the entered currency
and exchange between currencies.

__NOTE:__ The used API key in this project is now invalid and cannot be used anymore. To use the application as is a new key needs to be requested and put in the application.

#### Requiring and setting your personal API key

In order to retrieve your API key for this 3rd party navigate to [this](https://free.currencyconverterapi.com/free-api-key) link and enter you E-Mail to retrieve the API key.

Once you received your key nagivate to the class __com.nicomadry.Banking.itl.service.CurrencyServiceImpl__ and enter your key in the constant __com.nicomadry.Banking.itl.service.CurrencyServiceImpl.API_KEY__. 

### IBAN validation
Since validating the IBAN was a requirement of the project, the [FintechToolbox](https://fintechtoolbox.com/) API was used to validate entered IBAN's against this service.
This API is also called via HTTP requests.

__NOTE:__ The used API key in this project is now invalid and cannot be used anymore. To use the application as is a new key needs to be requested and put in the application.

#### Requiring and setting your personal API key

In order to retrieve your API key for this 3rd party navigate to [this](https://fintechtoolbox.com/#api-key) link and enter you E-Mail to retrieve the API key.

Once you received your key nagivate to the class __com.nicomadry.Banking.itl.service.IBANServiceImpl__ and enter your key in the constant __com.nicomadry.Banking.itl.service.IBANServiceImpl.API_KEY__. 

__Note:__ It is important to keep "token " with the trailing whitespace in the constant and paste your retrieved key after that. ( Example: "token XXXXYYYYZZZZ" )

### Hibernate
This project uses [Hibernate](https://hibernate.org/) to enable the application some abstraction to the database. The result is not needing to know the exact
database which is used since the application uses HQL for queries which will be translated to the correct database dialect by hibernate.

## Installation
This section presumes the project is __already__ cloned or download. If not already done, perform these steps now and then continue with the
instructions below.

### Pre-requisites
In order to run the application some pre-requisites need to be met. The following section will run you through the complete installation of the application.

### Database
A PostgreSQL database is needed. It doesn't matter how it is installed, you just need to be able to perform import scripts in it (sql files).
If the database is installed and running execute the script found under _resources/Database_init_. 

__IMPORTANT:__ the file path location inside script _1010_CreateTableSpace.sql_ needs to be adjusted to you environment!

Further explanations of the scripts are added in the README file of the corresponding folder.

_Since the application is using hibernate another database can also be used, however the requirement of the test was to use PostgreSQL_

### Hibernate
Hibernate is configured to auto create the tables and data needed for the entities of the system.
In order for the application to connect to the database, the connection-url needs to be configured in the file _resources/project-defaults.yml_.

## Starting the application

After the installation is completed, our application can be started for testing purposes.

### Starting the application without docker
In order to build and run this application without docker the following steps are needed:

1. Open a CMD in the project dir
2. Enter 'mvn thorntail:run'

The application will now be automatically built and started. After the process is finished the application is reachable under:
localhost:8080

### Starting the application with docker
In order to build and run this application with docker the following steps are needed:

1. Open a CMD in the project dir
2. Enter 'mvn thorntail:package'
3. Enter 'docker build -t [container name] .'
4. To run the created container enter 'docker run -p 8080:8080 [container name]'

Afterwards the application will be reachable under:
localhost:8080

## Masterdata Import

After the application is started, the scripts found under _resources/Database_masterdata_ can be executed. The database is now configured correctly and ready to use.

_Further explanations of the scripts are added in the README file of the corresponding folder._

The User import is needed, since all API requests need an authenticated session and that is not possible without a user to login with.

## Testing the application

Once the installation is completed and the server is running, you can start testing the application
with the provided [Postman](https://www.getpostman.com/) collection found under _resources/Postman/Banking.postman_collection.json_.

Beware that a good documentation of the REST interface is missing and a swagger file with the definitions would be needed, however
I added some description to the requests which are not intuitive and might need some clarification.

## Personal note

Thank you for reading until here and looking at my showcase application.

This definitely was a interesting journey for me, since I never built an application of this scale with these technologies from scratch.
I learned quite a lot and additionally understood some features or concepts more in depth, which previously where only understood superficial.
I know that a lot could be done better and a lot of improvements are possible but I wanted to draw a line where this project is at now. Besides I personally think this is something which could be shown with slight proud.

However the project likely will not be continued in any way and bugs will likely not be addressed since this is a showcase project, which is not used actively.
