# Property Management System

A comprehensive property management solution built with Spring Boot and PostgreSQL.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Setup](#setup)
- [API Documentation](#api-documentation)
  - [Authentication](#authentication)
  - [Users](#users)
  - [Properties](#properties)
  - [Categories](#categories)
  - [Amenities](#amenities)
  - [Images](#images)
  - [Ratings](#ratings)
  - [Transactions](#transactions)

## Overview

The Property Management System is a RESTful API that enables real estate agencies to manage properties, agents, clients, and transactions. It provides robust security features, including JWT-based authentication, role-based access control, and two-factor authentication.

## Features

- User authentication and authorization with JWT
- Two-factor authentication (2FA)
- Password reset functionality
- Role-based access control (Admin, Agent, Client)
- Property management with categories and amenities
- Image upload and management
- Rating and review system
- Transaction processing
- Email notifications

## Technology Stack

- Java 17
- Spring Boot 3.4.5
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT
- Thymeleaf (for email templates)
- Maven

## Setup

1. **Clone the repository**

2. **Database Setup**

   - Create a PostgreSQL database named `property_management`
   - Configure database credentials in `application.properties`

3. **Configure Email Service**

   - Update email configuration in `application.properties`

4. **Build the project**

   ```bash
   mvn clean install
   ```

5. **Run the application**

   ```bash
   mvn spring-boot:run
   ```

   The API will be available at `http://localhost:8080/api`

6. **Default Admin User**

   - An admin user is automatically created on application startup
   - Default credentials:
     - Email: admin@property.com
     - Password: Admin@123
   - You can customize these in `application.properties`:
     ```properties
     app.init-admin=true
     app.admin.email=admin@property.com
     app.admin.password=Admin@123
     app.admin.firstName=System
     app.admin.lastName=Administrator
     ```

7. **Test Data (Optional)**
   - To seed the database with test data, run the application with the "dev" profile:
     ```bash
     mvn spring-boot:run -Dspring.profiles.active=dev
     ```
   - Or enable test data in `application.properties`:
     ```properties
     app.test-data.enabled=true
     ```
   - Test users created:
     - Agent: agent@property.com / Agent@123
     - Client: client@property.com / Client@123

## API Documentation

### Authentication

#### Register a new user

**Endpoint:** `POST /api/auth/signup`

**Request Body:**

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "password123",
  "phone": "1234567890",
  "address": "123 Main St, Anytown, USA",
  "role": "CLIENT",
  "enableTwoFactor": false
}
```

**Response:**

```json
{
  "message": "User registered successfully",
  "success": true
}
```

#### Login

**Endpoint:** `POST /api/auth/login`

**Request Body:**

```json
{
  "email": "john.doe@example.com",
  "password": "password123"
}
```

**Response:**

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "roles": ["ROLE_CLIENT"],
  "twoFactorEnabled": false,
  "authenticated": true
}
```

#### Two-Factor Authentication Verification

**Endpoint:** `POST /api/auth/verify-2fa`

**Request Body:**

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "code": "123456"
}
```

**Response:**

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "roles": ["ROLE_CLIENT"],
  "twoFactorEnabled": true,
  "authenticated": true
}
```

#### Forgot Password

**Endpoint:** `POST /api/auth/forgot-password?email=john.doe@example.com`

**Response:**

```json
{
  "message": "Password reset email sent",
  "success": true
}
```

#### Reset Password

**Endpoint:** `POST /api/auth/reset-password`

**Request Body:**

```json
{
  "token": "reset-token-from-email",
  "newPassword": "newPassword123"
}
```

**Response:**

```json
{
  "message": "Password has been reset successfully",
  "success": true
}
```

#### Generate Two-Factor QR Code

**Endpoint:** `GET /api/auth/2fa/generate/{userId}`

**Response:**

```
data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...
```

#### Enable Two-Factor Authentication

**Endpoint:** `POST /api/auth/2fa/enable/{userId}?code=123456`

**Response:**

```json
{
  "message": "Two-factor authentication enabled successfully",
  "success": true
}
```

#### Disable Two-Factor Authentication

**Endpoint:** `POST /api/auth/2fa/disable/{userId}?code=123456`

**Response:**

```json
{
  "message": "Two-factor authentication disabled successfully",
  "success": true
}
```

### Users

#### Get All Users (Admin only)

**Endpoint:** `GET /api/admin/users`

**Authorization:** Bearer Token with ADMIN role

**Response:**

```json
[
  {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "1234567890",
    "address": "123 Main St, Anytown, USA",
    "role": "CLIENT",
    "enabled": true,
    "twoFactorEnabled": false,
    "createdAt": "2025-05-14T10:00:00"
  },
  {
    "id": 2,
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane.smith@example.com",
    "phone": "0987654321",
    "address": "456 Oak Ave, Somewhere, USA",
    "role": "AGENT",
    "enabled": true,
    "twoFactorEnabled": true,
    "createdAt": "2025-05-14T11:00:00"
  }
]
```

#### Get User by ID (Admin only)

**Endpoint:** `GET /api/admin/users/{id}`

**Authorization:** Bearer Token with ADMIN role

**Response:**

```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "1234567890",
  "address": "123 Main St, Anytown, USA",
  "role": "CLIENT",
  "enabled": true,
  "twoFactorEnabled": false,
  "createdAt": "2025-05-14T10:00:00"
}
```

#### Get Users by Role (Admin only)

**Endpoint:** `GET /api/admin/users/role/{role}`

**Authorization:** Bearer Token with ADMIN role

**Response:**

```json
[
  {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "1234567890",
    "address": "123 Main St, Anytown, USA",
    "role": "CLIENT",
    "enabled": true,
    "twoFactorEnabled": false,
    "createdAt": "2025-05-14T10:00:00"
  }
]
```

#### Update User (Admin only)

**Endpoint:** `PUT /api/admin/users/{id}`

**Authorization:** Bearer Token with ADMIN role

**Request Body:**

```json
{
  "firstName": "John",
  "lastName": "Updated",
  "phone": "1234567890",
  "address": "123 Main St, Anytown, USA"
}
```

**Response:**

```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Updated",
  "email": "john.doe@example.com",
  "phone": "1234567890",
  "address": "123 Main St, Anytown, USA",
  "role": "CLIENT",
  "enabled": true,
  "twoFactorEnabled": false,
  "createdAt": "2025-05-14T10:00:00"
}
```

#### Change User Role (Admin only)

**Endpoint:** `PUT /api/admin/users/{id}/role?role=AGENT`

**Authorization:** Bearer Token with ADMIN role

**Response:**

```json
{
  "message": "User role updated to: AGENT",
  "success": true
}
```

#### Delete User (Admin only)

**Endpoint:** `DELETE /api/admin/users/{id}`

**Authorization:** Bearer Token with ADMIN role

**Response:**

```json
{
  "message": "User deleted successfully",
  "success": true
}
```

#### Get User Profile

**Endpoint:** `GET /api/client/profile`

**Authorization:** Bearer Token

**Response:**

```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "1234567890",
  "address": "123 Main St, Anytown, USA",
  "role": "CLIENT",
  "enabled": true,
  "twoFactorEnabled": false,
  "createdAt": "2025-05-14T10:00:00"
}
```

#### Update User Profile

**Endpoint:** `PUT /api/client/profile`

**Authorization:** Bearer Token

**Request Body:**

```json
{
  "firstName": "John",
  "lastName": "Updated",
  "phone": "1234567890",
  "address": "123 Main St, Anytown, USA"
}
```

**Response:**

```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Updated",
  "email": "john.doe@example.com",
  "phone": "1234567890",
  "address": "123 Main St, Anytown, USA",
  "role": "CLIENT",
  "enabled": true,
  "twoFactorEnabled": false,
  "createdAt": "2025-05-14T10:00:00"
}
```

#### Change Password

**Endpoint:** `PUT /api/client/change-password?currentPassword=password123&newPassword=newPassword123`

**Authorization:** Bearer Token

**Response:**

```json
{
  "message": "Password changed successfully",
  "success": true
}
```

### Properties

#### Get All Properties

**Endpoint:** `GET /api/public/properties`

**Response:**

```json
[
  {
    "id": 1,
    "title": "Modern Apartment",
    "description": "A beautiful modern apartment in the city center",
    "location": "Downtown",
    "price": 250000.0,
    "status": "AVAILABLE",
    "score": 4,
    "createdAt": "2025-05-14T10:00:00",
    "agent": {
      "id": 2,
      "firstName": "Jane",
      "lastName": "Smith"
    },
    "category": {
      "id": 1,
      "name": "Apartment"
    },
    "amenities": [
      {
        "id": 1,
        "name": "Swimming Pool"
      },
      {
        "id": 2,
        "name": "Gym"
      }
    ]
  }
]
```

#### Get Property by ID

**Endpoint:** `GET /api/public/properties/{id}`

**Response:**

```json
{
  "id": 1,
  "title": "Modern Apartment",
  "description": "A beautiful modern apartment in the city center",
  "location": "Downtown",
  "price": 250000.0,
  "status": "AVAILABLE",
  "score": 4,
  "createdAt": "2025-05-14T10:00:00",
  "agent": {
    "id": 2,
    "firstName": "Jane",
    "lastName": "Smith"
  },
  "category": {
    "id": 1,
    "name": "Apartment"
  },
  "amenities": [
    {
      "id": 1,
      "name": "Swimming Pool"
    },
    {
      "id": 2,
      "name": "Gym"
    }
  ]
}
```

#### Get Properties by Status

**Endpoint:** `GET /api/public/properties/status/{status}`

**Response:**

```json
[
  {
    "id": 1,
    "title": "Modern Apartment",
    "description": "A beautiful modern apartment in the city center",
    "location": "Downtown",
    "price": 250000.0,
    "status": "AVAILABLE",
    "score": 4,
    "createdAt": "2025-05-14T10:00:00"
  }
]
```

#### Get Properties by Category

**Endpoint:** `GET /api/public/properties/category/{categoryId}`

**Response:**

```json
[
  {
    "id": 1,
    "title": "Modern Apartment",
    "description": "A beautiful modern apartment in the city center",
    "location": "Downtown",
    "price": 250000.0,
    "status": "AVAILABLE",
    "score": 4,
    "createdAt": "2025-05-14T10:00:00"
  }
]
```

#### Get Properties by Location

**Endpoint:** `GET /api/public/properties/location?location=Downtown`

**Response:**

```json
[
  {
    "id": 1,
    "title": "Modern Apartment",
    "description": "A beautiful modern apartment in the city center",
    "location": "Downtown",
    "price": 250000.0,
    "status": "AVAILABLE",
    "score": 4,
    "createdAt": "2025-05-14T10:00:00"
  }
]
```

#### Get Agent Properties

**Endpoint:** `GET /api/agent/properties`

**Authorization:** Bearer Token with AGENT or ADMIN role

**Response:**

```json
[
  {
    "id": 1,
    "title": "Modern Apartment",
    "description": "A beautiful modern apartment in the city center",
    "location": "Downtown",
    "price": 250000.0,
    "status": "AVAILABLE",
    "score": 4,
    "createdAt": "2025-05-14T10:00:00"
  }
]
```

#### Create Property

**Endpoint:** `POST /api/agent/properties?categoryId=1&amenityIds=1,2`

**Authorization:** Bearer Token with AGENT or ADMIN role

**Request Body:**

```json
{
  "title": "Modern Apartment",
  "description": "A beautiful modern apartment in the city center",
  "location": "Downtown",
  "price": 250000.0,
  "status": "AVAILABLE"
}
```

**Response:**

```json
{
  "id": 1,
  "title": "Modern Apartment",
  "description": "A beautiful modern apartment in the city center",
  "location": "Downtown",
  "price": 250000.0,
  "status": "AVAILABLE",
  "createdAt": "2025-05-14T10:00:00",
  "agent": {
    "id": 2,
    "firstName": "Jane",
    "lastName": "Smith"
  },
  "category": {
    "id": 1,
    "name": "Apartment"
  },
  "amenities": [
    {
      "id": 1,
      "name": "Swimming Pool"
    },
    {
      "id": 2,
      "name": "Gym"
    }
  ]
}
```

#### Update Property

**Endpoint:** `PUT /api/agent/properties/{id}?categoryId=1&amenityIds=1,2,3`

**Authorization:** Bearer Token with AGENT or ADMIN role

**Request Body:**

```json
{
  "title": "Updated Modern Apartment",
  "description": "An updated beautiful modern apartment in the city center",
  "location": "Downtown",
  "price": 260000.0,
  "status": "AVAILABLE"
}
```

**Response:**

```json
{
  "id": 1,
  "title": "Updated Modern Apartment",
  "description": "An updated beautiful modern apartment in the city center",
  "location": "Downtown",
  "price": 260000.0,
  "status": "AVAILABLE",
  "createdAt": "2025-05-14T10:00:00",
  "updatedAt": "2025-05-14T11:00:00",
  "agent": {
    "id": 2,
    "firstName": "Jane",
    "lastName": "Smith"
  },
  "category": {
    "id": 1,
    "name": "Apartment"
  },
  "amenities": [
    {
      "id": 1,
      "name": "Swimming Pool"
    },
    {
      "id": 2,
      "name": "Gym"
    },
    {
      "id": 3,
      "name": "Parking"
    }
  ]
}
```

#### Delete Property

**Endpoint:** `DELETE /api/agent/properties/{id}`

**Authorization:** Bearer Token with AGENT or ADMIN role

**Response:**

```json
{
  "message": "Property deleted successfully",
  "success": true
}
```

#### Update Property Status

**Endpoint:** `PATCH /api/agent/properties/{id}/status?status=SOLD`

**Authorization:** Bearer Token with AGENT or ADMIN role

**Response:**

```json
{
  "id": 1,
  "title": "Modern Apartment",
  "description": "A beautiful modern apartment in the city center",
  "location": "Downtown",
  "price": 250000.0,
  "status": "SOLD",
  "updatedAt": "2025-05-14T11:00:00"
}
```

#### Add Amenity to Property

**Endpoint:** `POST /api/agent/properties/{propertyId}/amenities/{amenityId}`

**Authorization:** Bearer Token with AGENT or ADMIN role

**Response:**

```json
{
  "id": 1,
  "title": "Modern Apartment",
  "amenities": [
    {
      "id": 1,
      "name": "Swimming Pool"
    },
    {
      "id": 2,
      "name": "Gym"
    },
    {
      "id": 3,
      "name": "Parking"
    }
  ]
}
```

#### Remove Amenity from Property

**Endpoint:** `DELETE /api/agent/properties/{propertyId}/amenities/{amenityId}`

**Authorization:** Bearer Token with AGENT or ADMIN role

**Response:**

```json
{
  "id": 1,
  "title": "Modern Apartment",
  "amenities": [
    {
      "id": 1,
      "name": "Swimming Pool"
    },
    {
      "id": 2,
      "name": "Gym"
    }
  ]
}
```

### Categories

#### Get All Categories

**Endpoint:** `GET /api/public/categories`

**Response:**

```json
[
  {
    "id": 1,
    "name": "Apartment",
    "description": "Residential units in multi-story buildings"
  },
  {
    "id": 2,
    "name": "House",
    "description": "Standalone residential buildings"
  }
]
```

#### Get Category by ID

**Endpoint:** `GET /api/public/categories/{id}`

**Response:**

```json
{
  "id": 1,
  "name": "Apartment",
  "description": "Residential units in multi-story buildings"
}
```

#### Get Category by Name

**Endpoint:** `GET /api/public/categories/name/{name}`

**Response:**

```json
{
  "id": 1,
  "name": "Apartment",
  "description": "Residential units in multi-story buildings"
}
```

#### Create Category (Admin only)

**Endpoint:** `POST /api/admin/categories`

**Authorization:** Bearer Token with ADMIN role

**Request Body:**

```json
{
  "name": "Commercial",
  "description": "Properties for business purposes"
}
```

**Response:**

```json
{
  "id": 3,
  "name": "Commercial",
  "description": "Properties for business purposes"
}
```

#### Update Category (Admin only)

**Endpoint:** `PUT /api/admin/categories/{id}`

**Authorization:** Bearer Token with ADMIN role

**Request Body:**

```json
{
  "name": "Commercial Property",
  "description": "Properties for business and commercial purposes"
}
```

**Response:**

```json
{
  "id": 3,
  "name": "Commercial Property",
  "description": "Properties for business and commercial purposes"
}
```

#### Delete Category (Admin only)

**Endpoint:** `DELETE /api/admin/categories/{id}`

**Authorization:** Bearer Token with ADMIN role

**Response:**

```json
{
  "message": "Category deleted successfully",
  "success": true
}
```

### Amenities

#### Get All Amenities

**Endpoint:** `GET /api/public/amenities`

**Response:**

```json
[
  {
    "id": 1,
    "name": "Swimming Pool",
    "description": "Outdoor swimming pool"
  },
  {
    "id": 2,
    "name": "Gym",
    "description": "Fitness center with equipment"
  }
]
```

#### Get Amenity by ID

**Endpoint:** `GET /api/public/amenities/{id}`

**Response:**

```json
{
  "id": 1,
  "name": "Swimming Pool",
  "description": "Outdoor swimming pool"
}
```

#### Get Amenity by Name

**Endpoint:** `GET /api/public/amenities/name/{name}`

**Response:**

```json
{
  "id": 1,
  "name": "Swimming Pool",
  "description": "Outdoor swimming pool"
}
```

#### Create Amenity (Admin only)

**Endpoint:** `POST /api/admin/amenities`

**Authorization:** Bearer Token with ADMIN role

**Request Body:**

```json
{
  "name": "Parking",
  "description": "Covered parking space"
}
```

**Response:**

```json
{
  "id": 3,
  "name": "Parking",
  "description": "Covered parking space"
}
```

#### Update Amenity (Admin only)

**Endpoint:** `PUT /api/admin/amenities/{id}`

**Authorization:** Bearer Token with ADMIN role

**Request Body:**

```json
{
  "name": "Parking",
  "description": "Covered and secure parking space"
}
```

**Response:**

```json
{
  "id": 3,
  "name": "Parking",
  "description": "Covered and secure parking space"
}
```

#### Delete Amenity (Admin only)

**Endpoint:** `DELETE /api/admin/amenities/{id}`

**Authorization:** Bearer Token with ADMIN role

**Response:**

```json
{
  "message": "Amenity deleted successfully",
  "success": true
}
```

### Images

#### Get Image by ID

**Endpoint:** `GET /api/public/images/{id}`

**Response:**

```json
{
  "id": 1,
  "url": "uploads/images/abc123.jpg",
  "description": "Front view of the apartment"
}
```

#### Get Images by Property ID

**Endpoint:** `GET /api/public/properties/{propertyId}/images`

**Response:**

```json
[
  {
    "id": 1,
    "url": "uploads/images/abc123.jpg",
    "description": "Front view of the apartment"
  },
  {
    "id": 2,
    "url": "uploads/images/def456.jpg",
    "description": "Living room"
  }
]
```

#### Upload Image

**Endpoint:** `POST /api/agent/properties/{propertyId}/images`

**Authorization:** Bearer Token with AGENT or ADMIN role

**Form Data:**

- file: [image file]
- description: "Front view of the apartment"

**Response:**

```json
{
  "id": 1,
  "url": "uploads/images/abc123.jpg",
  "description": "Front view of the apartment"
}
```

#### Update Image Description

**Endpoint:** `PUT /api/agent/images/{id}?description=Updated front view of the apartment`

**Authorization:** Bearer Token with AGENT or ADMIN role

**Response:**

```json
{
  "id": 1,
  "url": "uploads/images/abc123.jpg",
  "description": "Updated front view of the apartment"
}
```

#### Delete Image

**Endpoint:** `DELETE /api/agent/images/{id}`

**Authorization:** Bearer Token with AGENT or ADMIN role

**Response:**

```json
{
  "message": "Image deleted successfully",
  "success": true
}
```

### Ratings

#### Get Rating by ID

**Endpoint:** `GET /api/public/ratings/{id}`

**Response:**

```json
{
  "id": 1,
  "score": 4,
  "comment": "Great property with an amazing view",
  "user": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe"
  },
  "property": {
    "id": 1,
    "title": "Modern Apartment"
  }
}
```

#### Get Ratings by Property ID

**Endpoint:** `GET /api/public/properties/{propertyId}/ratings`

**Response:**

```json
[
  {
    "id": 1,
    "score": 4,
    "comment": "Great property with an amazing view",
    "user": {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe"
    }
  },
  {
    "id": 2,
    "score": 5,
    "comment": "Excellent location and amenities",
    "user": {
      "id": 3,
      "firstName": "Alice",
      "lastName": "Johnson"
    }
  }
]
```

#### Get Ratings by User ID (Admin only)

**Endpoint:** `GET /api/admin/users/{userId}/ratings`

**Authorization:** Bearer Token with ADMIN role

**Response:**

```json
[
  {
    "id": 1,
    "score": 4,
    "comment": "Great property with an amazing view",
    "property": {
      "id": 1,
      "title": "Modern Apartment"
    }
  }
]
```

#### Get User's Own Ratings

**Endpoint:** `GET /api/client/my-ratings`

**Authorization:** Bearer Token with CLIENT or ADMIN role

**Response:**

```json
[
  {
    "id": 1,
    "score": 4,
    "comment": "Great property with an amazing view",
    "property": {
      "id": 1,
      "title": "Modern Apartment"
    }
  }
]
```

#### Create Rating

**Endpoint:** `POST /api/client/properties/{propertyId}/ratings`

**Authorization:** Bearer Token with CLIENT or ADMIN role

**Request Body:**

```json
{
  "score": 4,
  "comment": "Great property with an amazing view"
}
```

**Response:**

```json
{
  "id": 1,
  "score": 4,
  "comment": "Great property with an amazing view",
  "user": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe"
  },
  "property": {
    "id": 1,
    "title": "Modern Apartment"
  }
}
```

#### Update Rating

**Endpoint:** `PUT /api/client/ratings/{id}`

**Authorization:** Bearer Token with CLIENT or ADMIN role

**Request Body:**

```json
{
  "score": 5,
  "comment": "Updated: Great property with an amazing view and excellent service"
}
```

**Response:**

```json
{
  "id": 1,
  "score": 5,
  "comment": "Updated: Great property with an amazing view and excellent service",
  "user": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe"
  },
  "property": {
    "id": 1,
    "title": "Modern Apartment"
  }
}
```

#### Delete Rating

**Endpoint:** `DELETE /api/client/ratings/{id}`

**Authorization:** Bearer Token with CLIENT or ADMIN role

**Response:**

```json
{
  "message": "Rating deleted successfully",
  "success": true
}
```

#### Delete Rating (Admin only)

**Endpoint:** `DELETE /api/admin/ratings/{id}`

**Authorization:** Bearer Token with ADMIN role

**Response:**

```json
{
  "message": "Rating deleted successfully",
  "success": true
}
```

### Transactions

#### Get All Transactions (Admin only)

**Endpoint:** `GET /api/admin/transactions`

**Authorization:** Bearer Token with ADMIN role

**Response:**

```json
[
  {
    "id": 1,
    "amount": 250000.0,
    "commission": 7500.0,
    "date": "2025-05-14T10:00:00",
    "status": "COMPLETED",
    "transactionType": "SALE",
    "property": {
      "id": 1,
      "title": "Modern Apartment"
    },
    "agent": {
      "id": 2,
      "firstName": "Jane",
      "lastName": "Smith"
    },
    "client": {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe"
    }
  }
]
```

#### Get Transaction by ID (Admin only)

**Endpoint:** `GET /api/admin/transactions/{id}`

**Authorization:** Bearer Token with ADMIN role

**Response:**

```json
{
  "id": 1,
  "amount": 250000.0,
  "commission": 7500.0,
  "date": "2025-05-14T10:00:00",
  "status": "COMPLETED",
  "transactionType": "SALE",
  "property": {
    "id": 1,
    "title": "Modern Apartment"
  },
  "agent": {
    "id": 2,
    "firstName": "Jane",
    "lastName": "Smith"
  },
  "client": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe"
  }
}
```

#### Get Transactions by Property ID (Admin only)

**Endpoint:** `GET /api/admin/properties/{propertyId}/transactions`

**Authorization:** Bearer Token with ADMIN role

**Response:**

```json
[
  {
    "id": 1,
    "amount": 250000.0,
    "commission": 7500.0,
    "date": "2025-05-14T10:00:00",
    "status": "COMPLETED",
    "transactionType": "SALE"
  }
]
```

#### Get Agent's Transactions

**Endpoint:** `GET /api/agent/my-transactions`

**Authorization:** Bearer Token with AGENT or ADMIN role

**Response:**

```json
[
  {
    "id": 1,
    "amount": 250000.0,
    "commission": 7500.0,
    "date": "2025-05-14T10:00:00",
    "status": "COMPLETED",
    "transactionType": "SALE",
    "property": {
      "id": 1,
      "title": "Modern Apartment"
    },
    "client": {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe"
    }
  }
]
```

#### Get Client's Transactions

**Endpoint:** `GET /api/client/my-transactions`

**Authorization:** Bearer Token with CLIENT or ADMIN role

**Response:**

```json
[
  {
    "id": 1,
    "amount": 250000.0,
    "date": "2025-05-14T10:00:00",
    "status": "COMPLETED",
    "transactionType": "SALE",
    "property": {
      "id": 1,
      "title": "Modern Apartment"
    },
    "agent": {
      "id": 2,
      "firstName": "Jane",
      "lastName": "Smith"
    }
  }
]
```

#### Create Transaction

**Endpoint:** `POST /api/agent/transactions?propertyId=1&clientId=1`

**Authorization:** Bearer Token with AGENT or ADMIN role

**Request Body:**

```json
{
  "amount": 250000.0,
  "date": "2025-05-14T10:00:00",
  "status": "PENDING",
  "transactionType": "SALE"
}
```

**Response:**

```json
{
  "id": 1,
  "amount": 250000.0,
  "commission": 7500.0,
  "date": "2025-05-14T10:00:00",
  "status": "PENDING",
  "transactionType": "SALE",
  "property": {
    "id": 1,
    "title": "Modern Apartment"
  },
  "agent": {
    "id": 2,
    "firstName": "Jane",
    "lastName": "Smith"
  },
  "client": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe"
  }
}
```

#### Update Transaction

**Endpoint:** `PUT /api/agent/transactions/{id}`

**Authorization:** Bearer Token with AGENT or ADMIN role

**Request Body:**

```json
{
  "amount": 245000.0,
  "commission": 7350.0,
  "date": "2025-05-14T10:00:00",
  "status": "COMPLETED",
  "transactionType": "SALE"
}
```

**Response:**

```json
{
  "id": 1,
  "amount": 245000.0,
  "commission": 7350.0,
  "date": "2025-05-14T10:00:00",
  "status": "COMPLETED",
  "transactionType": "SALE",
  "property": {
    "id": 1,
    "title": "Modern Apartment"
  },
  "agent": {
    "id": 2,
    "firstName": "Jane",
    "lastName": "Smith"
  },
  "client": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe"
  }
}
```

#### Update Transaction Status

**Endpoint:** `PATCH /api/agent/transactions/{id}/status?status=COMPLETED`

**Authorization:** Bearer Token with AGENT or ADMIN role

**Response:**

```json
{
  "id": 1,
  "amount": 250000.0,
  "commission": 7500.0,
  "date": "2025-05-14T10:00:00",
  "status": "COMPLETED",
  "transactionType": "SALE",
  "property": {
    "id": 1,
    "title": "Modern Apartment"
  }
}
```

#### Delete Transaction (Admin only)

**Endpoint:** `DELETE /api/admin/transactions/{id}`

**Authorization:** Bearer Token with ADMIN role

**Response:**

```json
{
  "message": "Transaction deleted successfully",
  "success": true
}
```
