# Security Configuration Notes

## JWT Secret Key

### Development/Testing Environment
For development and testing, the JWT secret is hardcoded in `application.properties`. This is acceptable for local development and testing purposes.

### Production Environment
**IMPORTANT**: Before deploying to production, you MUST configure the JWT secret using environment variables.

#### How to Configure for Production:

1. **Update application.properties**:
   ```properties
   jwt.secret=${JWT_SECRET:changeme-default-secret-for-fallback}
   jwt.expiration=${JWT_EXPIRATION:86400000}
   ```

2. **Set Environment Variables**:
   
   On Linux/Mac:
   ```bash
   export JWT_SECRET="your-super-secret-key-at-least-256-bits"
   export JWT_EXPIRATION="86400000"
   ```
   
   On Windows:
   ```cmd
   set JWT_SECRET=your-super-secret-key-at-least-256-bits
   set JWT_EXPIRATION=86400000
   ```
   
   In Docker:
   ```yaml
   environment:
     - JWT_SECRET=your-super-secret-key-at-least-256-bits
     - JWT_EXPIRATION=86400000
   ```
   
   In Kubernetes:
   ```yaml
   env:
     - name: JWT_SECRET
       valueFrom:
         secretKeyRef:
           name: jwt-secret
           key: secret
   ```

3. **Generate a Secure Secret**:
   ```bash
   # Generate a random 256-bit secret (recommended)
   openssl rand -base64 32
   ```

## Security Best Practices

1. **Never commit secrets to version control**
2. **Use different secrets for different environments** (dev, staging, production)
3. **Rotate secrets periodically** (at least every 90 days)
4. **Use environment-specific secrets** stored in secure secret management systems (AWS Secrets Manager, Azure Key Vault, HashiCorp Vault, etc.)
5. **Always use HTTPS in production** to protect tokens in transit
6. **Monitor for suspicious authentication patterns**

## Token Expiration

The default token expiration is set to 24 hours (86400000 milliseconds). You can adjust this based on your security requirements:

- **Higher security applications**: Use shorter expiration times (e.g., 1-4 hours)
- **User convenience**: Use longer expiration times (e.g., 24-72 hours)
- **Consider implementing refresh tokens** for better UX with high security

## Role-Based Access Control

The authentication filter now properly assigns user roles as Spring Security authorities with the `ROLE_` prefix. This enables proper role-based access control throughout the application.

Current roles:
- `ROLE_USER` - Regular users
- `ROLE_ADMIN` - Administrators

You can use these in your controllers with Spring Security annotations:
```java
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> adminOnlyEndpoint() {
    // Admin-only logic
}
```

## CSRF Protection

CSRF (Cross-Site Request Forgery) protection is **intentionally disabled** in this application because:

1. **Stateless JWT Authentication**: This API uses JWT tokens in the Authorization header, not cookies
2. **No CSRF Risk**: CSRF attacks rely on browsers automatically sending credentials (cookies). JWT tokens in headers must be explicitly added by JavaScript code, which cannot be done from another domain due to CORS
3. **Stateless Sessions**: The application uses `SessionCreationPolicy.STATELESS`, meaning no server-side sessions are maintained

This is a standard practice for RESTful APIs using token-based authentication. If you were using cookie-based authentication, CSRF protection should be enabled.
