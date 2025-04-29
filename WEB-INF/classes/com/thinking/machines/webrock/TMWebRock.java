package com.thinking.machines.webrock;

import java.io.File;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.reflect.*;
import com.thinking.machines.webrock.pojo.Service;
import com.thinking.machines.webrock.pojo.AutowiredInfo;
import com.thinking.machines.webrock.scopes.ApplicationScope;
import com.thinking.machines.webrock.scopes.RequestScope;
import com.thinking.machines.webrock.scopes.SessionScope;
import com.thinking.machines.webrock.annotations.POST;
import com.thinking.machines.webrock.exceptions.ServiceException;
import com.thinking.machines.webrock.annotations.GET;
import com.thinking.machines.webrock.model.WebRockModel;

public class TMWebRock extends HttpServlet {
    private byte getParameterPrimitiveType(Class<?> paramType) {

        if (paramType == long.class)
            return (byte) 0;
        if (paramType == int.class)
            return (byte) 1;
        if (paramType == short.class)
            return (byte) 2;
        if (paramType == byte.class)
            return (byte) 3;
        if (paramType == double.class)
            return (byte) 4;
        if (paramType == float.class)
            return (byte) 5;
        if (paramType == char.class)
            return (byte) 6;
        if (paramType == boolean.class)
            return (byte) 7;

        return (byte) -1;
    }

    private Method getMethod(Class serviceClass, String name, Class paramType) {
        try {
            return serviceClass.getMethod(name, paramType);
        } catch (NoSuchMethodException noSuchMethodException) {
            return null;
        }
    }

    private void handleInjection(HttpServletRequest request, Service service, Class serviceClass, Object object) {
        try {
            Method method;
            if (service.getInjectSessionScope()) {
                System.out.println("Sesesison scope");
                method = getMethod(serviceClass, "setSessionScope", SessionScope.class);
                // if setter method not found ignore
                if (method != null) {
                    SessionScope sessionScope = new SessionScope(request.getSession());
                    System.out.println("Setting session scope : " + sessionScope);
                    System.out.println("Method : " + method.getName());
                    method.invoke(object, sessionScope);
                }
            }
            if (service.getInjectApplicationScope()) {
                method = getMethod(serviceClass, "setApplicationScope", ApplicationScope.class);
                // if setter method not found ignore
                if (method != null) {
                    ApplicationScope applicationScope = new ApplicationScope(getServletContext());
                    method.invoke(object, applicationScope);
                }
            }
            if (service.getInjectRequestScope()) {
                method = getMethod(serviceClass, "setRequestScope", RequestScope.class);
                // if setter method not found ignore
                if (method != null) {
                    RequestScope requestScope = new RequestScope(request);
                    method.invoke(object, requestScope);
                }

            }
            if (service.getInjectApplicationDirectory()) {
                method = getMethod(serviceClass, "setApplicationDirectory", ApplicationDirectory.class);
                if (method != null) {
                    String directoryPath = getServletContext().getRealPath("/");
                    File directory = new File(directoryPath);
                    ApplicationDirectory applicationDirectory = new ApplicationDirectory(directory);
                    method.invoke(object, applicationDirectory);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void handleRequestForwardTo(HttpServletRequest request, HttpServletResponse response,
            WebRockModel webRockModel, String forwardTo, Object arg) throws ServiceException {
        try {
            Service service = webRockModel.getService(forwardTo);
            boolean exists = ((service) != null);
            RequestDispatcher requestDispatcher = null;
            if (exists) {
                // if arg is not null
                if (arg!=null) {
                    // what ever that method returns which forward the request to this service
                    // pass the returned result as argument to the service parameter
                    // only if the any parameter exists otherwise Ignore
                    Method method = service.getService();
                    Parameter params[] = method.getParameters();
                    if (params.length > 1) {
                        throw new ServiceException("The forwading method number of arguments is more than one");
                    }
                    // if no numbers of parameters ignore it
                    if (params.length == 1) {
                        // now check the compatabilty of the argument (which the earlier method retuned)
                        // with the parameter
                        Class<?> paramType = params[0].getType();
                        Class<?> argClass = arg.getClass();
                        byte primitive = (byte) getParameterPrimitiveType(paramType);
                        System.out.println("Is primitive : " + primitive);
                        System.out.println("Param type : " + paramType.getName());
                        boolean primitiveMatched = false;
                        // if the parameter is primitive, then compare it with the argument type
                        if (primitive != -1) {
                            if (primitive == 0 && argClass == Long.class)
                                primitiveMatched = true;
                            else if (primitive == 1 && argClass == Integer.class)
                                primitiveMatched = true;
                            else if (primitive == 2 && argClass == Short.class)
                                primitiveMatched = true;
                            else if (primitive == 3 && argClass == Byte.class)
                                primitiveMatched = true;
                            else if (primitive == 4 && argClass == Double.class)
                                primitiveMatched = true;
                            else if (primitive == 5 && argClass == Float.class)
                                primitiveMatched = true;
                            else if (primitive == 6 && argClass == Character.class)
                                primitiveMatched = true;
                            else if (primitive == 7 && argClass == Boolean.class)
                                primitiveMatched = true;

                        }
                        System.out.println("Param & Arg matched or not : " + primitiveMatched);
                        // if the parameter and argument mismatches
                        if (!paramType.isInstance(arg) && !primitiveMatched) {
                            throw new ServiceException("Argumment type mismatch");

                        }
                        try {
                            Object object = service.getServiceClass().newInstance();
                            // check if the argument is primitive type
                            // if it's primitive then invoke the method accordignly
                            method.invoke(object, arg);
                        } catch (InstantiationException | IllegalAccessException exp) {
                            throw new ServiceException(exp.getMessage());
                        }
                    }

                } else {
                    // in case of no parameter handle it normally

                    // get the servlet mapping part of the url (based on how servlet is mapped)
                    // for instance it'll return /calculatorService
                    String servletPath = request.getServletPath();
                    requestDispatcher = request.getRequestDispatcher(servletPath + forwardTo);
                    requestDispatcher.forward(request, response);
                }
            } else {
                // check whether the resource exists or not
                if (getServletContext().getResource(forwardTo) != null) {
                    requestDispatcher = request.getRequestDispatcher(forwardTo);
                    requestDispatcher.forward(request, response);
                } else
                    throw new ServiceException("The Service (" + forwardTo + ") meant to be forward not found");
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void handleAutowiredProperties(Service service, HttpServletRequest request) {
        System.out.println("HANDLE AUTOWIRE");
        List<AutowiredInfo> autowiredList = service.getAutowiredList();
        System.out.println("Size : " + autowiredList.size());
        for (AutowiredInfo autowiredInfo : autowiredList) {
            Class<?> fieldTypeClass = autowiredInfo.getAutowiredField().getType();
            String fieldTypeClassName = fieldTypeClass.getName();
            String beanName = autowiredInfo.getBeanName();
            System.out.println("Bean name" + beanName);
            Method setterMethod = autowiredInfo.getSetterMethod();
            // now look in this flow of scope
            // request or if not found then -> session , not found - > application
            // If Found, check the type of Object with method parameter
            Object object = null;
            object = request.getAttribute(beanName);
            System.out.println("IN REQ " + object);
            if (!fieldTypeClass.isInstance(object))
                object = null;
            if (object == null) {
                // check in session scope
                object = request.getSession().getAttribute(beanName);
                System.out.println("IN Session : " + object);
                if (!fieldTypeClass.isInstance(object))
                    object = null;
                if (object == null) {
                    object = getServletContext().getAttribute(beanName);
                    if (!fieldTypeClass.isInstance(object))
                        object = null;
                }
            }

            try {
                Object obj = service.getServiceClass().newInstance();
                if(object!=null)  setterMethod.invoke(obj, object);
                else setterMethod.invoke(obj, new Object[]{null});
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException exception) {
                System.out.println("Exception raised");
                System.out.println(exception);
            }
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            System.out.println("GET TYPE REQUEST");
            WebRockModel webRockModel = (WebRockModel) getServletContext().getAttribute("webRockModel");
            // webRockModel.print();
            String path = request.getPathInfo();
            Service service = webRockModel.getService(path);
            Class serviceClass = service.getServiceClass();
            String forwardTo = service.getForwardTo();
            if (!service.isGetAllowed()) {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            // before invoking the service/method
            // set data against the all autowired properties

            handleAutowiredProperties(service, request);

            Method serviceMethod = service.getService();

            System.out.println(request.getPathInfo());
            //int a = Integer.parseInt(request.getParameter("a"));
            //int b = Integer.parseInt(request.getParameter("b"));
//            System.out.println("Values : " + a + "," + b);

            Object obj = serviceClass.newInstance();
            System.out.println("Invoking method : " + serviceMethod.getName());
            handleInjection(request, service, serviceClass, obj);
            Object result = serviceMethod.invoke(obj, new Object[0]);
            System.out.println("Result : " + result);
            if (result != null)
                System.out.println("Result class : " + result.getClass().getName());
            if (forwardTo != null) {
                handleInjection(request, service, serviceClass, obj);
                try {
                    handleRequestForwardTo(request, response, webRockModel, forwardTo, result);
                } catch (ServiceException serviceException) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            }

        } catch (InvocationTargetException ite) {
            System.out.println(ite);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            System.out.println("POST TYPE REQUEST");
            WebRockModel webRockModel = (WebRockModel) getServletContext().getAttribute("webRockModel");
            String path = request.getPathInfo();
            Service service = webRockModel.getService(path);
            Class serviceClass = service.getServiceClass();
            String forwardTo = service.getForwardTo();
            if (!service.isPostAllowed()) {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            Method serviceMethod = service.getService();

            System.out.println(request.getPathInfo());
            int a = Integer.parseInt(request.getParameter("a"));
            int b = Integer.parseInt(request.getParameter("b"));
            System.out.println("Values : " + a + "," + b);

            Object obj = serviceClass.newInstance();
            Object result = serviceMethod.invoke(obj, a, b);
            System.out.println("Result : " + result);
            if (forwardTo != null) {
                handleInjection(request, service, serviceClass, obj);
                handleRequestForwardTo(request, response, webRockModel, forwardTo, result);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}