# Bearer Token Authentication Implementation - Frontend Integration Guide

## Summary of Backend Changes

The backend has been updated to require Bearer token authentication for the following routes:
- `/problems/**` - All problem-related endpoints
- `/problem-collections/**` - All problem collection endpoints  
- `/algorithms/**` - All algorithm endpoints

## How Authentication Works

1. **Login Endpoint** (`POST /auth/login`):
   - Remains publicly accessible (no authentication required)
   - Returns a JWT token in the response

2. **Protected Endpoints**:
   - Require an `Authorization` header with format: `Bearer <token>`
   - Without a valid token, requests will receive a 401 Unauthorized response

## Frontend Integration Prompt for AI

Use this prompt when working with the AI to modify your frontend code:

---

**PROMPT FOR FRONTEND AI:**

I need you to update the frontend application to work with the new Bearer token authentication system on the backend. Here are the requirements:

### 1. Login Flow Changes

When calling `POST /auth/login`, the response now includes a `token` field in addition to the existing user information:

```json
{
  "id": "user-uuid",
  "username": "username",
  "email": "email@example.com",
  "role": "USER",
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Action Required:**
- Store the `token` from the login response (e.g., in localStorage, sessionStorage, or your state management solution)
- Keep this token for subsequent API requests

### 2. Protected API Requests

All requests to the following endpoints now require authentication:
- `/problems/**`
- `/problem-collections/**`
- `/algorithms/**`

**Action Required:**
- Add an `Authorization` header to all requests to these endpoints
- Format: `Authorization: Bearer <token>`

Example using fetch:
```javascript
const token = localStorage.getItem('authToken'); // or however you're storing it

fetch('/api/problem-collections', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
})
```

Example using axios:
```javascript
const token = localStorage.getItem('authToken');

axios.get('/api/problem-collections', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
})

// Or configure axios globally:
axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
```

### 3. Token Management

**Action Required:**
- Store the token after successful login
- Include the token in all requests to protected endpoints
- Clear the token on logout
- Handle 401 Unauthorized responses (token expired or invalid) by redirecting to login

### 4. Implementation Checklist

Please update the following in the frontend:

1. **Login Component/Service:**
   - [ ] Extract the `token` field from login response
   - [ ] Store the token (localStorage/sessionStorage/state)

2. **API Client/HTTP Service:**
   - [ ] Add Bearer token to Authorization header for protected endpoints
   - [ ] Create an interceptor or middleware to automatically add the token

3. **Logout Functionality:**
   - [ ] Clear the stored token on logout

4. **Error Handling:**
   - [ ] Handle 401 responses by redirecting to login page
   - [ ] Clear token on 401 errors

5. **Routes to Update:**
   - [ ] All calls to `/problems/**`
   - [ ] All calls to `/problem-collections/**`
   - [ ] All calls to `/algorithms/**`

### 5. Example Implementation

Here's a complete example of how to implement this:

```javascript
// auth.service.js or similar
class AuthService {
  async login(email, password) {
    const response = await fetch('/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });
    
    const data = await response.json();
    
    if (response.ok) {
      // Store the token
      localStorage.setItem('authToken', data.token);
      localStorage.setItem('user', JSON.stringify(data));
      return data;
    }
    throw new Error('Login failed');
  }
  
  logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
  }
  
  getToken() {
    return localStorage.getItem('authToken');
  }
  
  isAuthenticated() {
    return !!this.getToken();
  }
}

// api.service.js or similar
class ApiService {
  constructor(authService) {
    this.authService = authService;
  }
  
  async request(url, options = {}) {
    const token = this.authService.getToken();
    
    const headers = {
      'Content-Type': 'application/json',
      ...options.headers
    };
    
    // Add Authorization header if token exists
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    
    const response = await fetch(url, {
      ...options,
      headers
    });
    
    // Handle 401 - redirect to login
    if (response.status === 401) {
      this.authService.logout();
      window.location.href = '/login';
      throw new Error('Unauthorized');
    }
    
    return response;
  }
  
  // Convenience methods
  async get(url) {
    return this.request(url, { method: 'GET' });
  }
  
  async post(url, data) {
    return this.request(url, {
      method: 'POST',
      body: JSON.stringify(data)
    });
  }
  
  async put(url, data) {
    return this.request(url, {
      method: 'PUT',
      body: JSON.stringify(data)
    });
  }
  
  async delete(url) {
    return this.request(url, { method: 'DELETE' });
  }
}
```

### 6. Security Notes

- The JWT token expires after 24 hours (86400000 milliseconds)
- The token contains the user's ID and email
- Always use HTTPS in production to protect the token in transit
- Never expose the token in URLs or logs

---

Please implement these changes across all relevant components and API calls in the frontend application. Make sure to test the login flow and verify that all protected endpoints work correctly with the Bearer token.
