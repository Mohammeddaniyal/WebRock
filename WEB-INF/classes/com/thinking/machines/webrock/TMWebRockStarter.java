package com.thinking.machines.webrock;

import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import com.thinking.machines.webrock.pojo.*;
import com.thinking.machines.webrock.scopes.ApplicationScope;
import com.thinking.machines.webrock.scopes.RequestScope;
import com.thinking.machines.webrock.scopes.SessionScope;
import com.thinking.machines.webrock.model.*;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.annotations.Autowired;

import java.util.logging.*;

public class TMWebRockStarter extends HttpServlet {
    private ArrayList<Service> runOnStartServicesList = new ArrayList<>();

    public void init() throws ServletException {
        super.init();
        System.out.println("hi");
        WebRockModel webRockModel = new WebRockModel();
        getServletContext().setAttribute("webRockModel", webRockModel);

        String rootFolderName = getServletContext().getInitParameter("SERVICE_PACKAGE_PREFIX");
        if (rootFolderName == null) {
            throw new ServletException("No root folder specified");
        }
        String absolutePath = getServletContext().getRealPath("/WEB-INF/classes/" + rootFolderName);
        System.out.println("Absolute Path : " + absolutePath);
        System.out.println("Path : " + getServletContext().getRealPath("/"));

        if (absolutePath == null) {
            throw new ServletException("Root folder not found in web app");
        }

        File rootFolder = new File(absolutePath);
        if (!rootFolder.exists() || !rootFolder.isDirectory()) {
            throw new ServletException("Specified root is not a valid directory: " + absolutePath);
        }
        System.out.println("calling LoadServices");
        loadServices(rootFolder);
        System.out.println("calling runStartServices");
        runStartServices();
    }

    private void loadServices(File folder) throws ServletException {
        if (folder == null || !folder.exists())
            return;

        File files[] = folder.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                loadServices(file);
            } else {
                if (!file.getName().endsWith(".class"))
                    continue;
                System.out.println("Found file: " + file.getAbsolutePath());

                Class serviceClass = getServiceClass(file);
                if (serviceClass == null)
                    continue;
                WebRockModel webRockModel = (WebRockModel) getServletContext().getAttribute("webRockModel");

                Path path = (Path) serviceClass.getAnnotation(Path.class);
                if (path == null)
                    continue;

                SecuredAccess classSecuredAccess = (SecuredAccess) serviceClass.getAnnotation(SecuredAccess.class);
                SecuredAccessInfo classLevelSecuredAccessInfo = null;
                if (classSecuredAccess != null) {
                    classLevelSecuredAccessInfo = handleSecuredAccessInfo(classSecuredAccess);
                    if (classLevelSecuredAccessInfo == null) {
                        // raise exception
                        return;
                    }
                }

                boolean isGetAllowedOnClass = serviceClass.isAnnotationPresent(GET.class);

                boolean isPostAllowedOnClass = serviceClass.isAnnotationPresent(POST.class);

                boolean injectSessionScope = serviceClass.isAnnotationPresent(InjectSessionScope.class);

                boolean injectApplicationScope = serviceClass.isAnnotationPresent(InjectApplicationScope.class);

                boolean injectRequestScope = serviceClass.isAnnotationPresent(InjectRequestScope.class);

                boolean injectApplicationDirectory = serviceClass.isAnnotationPresent(InjectApplicationDirectory.class);

                // before creating service object and putting them into hashamp
                // create List of autowired properties
                Field[] fields = serviceClass.getDeclaredFields();
                ArrayList<AutowiredInfo> autowiredList = new ArrayList<>();
                ArrayList<RequestParameterFieldInfo> requestParameterFieldInfoList = new ArrayList<>();
                RequestParameterFieldInfo requestParameterFieldInfo;
                for (Field field : fields) {
                    Autowired autowired = field.getAnnotation(Autowired.class);
                    if (autowired != null) {
                        System.out.println("Field with autowired : " + field.getName());
                        System.out.println("Autowired value : " + autowired.name());
                        String beanName = autowired.name();
                        Class<?> fieldTypeClass = field.getType();
                        String fieldName = fieldTypeClass.getSimpleName();
                        String setterMethodName = "set" + fieldName.substring(0, 1).toUpperCase()
                                + fieldName.substring(1);
                        System.out.println("Setter method name : " + setterMethodName);
                        Method setterMethod = null;
                        try {
                            setterMethod = serviceClass.getMethod(setterMethodName, fieldTypeClass);
                            System.out.println("Got setter method : " + setterMethod.getName());
                        } catch (NoSuchMethodException noSuchMethodException) {
                            System.out.println("Method not found(setterMethod)");
                            setterMethod = null;
                        }
                        // now create the objec of AutowiredInfo
                        AutowiredInfo autowiredInfo = new AutowiredInfo(beanName, field, setterMethod);
                        autowiredList.add(autowiredInfo);
                    }
                    InjectRequestParameter injectRequestParameter = field.getAnnotation(InjectRequestParameter.class);
                    if (injectRequestParameter != null) {
                        String name = injectRequestParameter.value();
                        if (name.trim().length() == 0) {
                            continue;
                        }
                        String fieldName = field.getName();
                        String setterMethodName = "set" + fieldName.substring(0, 1).toUpperCase()
                                + fieldName.substring(1);
                        System.out.println("INJECT REQUEST PARAMETER, setter method name " + setterMethodName);
                        Method setterMethod = null;
                        // find the setterMethod using traversing the array
                        Method[] methods = serviceClass.getDeclaredMethods();
                        for (Method m : methods) {
                            if (m.getName().equals(setterMethodName)) {
                                setterMethod = m;
                                int length = setterMethod.getParameters().length;
                                if (length == 0 || length > 1) {
                                    // raise exception
                                }
                                break;
                            }
                        }
                        requestParameterFieldInfo = new RequestParameterFieldInfo(field.getType(), setterMethod, name);
                        requestParameterFieldInfoList.add(requestParameterFieldInfo);
                    }

                }

                Method[] methods = serviceClass.getDeclaredMethods();
                serviceClass.getDeclaredMethods();
                SecuredAccessInfo securedAccessInfo=null;
                for (Method method : methods) {
                    System.out.println("Comes : " + method.getName());
                    boolean pathPresent = method.isAnnotationPresent(Path.class);
                    boolean onStartUpPresent = method.isAnnotationPresent(OnStartUp.class);
                    if (!pathPresent && !onStartUpPresent)
                        continue;
                    Path p = null;
                    if (pathPresent)
                        p = method.getAnnotation(Path.class);
                    OnStartUp onStartUp = null;
                    if (onStartUpPresent)
                        onStartUp = method.getAnnotation(OnStartUp.class);
                    SecuredAccess methodSecuredAccess = method.getAnnotation(SecuredAccess.class);
                    // if OnStartUp applied and as well as SecuredAccess
                    // don't allow becuase secured access if for http request services
                    // but onstartup is when framework loads at the time of initialization
                    if (securedAccessInfo != null && onStartUpPresent) {
                        // raise exception
                        System.out.println("Raised exception !!!!");
                        return;
                    }

                    // if not found then use the class one
                    if (methodSecuredAccess == null) {
                        securedAccessInfo = classLevelSecuredAccessInfo;
                    } else {
                        securedAccessInfo = handleSecuredAccessInfo(methodSecuredAccess);
                        if (securedAccessInfo == null) {
                            // raise exception
                            System.out.println("Bye byte");
                            return;
                        }
                    }
                    System.out.println("OnStartup is " + method.isAnnotationPresent(OnStartUp.class));
                    System.out.println("Arrived : " + method.getName());
                    boolean runOnStart = (onStartUp != null);
                    boolean isGetAllowed = isGetAllowedOnClass;
                    boolean isPostAllowed = isPostAllowedOnClass;
                    // giving priority to method level annotation GET/POST
                    if (method.isAnnotationPresent(GET.class) || method.isAnnotationPresent(POST.class)) {
                        System.out.println("Either GET/POST Present : " + p.value());
                        isGetAllowed = method.isAnnotationPresent(GET.class);
                        isPostAllowed = method.isAnnotationPresent(POST.class);
                    } else if (isGetAllowedOnClass == false && isPostAllowedOnClass == false)
                    // in case of no annotaiton
                    // present either on class
                    // or method allow both
                    // change dont allow both default would be get
                    {
                        System.out.println(path.value());
                        isGetAllowed = true;
                        // isGetAllowed = isPostAllowed = true;
                    }
                    ArrayList<RequestParameterInfo> requestParameterInfoList = new ArrayList<>();
                    // in case of Path present check for the method parameters
                    if (pathPresent) {
                        Parameter parameters[] = method.getParameters();
                        System.out.println("METHOD " + method.getName());
                        System.out.println(parameters.length);
                        RequestParameterInfo requestParameterInfo;
                        int i = 0;
                        for (Parameter parameter : parameters) {
                            System.out.println("Parameter : " + parameter.getType().getName());
                            // in case of get type method @RequestParameter is mandatory
                            // in case of post type method @RequestParameter must no be used
                            Class<?> parameterClass = parameter.getType();
                            boolean isInjectParameter = (parameterClass == SessionScope.class
                                    || parameterClass == ApplicationScope.class || parameterClass == RequestScope.class
                                    || parameterClass == ApplicationDirectory.class);
                            RequestParameter requestParameter = parameter.getAnnotation(RequestParameter.class);
                            if (isPostAllowed && requestParameter != null) {
                                // throw exception
                                throw new ServletException("Method " + method.getName()
                                        + " is annotated with @POST. @RequestParameter is not allowed on POST methods");
                            }
                            if (isPostAllowed) {
                                if (i == 2) {
                                    // Except SessionScope ApplicationScope RequestScope and ApplicationDirectory
                                    // only one other type of parameter allowed
                                    // otherwise raise exception
                                    System.out.println("Only one data parameter allowed in POST service");
                                    throw new ServletException("Only one data parameter allowed in POST service");
                                }
                                if (!isInjectParameter)
                                    i++;

                            }
                            // (in case of GET type service) if annotation not present raise exception and
                            // send error page
                            // Class<?> parameterClass=parameter.getType();
                            if (isGetAllowed) {
                                System.out.println("Inject Parameter " + isInjectParameter);
                                if (requestParameter == null && !isInjectParameter) {
                                    System.out.println("RAISED EXCEPTION");
                                    throw new ServletException(
                                            "Startup validation failed. Missing @RequestParameter in service method.");
                                }
                                if (requestParameter != null && isInjectParameter) {
                                    System.out.println("RAISED EXCEPTION");
                                    throw new ServletException(
                                            "Startup validation failed.Not Allowed @RequestParameter with either(SessionScope,RequestScope,ApplicationScope or ApplicationDirectory) in service method.");
                                }
                                String name = "";
                                if (!isInjectParameter)
                                    name = requestParameter.value();
                                System.out.println("PARAMETER");
                                System.out.println("Parameter class : " + parameterClass.getName());
                                System.out.println("Name : " + name);
                                requestParameterInfo = new RequestParameterInfo(parameterClass, name);
                                requestParameterInfoList.add(requestParameterInfo);
                            }
                        }

                    }
                    Forward forward = null;
                    if (runOnStart == false) {
                        forward = method.getAnnotation(Forward.class);
                    }
                    Service service = new Service();
                    service.setServiceClass(serviceClass);
                    service.setService(method);
                    try {
                        if (p != null) {
                            System.out.println("HELLO");
                            service.setPath(path.value() + p.value());
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    service.setIsGetAllowed(isGetAllowed);
                    service.setIsPostAllowed(isPostAllowed);
                    service.setRequestParameterInfoList(requestParameterInfoList);
                    service.setInjectSessionScope(injectSessionScope);
                    service.setInjectApplicationScope(injectApplicationScope);
                    service.setInjectRequestScope(injectRequestScope);
                    service.setInjectApplicationDirectory(injectApplicationDirectory);
                    service.setAutowiredList(autowiredList);
                    service.setRequestParameterFieldInfoList(requestParameterFieldInfoList);
                    service.setSecuredAccessInfo(securedAccessInfo);
                    if (forward != null)
                        service.setForwardTo(forward.value());
                    if (runOnStart) {
                        boolean valid = isStartUpMethodValid(method);
                        if (valid) {
                            service.setRunOnStart(runOnStart);
                            service.setPriority(onStartUp.priority());
                            insertRunOnStartServiceByPriority(service);
                        }
                    }
                    /*
                     * System.out.println("---------------");
                     * if (p != null)
                     * System.out.println("Path : " + path.value() + p.value());
                     * System.out.println(isGetAllowed + "," + isPostAllowed);
                     * System.out.println("---------------");
                     */
                    webRockModel.putService(service);

                }
            }
        }

    }

    private Class getServiceClass(File file) throws ServletException {
        try {
            String absolutePath = file.getAbsolutePath();
            String rootFolderName = getServletContext().getInitParameter("SERVICE_PACKAGE_PREFIX");
            int index = absolutePath.indexOf(rootFolderName + "\\");
            if (index == -1) {
                throw new ServletException("Root folder not found in path : " + rootFolderName + " , " + absolutePath);
            }
            // c:\tomcat9\webapps\dan\WEB-INF\classes\booby\com...
            // +1 for backslash
            // no need for this -> index+=rootFolderName.length()+1;
            String packageName = absolutePath.substring(index).replace("\\", ".");
            packageName = packageName.substring(0, packageName.lastIndexOf("."));
            System.out.println("packageName : " + packageName);
            return Class.forName(packageName);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isStartUpMethodValid(Method method) {
        boolean valid = false;
        valid = (method.getReturnType().equals(void.class) && method.getParameterCount() == 0);
        System.out.println("Start up method is valid  " + valid + ", name :" + method.getName());
        return valid;
    }

    private void insertRunOnStartServiceByPriority(Service service) {
        int i = 0;
        while (i < runOnStartServicesList.size()
                && runOnStartServicesList.get(i).getPriority() <= service.getPriority()) {
            i++;
        }
        runOnStartServicesList.add(i, service);
    }

    private void runStartServices() {
        System.out.println(runOnStartServicesList.size());
        try {
            for (Service service : runOnStartServicesList) {
                System.out.println("Invoking method : " + service.getService().getName());
                Object object = service.getServiceClass().newInstance();
                service.getService().invoke(object);
            }
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException exception) {
            System.out.println(exception);
        }
    }

    private SecuredAccessInfo handleSecuredAccessInfo(SecuredAccess securedAccess) throws ServletException {
        String checkpost = "";
        String guard = "";
        Class classCheckpostClass;
        Method classGuardMethod = null;
        boolean securedAccessOnClass = false;
        // if present then check for the checkpost and guard value
        if (securedAccess != null) {
            checkpost = securedAccess.checkpost();
            guard = securedAccess.guard();
            if (checkpost == null || guard == null) {
                // raise exception
                // put exception in DS
                System.out.println("INvalid Class checkpost/guard");
                return null;
            } else if (checkpost.trim().length() == 0 || guard.trim().length() == 0) {
                // raise exception
                // put exception in DS
                System.out.println("Invalid Class checkpost/guard");
                return null;
            }
            // now load the class and find the method
            try {
                classCheckpostClass = Class.forName(checkpost);
                // if class successfully loaded now fint the guard method
                Method methods[] = classCheckpostClass.getMethods();
                for (Method m : methods) {
                    if (m.getName().equals(guard)) {
                        classGuardMethod = m;
                        break;
                    }
                }
                // not found raise exception
                if (classGuardMethod == null) {
                    // load exception in DS
                    return null;
                }
                SecuredAccessInfo securedAccessInfo = new SecuredAccessInfo();
                securedAccessInfo.setClazz(classCheckpostClass);
                securedAccessInfo.setMethod(classGuardMethod);
                return securedAccessInfo;
            } catch (ClassNotFoundException classNotFoundException) {
                // raise exception and load into DS
                return null;
            }
        } // handling secured access on class level ends here
        else{ return null;}
    }
}