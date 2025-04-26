package com.thinking.machines.webrock;

import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import com.thinking.machines.webrock.pojo.*;
import com.thinking.machines.webrock.model.*;
import com.thinking.machines.webrock.annotations.*;
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

                Method[] methods = serviceClass.getDeclaredMethods();
                System.out.println("Size " + methods.length);
                // try {
                serviceClass.getDeclaredMethods();
                /*
                 * / } catch (ClassNotFoundException e) {
                 * System.out.println("Error forcing class initialization: " + e.getMessage());
                 * logger.severe("Error forcing class initialization: " + e.getMessage());
                 * }
                 */
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
                    System.out.println("---------------");
                    if (p != null)
                        System.out.println("Path : " + path.value() + p.value());
                    System.out.println(isGetAllowed + "," + isPostAllowed);
                    System.out.println("---------------");
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