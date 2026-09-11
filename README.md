# SecureBill

SecureBill is a Jakarta EE web application that demonstrates secure billing workflows using role-based access control (RBAC), JWT authentication, and multi-tenant data isolation.

The project includes a web client and two secured REST APIs:

- RBAC REST API
- Multi-Tenant REST API
- Jakarta Faces / PrimeFaces Web Client

## Features

### Role-Based Access Control

SecureBill supports different permissions based on user roles.

| Role | View | Create | Update | Delete |
|---|---|---|---|---|
| ActiveStudent | ✅ | ✅ | ✅ | ❌ |
| Accounting | ✅ | ✅ | ❌ | ❌ |
| Executive | ✅ | ❌ | ❌ | ✅ |

Authorization is enforced by the secured REST API using JWT claims.

### Multi-Tenant Data Isolation

Authenticated users can manage their own billing records while remaining isolated from other users' data.

Each bill is associated with the authenticated user, ensuring that users can only access their own records.

### Authentication

Authentication is handled through Keycloak using OpenID Connect and JWT.

The web client obtains an access token from Keycloak and sends it with requests to the secured REST APIs.

## Architecture

```text
                     SecureBill Web Client
                  Jakarta Faces + PrimeFaces
                            |
              +-------------+-------------+
              |                           |
              v                           v
       RBAC REST API              Multi-Tenant REST API
       Jakarta REST               Jakarta REST
              |                           |
              +-------------+-------------+
                            |
                         Keycloak
                     OpenID Connect
                          JWT
...

The original development environment also integrated Keycloak with Active Directory through LDAP for user authentication and role mapping.

## Technology Stack

- Java 21
- Jakarta EE 10
- Jakarta Faces
- PrimeFaces
- PrimeFlex
- OmniFaces
- Jakarta REST
- Jakarta Persistence
- Hibernate
- MicroProfile JWT
- MicroProfile Config
- Keycloak
- WildFly
- Maven
- H2 Database

## Project Structure

```text
secure-billing-rbac-demo/
│
├── dmit2015-assignment07-restclient/
│   └── SecureBill Web Client
│
├── dmit2015-assignment07-rbac-restapi/
│   └── Role-Based Access Control REST API
│
└── dmit2015-assignment07-multitenant-restapi/
    └── Multi-Tenant REST API

##Local Development

The local development environment uses the following services:

Service	URL
SecureBill Web Client	http://localhost:8080
Keycloak	http://localhost:8180
RBAC REST API	http://localhost:8181
Multi-Tenant REST API	http://localhost:8182

##Environment Variables
The Web Client requires the following environment variables:
export KEYCLOAK_PROVIDER_URI="http://localhost:8180/realms/dmit2015-realm/"
export KEYCLOAK_CLIENT_ID="your-keycloak-client-id"
export KEYCLOAK_CLIENT_SECRET="your-keycloak-client-secret"
The secured REST APIs require:
export KEYCLOAK_JWKS_URI="http://localhost:8180/realms/dmit2015-realm/protocol/openid-connect/certs"
export KEYCLOAK_ISSUER="http://localhost:8180/realms/dmit2015-realm"

Secrets and local HTTP test files are excluded from source control.

Running the Application

Start Keycloak first.

Then start the RBAC REST API:

cd dmit2015-assignment07-rbac-restapi
./mvnw wildfly:run

Start the Multi-Tenant REST API:

cd dmit2015-assignment07-multitenant-restapi
./mvnw wildfly:run

Finally, start the SecureBill Web Client:

cd dmit2015-assignment07-restclient
./mvnw wildfly:run

Open:

http://localhost:8080

##Security Design

SecureBill demonstrates two authorization models.

##Role-Based Access Control

Operations are authorized according to roles contained in the user's JWT.

##Multi-Tenant Authorization

Authenticated users can access only resources associated with their own identity.

Backend authorization remains enforced by the REST APIs even when the user interface hides unavailable actions.

##Screenshots

Screenshots of the dashboard, RBAC interface, multi-tenant workspace, and authentication flow will be added here.

##Deployment

Azure deployment is planned for the portfolio version of SecureBill.

A live demo link will be added after deployment.

##Project Background

SecureBill was originally developed as a secure REST API and access-control project and was later refactored into a portfolio application with an updated user interface, environment-based configuration, and cloud deployment preparation.