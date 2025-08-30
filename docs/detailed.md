# TMWebRock Framework - Detailed Documentation

***

## Overview

**TMWebRock** is a lightweight, annotation-driven Java web framework designed for building modular, scalable web applications on a servlet container such as Apache Tomcat. Inspired by Spring Boot, it leverages Java annotations, reflection, and servlet APIs to provide a simple yet powerful structure for creating RESTful web services with ease.

This project is primarily created as a learning tool and demonstration platform to showcase core backend concepts such as annotation processing, dependency injection, request routing, parameter binding, security, and scope management without the complexity of mature enterprise frameworks. TMWebRock is a practical example for interviews and educational purposes.

***

## Features

- **Annotation-Driven Service Definition:** Services and endpoints are declared using custom annotations on Java classes and methods.
- **Automatic Service Scanning and Registration:** At startup, TMWebRock scans user-specified packages to discover services dynamically.
- **RESTful HTTP Method Support:** Supports GET and POST REST endpoints with explicit handling of HTTP methods.
- **Dependency Injection:** Supports injecting dependencies into services using the `@Autowired` annotation and injecting lifecycle scoped objects.
- **Request Parameter Injection:** Bind HTTP query parameters or request body JSON to method parameters or fields.
- **Request Forwarding:** Chain service calls internally or forward to JSP/resources using the `@Forward` annotation.
- **Security Guards:** Declarative security guards via `@SecuredAccess` using guard classes/methods for pre-invocation validation.
- **Lifecycle Scope Injection:** Inject HTTP session, request, application scopes, and application directory reference for state sharing.
- **Startup Lifecycle Methods:** Define methods to run at framework startup with priority using `@OnStartUp`.
- **Comprehensive Error Handling:** Uses HTTP status codes and exception handling for invalid requests and security violations.
- **JSON Serialization:** Uses Gson to automatically serialize and deserialize JSON request and response bodies.

***

## Architecture & Core Components

### 1. Deployment Descriptor (`web.xml`)

- Configures servlets:
    - `TMWebRockStarter` servlet initializes and scans user packages at server startup.
    - `TMWebRock` servlet acts as the main controller handling HTTP requests at `/services/*`.
- Specifies context parameter `SERVICE_PACKAGE_PREFIX` that tells the framework which package(s) to scan for services.

### 2. TMWebRockStarter

- Runs at server startup to scan the user package configured in `web.xml`.
- Recursively loads `.class` files in the package and examines annotations to build an internal model of available services.
- Registers services metadata, their HTTP methods, injected dependencies, security guards, request parameters, and startup methods.
- Runs methods annotated with `@OnStartUp` as framework lifecycle hooks.

### 3. TMWebRock (Controller Servlet)

- Receives HTTP GET and POST requests under `/services/*`.
- Routes requests to the appropriate service methods based on the URL path.
- Performs security guard checks prior to service method execution.
- Handles dependency injection (`@Autowired`) and injects scoped objects (`SessionScope`, `RequestScope`, etc.).
- Supports parameter binding from query parameters or JSON bodies.
- Manages forwarding requests internally or to JSP pages/resources.
- Serializes responses to JSON for browser consumption.

### 4. Annotations

| Annotation                      | Target                       | Purpose                                             |
|--------------------------------|------------------------------|-----------------------------------------------------|
| `@Path`                        | Class, Method                 | Maps class/method to URL path segment               |
| `@GET` / `@POST`               | Class, Method                 | Denotes allowed HTTP method for service             |
| `@Autowired`                   | Field                        | Marks dependency to inject by name                   |
| `@InjectSessionScope`          | Class                        | Inject session scope object                           |
| `@InjectRequestScope`          | Class                        | Inject request scope object                           |
| `@InjectApplicationScope`      | Class                        | Inject application scope object                       |
| `@InjectApplicationDirectory`  | Class                        | Inject application directory reference                |
| `@InjectRequestParameter`      | Field                        | Inject HTTP request parameter by name into field     |
| `@RequestParameter`            | Method Parameter             | Map HTTP request parameter to method parameter       |
| `@Forward`                    | Method                       | Forward to another service or resource                |
| `@SecuredAccess`              | Class, Method                | Security guard configuration specifying check class and guard method |
| `@OnStartUp`                  | Method                       | Method to run during startup with priority            |

### 5. Scopes

Framework provides injected scoped objects to manage common webapp state:

| Scope            | Description                              |
|------------------|------------------------------------------|
| `SessionScope`   | Wraps `HttpSession` for per-user session data  |
| `RequestScope`   | Wraps `HttpServletRequest` attributes        |
| `ApplicationScope`| Wraps `ServletContext` for app-wide storage  |
| `ApplicationDirectory` | Provides server’s root deployment directory |

### 6. POJO Metadata Classes

- `Service`: Encapsulates service class, method, URLs, injection info, security guards, etc.
- `AutowiredInfo`, `RequestParameterInfo`, `RequestParameterFieldInfo`, and `SecuredAccessInfo` represent metadata about injection points and security.

***

## How to Use TMWebRock

### Setup and Deployment

1. **Environment:**  
   Java 8+ SDK, servlet container (e.g., Apache Tomcat).

2. **Project Structure:**
    - Place user service classes under package configured in `web.xml` as `SERVICE_PACKAGE_PREFIX` (e.g., `booby`).
    - Compile and build a WAR file with framework and user classes inside `WEB-INF/classes`.
    - Deploy WAR in servlet container.

3. **Start Server:**   
   Framework initializes scanning and registers services during startup.

***

### Writing Services

- Annotate user classes with `@Path` for base path.
- Use `@GET` and/or `@POST` on class or methods.
- Define methods handling endpoints with `@Path` and typed parameters.
- Use `@RequestParameter` on method parameters to bind HTTP request params.
- Inject scoped objects and dependencies with `@InjectSessionScope`, `@Autowired`, etc.
- Use `@Forward` to chain or redirect requests.
- Secure services by annotating with `@SecuredAccess`.

***

### Example Service

```java
@Path("/calculator")
@GET
@InjectSessionScope
@SecuredAccess(checkpost="booby.test.SecureAccessTest", guard="performGuard")
public class Calculator {

    @Autowired(name="std")
    private Student student;

    @Path("/add")
    @Forward("/calculator/result")
    public int add(@RequestParameter("a") int a, @RequestParameter("b") int b) {
        return a + b;
    }

    @Path("/result")
    public void showResult(@RequestParameter("result") int result) {
        System.out.println("Result: " + result);
    }
}
```

***

### Accessing Services

- Services exposed via `http://<host>:<port>/<context>/services/<basepath>/<methodpath>`
- E.g., `http://localhost:8080/app/services/calculator/add?a=10&b=20`
- Responses returned as JSON by default.

***

## Sample Frontend Integration (JavaScript)

- Use jQuery AJAX to send GET/POST requests.
- Serialize objects into JSON for POST methods.
- Example to add student via POST:

```js
var studentService = new StudentService();
var student = new Student(101, "Daniyal Ali");
studentService.add(student).then(function() {
    alert("Added successfully");
});
```

***

## Limitations and Future Enhancements

- Currently, scanning expects exploded class files (no JAR classpath scanning).
- Minimal error reporting and logging.
- Basic security guard support (extendable).
- Adding support for more HTTP methods (PUT, DELETE) recommended.
- Improved exception handling and validation.
- Integration with modern build tools (Maven/Gradle) for easier packaging.

***

## Conclusion

TMWebRock is an educational, flexible Java web framework providing annotation-driven REST service creation, dependency injection, and lifecycle management. It is ideal for learning servlet internals, reflection, and Java web app design, and makes a great showcase for interviews.

***