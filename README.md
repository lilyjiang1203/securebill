# SecureBill

SecureBill is a Jakarta EE web application that demonstrates secure billing workflows using role-based access control (RBAC), JWT authentication, and multi-tenant data isolation.

The project includes:

- Jakarta Faces / PrimeFaces Web Client
- RBAC REST API
- Multi-Tenant REST API
- Keycloak authentication and authorization
- Podman-based containerized local deployment

## Features

### Role-Based Access Control

SecureBill supports different permissions based on user roles.

| Role | View | Create | Update | Delete |
|---|---:|---:|---:|---:|
| ActiveStudent | ✅ | ✅ | ✅ | ❌ |
| Accounting | ✅ | ✅ | ❌ | ❌ |
| Executive | ✅ | ❌ | ❌ | ✅ |

Authorization is enforced by the secured REST API using JWT claims and Jakarta Security `@RolesAllowed`.

### Multi-Tenant Data Isolation

Authenticated users can manage their own billing records while remaining isolated from other users' data.

Each bill is associated with the authenticated identity derived from the JWT.

The API prevents users from:

- reading another user's bill
- updating another user's bill
- deleting another user's bill
- assigning records to another username through a forged request payload

Tenant identity is determined by the backend from the authenticated JWT rather than trusted from client input.

### Authentication

Authentication is handled through Keycloak using OpenID Connect and JWT.

The web client obtains an access token from Keycloak and includes the bearer token when calling the secured REST APIs.

## Architecture

```text
                     SecureBill Web Client
                  Jakarta Faces + PrimeFaces
                        Port 8081
                            |
              +-------------+-------------+
              |                           |
              v                           v
       RBAC REST API              Multi-Tenant REST API
         Port 8080                     Port 8082
              |                           |
              +-------------+-------------+
                            |
                         Keycloak
                        Port 8180
                     OpenID Connect
                          JWT
                            |
                     LDAP Federation
                            |
                 Active Directory Lab
```
The original development environment integrated Keycloak with Microsoft Active Directory through LDAP for user authentication and identity federation.

For the portfolio deployment, the authentication layer can use dedicated demonstration users without requiring an external Windows Server domain controller.

Technology Stack
Java 21
Jakarta EE 10
Jakarta Faces
PrimeFaces
PrimeFlex
OmniFaces
Jakarta REST
Jakarta Persistence
Hibernate
MicroProfile JWT
MicroProfile Config
Keycloak
WildFly
Maven
H2 Database
Podman
Podman Compose

Project Structure
secure-billing-rbac-demo/
│
├── dmit2015-assignment07-restclient/
│   └── SecureBill Web Client
│
├── dmit2015-assignment07-rbac-restapi/
│   └── Role-Based Access Control REST API
│
├── dmit2015-assignment07-multitenant-restapi/
│   └── Multi-Tenant REST API
│
├── keycloak-export/
│   └── Keycloak realm configuration for local container startup
│
└── compose.yaml
    └── Local multi-container orchestration

Local Containerized Development

The local containerized environment uses the following services:

Service	URL
SecureBill Web Client	http://localhost:8081
Keycloak	http://localhost:8180
RBAC REST API	http://localhost:8080
Multi-Tenant REST API	http://localhost:8082
Environment Variables

Configuration is environment-based so that local, containerized, and cloud environments can use different service addresses without changing application code.

Web Client
KEYCLOAK_PROVIDER_URI
KEYCLOAK_TOKEN_URL
KEYCLOAK_CLIENT_ID
KEYCLOAK_CLIENT_SECRET
RBAC_API_URL
MULTITENANT_API_URL

Secured REST APIs
KEYCLOAK_JWKS_URI
KEYCLOAK_ISSUER

Sensitive values such as the Keycloak client secret are not stored directly in source code.

Local HTTP test files and other sensitive development artifacts are excluded from source control.

Running with Podman Compose

Before starting the application, set the Keycloak client secret:
export KEYCLOAK_CLIENT_SECRET="your-current-keycloak-client-secret"

Start all services:
podman-compose up

To run the containers in the background:
podman-compose up -d

Stop the environment:
podman-compose down

After startup, open:
http://localhost:8081

The Compose environment starts:

Keycloak
RBAC REST API
Multi-Tenant REST API
SecureBill Web Client

The Keycloak realm is imported automatically from the local realm export.

Running Services Individually

The individual Jakarta EE modules can also be run directly with Maven.

RBAC REST API
cd dmit2015-assignment07-rbac-restapi
./mvnw wildfly:run

Multi-Tenant REST API
cd dmit2015-assignment07-multitenant-restapi
./mvnw wildfly:run

SecureBill Web Client
cd dmit2015-assignment07-restclient
./mvnw wildfly:run

Security Design

SecureBill demonstrates two different authorization models.

Role-Based Access Control

RBAC permissions are enforced by the REST API based on roles contained in the authenticated JWT.

The UI may hide unavailable actions for usability, but backend authorization remains the authoritative security control.

Multi-Tenant Authorization

Multi-tenant records are isolated by authenticated identity.

For list operations, only records belonging to the current authenticated user are returned.

For individual resource operations, the API validates both:
resource ID
+
authenticated username

before allowing access.

This prevents insecure direct object reference-style access to another user's billing records.

For create and update operations, the username is derived from the JWT rather than trusted from the submitted request body.

Security Testing

The following authorization scenarios were verified during development:

RBAC
ActiveStudent: Read ✅ Create ✅ Update ✅ Delete ❌
Accounting: Read ✅ Create ✅ Update ❌ Delete ❌
Executive: Read ✅ Create ❌ Update ❌ Delete ✅
Multi-Tenant
User can view their own records ✅
User cannot retrieve another user's record by ID ✅
User cannot update another user's record ✅
User cannot delete another user's record ✅
Forged username values in create requests are overridden by authenticated JWT identity ✅
Screenshots

Screenshots of the following application views will be added:

Dashboard
Login
RBAC Bills
Multi-Tenant My Bills
Role-specific authorization behavior
Deployment

The local portfolio version is fully containerized using Podman Compose.

Azure deployment is planned as the next stage of the SecureBill portfolio project.

A live demo link will be added after deployment.

Project Background

SecureBill was originally developed as a secure REST API and access-control academic project.

It was later refactored into a portfolio application with:

a redesigned user interface
environment-based configuration
JWT role handling
strengthened multi-tenant isolation
containerized REST APIs
containerized WildFly web client
Podman Compose orchestration
cloud deployment preparation
