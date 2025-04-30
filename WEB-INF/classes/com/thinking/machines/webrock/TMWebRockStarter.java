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
        System.out.println("Absolute Path : "+absolutePath);
        System.out.println("Path : "+getServletContext().getRealPath("/"));
        
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

                boolean isGetAllowedOnClass = serviceClass.isAnnotationPresent(GET.class);

                boolean isPostAllowedOnClass = serviceClass.isAnnotationPresent(POST.class);

                boolean injectSessionScope = serviceClass.isAnnotationPresent(InjectSessionScope.class);

                boolean injectApplicationScope = serviceClass.isAnnotationPresent(InjectApplicationScope.class);

                boolean injectRequestScope = serviceClass.isAnnotationPresent(InjectRequestScope.class);

                boolean injectApplicationDirectory = serviceClass.isAnnotationPresent(InjectApplicationDirectory.class);

              //before creating service object and putting them into hashamp
              //create List of autowired properties
              Field[] fields=serviceClass.getDeclaredFields();
                ArrayList<AutowiredInfo> autowiredList=new ArrayList<>();
              for(Field field:fields)
              {
                Autowired autowired=field.getAnnotation(Autowired.class);
                if(autowired!=null)
                {
                    System.out.println("Field with autowired : "+field.getName());
                    System.out.println("Autowired value : "+autowired.name());
                    String beanName=autowired.name();
                    Class<?> fieldTypeClass=field.getType();
                    String fieldName=fieldTypeClass.getSimpleName();
                    String setterMethodName="set"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
                    System.out.println("Setter method name : "+setterMethodName);
                    Method setterMethod=null;
                    try
                    {
                    setterMethod=serviceClass.getMethod(setterMethodName,fieldTypeClass);
                    System.out.println("Got setter method : "+setterMethod.getName());
                    }catch(NoSuchMethodException noSuchMethodException)
                    {
                        System.out.println("Method not found(setterMethod)");
                        setterMethod=null;
                    }
                    // now create the objec of AutowiredInfo
                    AutowiredInfo autowiredInfo=new AutowiredInfo(beanName, field, setterMethod);
                    autowiredList.add(autowiredInfo);
                }


                
              }
              
                Method[] methods = serviceClass.getDeclaredMethods();
                serviceClass.getDeclaredMethods();
             
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
                    {
                        System.out.println(path.value());
                        isGetAllowed = isPostAllowed = true;
                    }
                    ArrayList<RequestParameterInfo> requestParameterInfoList=new ArrayList<>();
                    // in case of Path present check for the method parameters
                    if (pathPresent) {
                        Parameter parameters[] = method.getParameters();
                        System.out.println("METHOD "+method.getName());
                        System.out.println(parameters.length);
                        RequestParameterInfo requestParameterInfo;
                        for (Parameter parameter : parameters) {
                            System.out.println("Parameter : "+parameter.getType().getName());
                            RequestParameter requestParameter = parameter.getAnnotation(RequestParameter.class);
                            // if annotation not present raise exception and send error page
                            Class<?> parameterClass=parameter.getType();
                         
                            boolean isInjectParameter=(parameterClass==SessionScope.class || parameterClass==ApplicationScope.class || parameterClass==RequestScope.class || parameterClass==ApplicationDirectory.class);
                            System.out.println("Inject Parameter "+isInjectParameter);
                            if (requestParameter == null && !isInjectParameter) {    
                                System.out.println("RAISED EXCEPTION");
                                throw new ServletException(
                                        "Startup validation failed. Missing @RequestParameter in service method.");
                            }
                            String name="";
                            if(!isInjectParameter) name=requestParameter.value();
                            System.out.println("PARAMETER");
                            System.out.println("Parameter class : "+parameterClass.getName());
                            System.out.println("Name : "+name);
                            requestParameterInfo=new RequestParameterInfo(parameterClass, name);
                            requestParameterInfoList.add(requestParameterInfo);
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
                    System.out.println("---------------");
                    if (p != null)
                        System.out.println("Path : " + path.value() + p.value());
                    System.out.println(isGetAllowed + "," + isPostAllowed);
                    System.out.println("---------------");*/
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
            // index+=rootFolderName.length()+1;
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
}