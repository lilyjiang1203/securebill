<img width="1268" height="674" alt="image" src="https://github.com/user-attachments/assets/ad29b39a-22ca-42e3-a5e5-8ad4e0426c3b" />### SecureBill

SecureBill is a Jakarta EE web application that demonstrates secure billing workflows using OpenID Connect authentication, JWT-based authorization, role-based access control (RBAC), and multi-tenant data isolation.

The project was originally developed as an academic security application and later enhanced into a portfolio project with:

Keycloak Identity and Access Management (IAM)
JWT authentication and authorization
Backend-enforced RBAC
Multi-tenant security controls
Containerized deployment using Podman
Cloud-ready configuration for Azure deployment

## Features
Role-Based Access Control (RBAC)

SecureBill implements authorization based on roles provided by Keycloak JWT claims.

Supported roles:

Role	View	Create	Update	Delete
ActiveStudent	✅	✅	✅	❌
Accounting	✅	✅	❌	❌
Exclusive	✅	❌	❌	✅

Authorization is enforced at the REST API layer using:

Jakarta Security @RolesAllowed
JWT role claims
Backend authorization checks

The frontend may hide unavailable actions for better user experience, but the backend remains the final security boundary.

## Authentication and Identity Management

SecureBill uses Keycloak as the Identity Provider (IdP).

Authentication is implemented using:

OpenID Connect (OIDC)
OAuth 2.0 token flow
JSON Web Tokens (JWT)

Authentication workflow:
```text
User
 |
 | username/password
 |
 v
SecureBill Web Client
 |
 | OpenID Connect Token Request
 |
 v
Keycloak Identity Provider
 |
 | JWT Access Token
 |
 v
Secure REST APIs
```
The issued JWT contains identity and authorization information, including:

username
email
realm roles

Example JWT role claim:
```json
{
  "realm_access": {
    "roles": [
      "ActiveStudent"
    ]
  }
}
```
The backend validates JWT signatures using Keycloak's JWKS endpoint before allowing access to protected resources.

## Keycloak Configuration
# Realm
dmit2015-realm

# Client
securebill-jwt-client

Protocol:

OpenID Connect

Authentication:

Client ID + Client Secret

Configured Realm Roles:

Role	Description
ActiveStudent	Create and manage permitted billing records
Accounting	View and create billing records
Exclusive	Manage delete operations

## Multi-Tenant Data Isolation

SecureBill demonstrates tenant isolation by ensuring users can only access their own billing data.

Each billing record is associated with the authenticated identity extracted from the JWT.

The API prevents:

Reading another user's billing records
Updating another user's records
Deleting another user's records
Assigning records to another username through forged requests

The authenticated username is determined by:
```text
JWT Identity
     |
     v
Backend Security Context
     |
     v
Tenant Ownership Validation
```
Client-submitted usernames are never trusted.

## Security Design

SecureBill demonstrates multiple security principles.

Identity and Authentication

Implemented:

Centralized identity management using Keycloak
OpenID Connect authentication
JWT-based identity propagation
Secure token validation

## Authorization

Implemented:

Role-Based Access Control (RBAC)
Backend authorization enforcement
JWT role claim validation
Protected REST endpoints

## Data Protection

Implemented:

Server-side tenant verification
Ownership validation before CRUD operations
Protection against insecure direct object references (IDOR)

## Architecture
# Application Architecture
```text
                         SecureBill Web Client
                       Jakarta Faces + PrimeFaces
                              Port 8081
                                  |
                 +----------------+----------------+
                 |                                 |
                 v                                 v

          RBAC REST API                 Multi-Tenant REST API
             Port 8080                       Port 8082
                 |                                 |
                 +----------------+----------------+
                                  |
                                  v

                         Keycloak Identity Provider
                              OpenID Connect
                                  |
                                  v

                              JWT Token
```
# Cloud Authentication Architecture

The portfolio deployment uses Azure-hosted Keycloak.
```text
                 SecureBill Application

                         |
                         |
                         v

              Azure Container Apps Keycloak

                         |
                         |
                 OpenID Connect / JWT

                         |
                         v

              Role-Based REST Authorization
```

# Deployment Architecture

The portfolio version uses a hybrid deployment model:

- Jakarta EE applications are containerized locally using Podman Compose.
- Keycloak authentication is hosted using Azure Container Apps.
- JWT-based authentication allows secure communication between services.
  
## Technology Stack
# Backend
Java 21
Jakarta EE 10
Jakarta REST
Jakarta Faces
PrimeFaces
PrimeFlex
OmniFaces
Jakarta Persistence
Hibernate
MicroProfile JWT
MicroProfile Config
# Identity and Security
Keycloak
OpenID Connect
OAuth 2.0
JWT
RBAC
LDAP Federation
# Application Server
WildFly
# Database
H2 Database
# Containerization
Podman
Podman Compose
# Cloud
Azure Container Apps
Azure-hosted Keycloak

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
├── dmit2015-assignment07-multitenant-restapi/
│   └── Multi-Tenant REST API
│
├── keycloak-export/
│   └── Keycloak realm configuration
│
└── compose.yaml
    └── Container orchestration configuration
```

## Containerized Development Environment

SecureBill runs as multiple isolated containers.

Service	Purpose	Port
securebill-web	Jakarta Faces frontend	8081
securebill-rbac-api	RBAC authorization API	8080
securebill-multitenant-api	Multi-tenant billing API	8082
Keycloak	Identity Provider	8180

## Environment Configuration

Configuration is managed through environment variables.

This allows different configurations for:

local development
container deployment
cloud deployment
# Web Client
KEYCLOAK_PROVIDER_URI
KEYCLOAK_TOKEN_URL
KEYCLOAK_CLIENT_ID
KEYCLOAK_CLIENT_SECRET
RBAC_API_URL
MULTITENANT_API_URL
# REST APIs
KEYCLOAK_JWKS_URI
KEYCLOAK_ISSUER

Sensitive information such as Keycloak client secrets is stored outside source code.

## Running with Podman Compose

Set the Keycloak client secret:
```bash
export KEYCLOAK_CLIENT_SECRET="your-keycloak-client-secret"
```
Start all containers:
```bash
podman-compose up
```
Run in background:
```bash
podman-compose up -d
```
Stop:
```bash
podman-compose down
```
Application:
```text
http://localhost:8081
```

## Security Testing
# Authentication Testing

Verified:

✅ User authentication through Keycloak
✅ JWT token generation
✅ JWT role claims
✅ Secure API authorization

# RBAC Testing

Verified:

ActiveStudent
View      ✅
Create    ✅
Update    ✅
Delete    ❌

Accounting
View      ✅
Create    ✅
Update    ❌
Delete    ❌

Exclusive
View      ✅
Create    ❌
Update    ❌
Delete    ✅

## Multi-Tenant Security Testing

Verified:

✅ User can access own billing records
✅ User cannot access another user's records
✅ User cannot modify another user's records
✅ User cannot delete another user's records
✅ Forged username values are ignored

## Screenshots

The following screenshots demonstrate SecureBill authentication, authorization, and security controls.

---
## Authentication

### Login Page
<img width="1070" height="628" alt="image" src="https://github.com/user-attachments/assets/d1a83d15-b0d3-4feb-aff1-5fbd57ed1a36" />

The SecureBill login page authenticates users through Keycloak OpenID Connect.

---
### Successful Login
<img width="1077" height="662" alt="image" src="https://github.com/user-attachments/assets/9d0cfa63-0613-4566-9bb8-3f025e9f7b6e" />

After successful authentication, users are redirected to SecureBill with permissions based on their assigned Keycloak roles.

---

### JWT Token Claims
<img width="499" height="259" alt="image" src="https://github.com/user-attachments/assets/4cd70794-cfa9-4415-991f-fbef4d97619c" />
The JWT access token contains authenticated identity information and realm roles issued by Keycloak.

Example:

```json
{
  "preferred_username": "active-student",
  "realm_access": {
    "roles": [
      "ActiveStudent"
    ]
  }
}
```

## Keycloak Configuration
# Client Configuration
<img width="1255" height="673" alt="image" src="https://github.com/user-attachments/assets/acd7c7e1-4e25-45ef-8cbf-0f3d9234ad78" />

# User Role Mapping
<img width="1274" height="671" alt="image" src="https://github.com/user-attachments/assets/2f1f162b-2c0f-443d-9688-430e6a8091d4" />

# Role-Based Access Control (RBAC)
ActiveStudent Access
<img width="1075" height="660" alt="image" src="https://github.com/user-attachments/assets/11561ace-8cb3-4f0f-9367-3e651b44d546" />
ActiveStudent permissions:
View billing records
Create billing records
Update billing records
Delete operation restricted

Accounting Access
<img width="1077" height="662" alt="image" src="https://github.com/user-attachments/assets/0f99bf87-5c0e-4381-9f3e-f408fba871f5" />

Accounting permissions:
View billing records
Create billing records
Update/Delete operations restricted

Exclusive Access
<img width="1074" height="630" alt="image" src="https://github.com/user-attachments/assets/f3d7d3a4-572b-4c0e-a6d2-4033022ab1f9" />
Exclusive permissions:
View billing records
Delete billing records
Create/Update operations restricted

# Multi-Tenant Security

Screenshots demonstrate:

User can access their own billing records
User cannot access another user's records
Backend authorization prevents unauthorized resource access

# Azure Keycloak Deployment
Keycloak authentication service is deployed using Azure Container Apps.
<img width="1268" height="674" alt="image" src="https://github.com/user-attachments/assets/58e6c766-98f3-4fb3-ab68-ccc4becae847" />


## Deployment

SecureBill is deployed using containerized Jakarta EE services.

Architecture:
```text
Browser
 |
 SecureBill Web Client
 |
 REST APIs
 |
 Keycloak Authentication
```

## Container Deployment

<img width="1039" height="656" alt="image" src="https://github.com/user-attachments/assets/014f0608-237b-4676-94cf-c09f7785bf6c" />

Keycloak Authentication Service

<img width="1274" height="668" alt="image" src="https://github.com/user-attachments/assets/e6a31c2b-2680-4a20-9f60-d88efd200d4c" />

Application Running

<img width="1079" height="660" alt="image" src="https://github.com/user-attachments/assets/4dd133c4-8f90-4c29-bd05-6c9912d16c27" />


## Future Improvements

Planned enhancements:

Deploy REST APIs to Azure Container Apps
Deploy frontend application to cloud hosting
Replace H2 with production database service
Add automated security testing
Add CI/CD pipeline

## Project Background

SecureBill was developed as a secure billing application focusing on identity management and access control.

The project demonstrates practical implementation of:

Identity and Access Management (IAM)
Authentication and Authorization
OpenID Connect
JWT security
RBAC design
Multi-tenant application security
Containerized enterprise application deployment
