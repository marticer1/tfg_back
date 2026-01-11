# Implementación de Autenticación con Bearer Token - Guía de Integración Frontend

## Resumen de los Cambios en el Backend

El backend ha sido actualizado para requerir autenticación con Bearer token en las siguientes rutas:
- `/problems/**` - Todos los endpoints relacionados con problemas
- `/problem-collections/**` - Todos los endpoints de colecciones de problemas
- `/algorithms/**` - Todos los endpoints de algoritmos

## Cómo Funciona la Autenticación

1. **Endpoint de Login** (`POST /auth/login`):
   - Permanece públicamente accesible (no requiere autenticación)
   - Devuelve un token JWT en la respuesta

2. **Endpoints Protegidos**:
   - Requieren un header `Authorization` con el formato: `Bearer <token>`
   - Sin un token válido, las peticiones recibirán una respuesta 401 Unauthorized

## Prompt para la IA del Frontend

Usa este prompt cuando trabajes con la IA para modificar tu código frontend:

---

**PROMPT PARA LA IA DEL FRONTEND:**

Necesito que actualices la aplicación frontend para trabajar con el nuevo sistema de autenticación Bearer token en el backend. Aquí están los requisitos:

### 1. Cambios en el Flujo de Login

Al llamar a `POST /auth/login`, la respuesta ahora incluye un campo `token` además de la información existente del usuario:

```json
{
  "id": "user-uuid",
  "username": "nombre_usuario",
  "email": "email@example.com",
  "role": "USER",
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Acción Requerida:**
- Almacenar el `token` de la respuesta del login (ej: en localStorage, sessionStorage, o tu solución de gestión de estado)
- Mantener este token para peticiones API subsecuentes

### 2. Peticiones API Protegidas

Todas las peticiones a los siguientes endpoints ahora requieren autenticación:
- `/problems/**`
- `/problem-collections/**`
- `/algorithms/**`

**Acción Requerida:**
- Añadir un header `Authorization` a todas las peticiones a estos endpoints
- Formato: `Authorization: Bearer <token>`

Ejemplo usando fetch:
```javascript
const token = localStorage.getItem('authToken'); // o como lo estés almacenando

fetch('/api/problem-collections', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
})
```

Ejemplo usando axios:
```javascript
const token = localStorage.getItem('authToken');

axios.get('/api/problem-collections', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
})

// O configurar axios globalmente:
axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
```

### 3. Gestión del Token

**Acción Requerida:**
- Almacenar el token después del login exitoso
- Incluir el token en todas las peticiones a endpoints protegidos
- Limpiar el token al hacer logout
- Manejar respuestas 401 Unauthorized (token expirado o inválido) redirigiendo al login

### 4. Checklist de Implementación

Por favor actualiza lo siguiente en el frontend:

1. **Componente/Servicio de Login:**
   - [ ] Extraer el campo `token` de la respuesta del login
   - [ ] Almacenar el token (localStorage/sessionStorage/state)

2. **Cliente API/Servicio HTTP:**
   - [ ] Añadir Bearer token al header Authorization para endpoints protegidos
   - [ ] Crear un interceptor o middleware para añadir automáticamente el token

3. **Funcionalidad de Logout:**
   - [ ] Limpiar el token almacenado al hacer logout

4. **Manejo de Errores:**
   - [ ] Manejar respuestas 401 redirigiendo a la página de login
   - [ ] Limpiar el token en errores 401

5. **Rutas a Actualizar:**
   - [ ] Todas las llamadas a `/problems/**`
   - [ ] Todas las llamadas a `/problem-collections/**`
   - [ ] Todas las llamadas a `/algorithms/**`

### 5. Ejemplo de Implementación

Aquí hay un ejemplo completo de cómo implementar esto:

```javascript
// auth.service.js o similar
class AuthService {
  async login(email, password) {
    const response = await fetch('/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });
    
    const data = await response.json();
    
    if (response.ok) {
      // Almacenar el token
      localStorage.setItem('authToken', data.token);
      localStorage.setItem('user', JSON.stringify(data));
      return data;
    }
    throw new Error('Login falló');
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

// api.service.js o similar
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
    
    // Añadir header Authorization si existe el token
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    
    const response = await fetch(url, {
      ...options,
      headers
    });
    
    // Manejar 401 - redirigir al login
    if (response.status === 401) {
      this.authService.logout();
      window.location.href = '/login';
      throw new Error('No autorizado');
    }
    
    return response;
  }
  
  // Métodos de conveniencia
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

### 6. Notas de Seguridad

- El token JWT expira después de 24 horas (86400000 milisegundos)
- El token contiene el ID y email del usuario
- Siempre usa HTTPS en producción para proteger el token en tránsito
- Nunca expongas el token en URLs o logs

---

Por favor implementa estos cambios en todos los componentes relevantes y llamadas API en la aplicación frontend. Asegúrate de probar el flujo de login y verificar que todos los endpoints protegidos funcionen correctamente con el Bearer token.
