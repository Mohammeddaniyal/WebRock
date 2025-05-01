package com.thinking.machines.webrock;

import com.google.gson.*;
import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.sound.midi.SysexMessage;

import java.lang.reflect.*;
import com.thinking.machines.webrock.pojo.Service;
import com.thinking.machines.webrock.pojo.AutowiredInfo;
import com.thinking.machines.webrock.pojo.RequestParameterFieldInfo;
import com.thinking.machines.webrock.pojo.RequestParameterInfo;
import com.thinking.machines.webrock.pojo.SecuredAccessInfo;
import com.thinking.machines.webrock.scopes.ApplicationScope;
import com.thinking.machines.webrock.scopes.RequestScope;
import com.thinking.machines.webrock.scopes.SessionScope;
import com.thinking.machines.webrock.annotations.POST;
import com.thinking.machines.webrock.annotations.RequestParameter;
import com.thinking.machines.webrock.exceptions.ServiceException;
import com.thinking.machines.webrock.annotations.GET;
import com.thinking.machines.webrock.model.WebRockModel;

public class TMWebRock extends HttpServlet {
private Object getScopeObject(HttpServletRequest request,Class clazz)
{
    Object obj=null;
    if (clazz == SessionScope.class) {
        SessionScope sessionScope = new SessionScope(request.getSession());
        obj = sessionScope;
    } else if (clazz == ApplicationScope.class) {
        ApplicationScope applicationScope = new ApplicationScope(getServletContext());
        obj = applicationScope;
    } else if (clazz == RequestScope.class) {
        RequestScope requestScope = new RequestScope(request);
        obj = requestScope;
    } else if (clazz == ApplicationDirectory.class) {
        String directoryPath = getServletContext().getRealPath("/");
        File directory = new File(directoryPath);
        System.out.println("Directory path " + directoryPath);
        ApplicationDirectory applicationDirectory = new ApplicationDirectory(directory);
        obj = applicationDirectory;
    }
    return obj;
}
    private Object parseParameterBasedOnType(HttpServletRequest request, Class clazz, String reqParam) {
        Object arg = null;
        arg=getScopeObject(request,clazz);
        if(arg!=null) return arg;
        if (clazz == long.class || clazz == Long.class) {
            arg = Long.parseLong(reqParam);
        } else if (clazz == int.class || clazz == Integer.class) {
            arg = Integer.parseInt(reqParam);
        } else if (clazz == short.class || clazz == Short.class) {
            arg = Short.parseShort(reqParam);
        } else if (clazz == byte.class || clazz == Byte.class) {
            arg = Byte.parseByte(reqParam);
        } else if (clazz == double.class || clazz == Double.class) {
            arg = Double.parseDouble(reqParam);
        } else if (clazz == float.class || clazz == Float.class) {
            arg = Float.parseFloat(reqParam);
        } else if (clazz == char.class || clazz == Character.class) {
            arg = reqParam.charAt(0);
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            arg = Boolean.parseBoolean(reqParam);
        } else// means String
        {
            arg = reqParam;
        }
        return arg;
    }

    private boolean isPrimitive(byte primitive, Class<?> argClass) {
        boolean primitiveMatched = false;
        if (primitive != -1) {
            if (primitive == 0 && (argClass == Long.class || argClass == long.class))
                primitiveMatched = true;
            else if (primitive == 1 && (argClass == Integer.class || argClass == int.class))
                primitiveMatched = true;
            else if (primitive == 2 && (argClass == Short.class || argClass == short.class))
                primitiveMatched = true;
            else if (primitive == 3 && (argClass == Byte.class || argClass == byte.class))
                primitiveMatched = true;
            else if (primitive == 4 && (argClass == Double.class || argClass == double.class))
                primitiveMatched = true;
            else if (primitive == 5 && (argClass == Float.class || argClass == float.class))
                primitiveMatched = true;
            else if (primitive == 6 && (argClass == Character.class || argClass == char.class))
                primitiveMatched = true;
            else if (primitive == 7 && (argClass == Boolean.class || argClass == boolean.class))
                primitiveMatched = true;

        }
        return primitiveMatched;
    }

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

    private void handleInjection(HttpServletRequest request, Service service, Class serviceClass, Object object)
            throws ServiceException {
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
            List<RequestParameterFieldInfo> requestParameterFieldInfoList = service.getRequestParameterFieldInfoList();

            for (RequestParameterFieldInfo requestParameterFieldInfo : requestParameterFieldInfoList) {
                String name = requestParameterFieldInfo.getName();
                String argString = request.getParameter(name);
                if (argString == null)
                    continue;
                Method setterMethod = requestParameterFieldInfo.getSetterMethod();
                if (setterMethod == null) {
                    // raise exception
                    return;
                }
                Parameter[] params = setterMethod.getParameters();

                if (params.length == 0 || params.length > 1) {
                    // raise exception
                    // send error page
                    System.out.println("Illegal number of parameters");
                    return;
                }

                Class<?> paramType = params[0].getType();
                Class<?> fieldClass = requestParameterFieldInfo.getFieldClass();
                byte primitive = (byte) getParameterPrimitiveType(paramType);
                System.out.println("Is primitive : " + primitive);
                System.out.println("Param type : " + paramType.getName());
                System.out.println("Field type : " + fieldClass.getName());
                boolean primitiveMatched = false;
                // if the parameter is primitive, then compare it with the argument type
                primitiveMatched = isPrimitive((byte) primitive, fieldClass);

                System.out.println("Param & Arg matched or not : " + primitiveMatched);
                // if the parameter and argument mismatches
                if (!paramType.isInstance(fieldClass) && !primitiveMatched) {
                    throw new RuntimeException("Argumment type mismatch");
                }
                Object arg = parseParameterBasedOnType(request, fieldClass, argString);
                System.out.println("FIELD ARGUMENT : " + arg);
                try {
                    setterMethod.invoke(object, arg);
                } catch (IllegalAccessException | InvocationTargetException exp) {
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
                if (arg != null) {
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
                        primitiveMatched = isPrimitive((byte) primitive, argClass);

                        System.out.println("Param & Arg matched or not : " + primitiveMatched);
                        // if the parameter and argument mismatches
                        if (!paramType.isInstance(arg) && !primitiveMatched) {
                            throw new ServiceException("Argumment type mismatch");
                        }

                        try {
                            Class<?> serviceClass = service.getServiceClass();
                            Object object = serviceClass.newInstance();
                            // before invoking handle the autowirings
                            handleAutowiredProperties(service, request, object);
                            handleInjection(request, service, serviceClass, object);
                            // check if the argument is primitive type
                            // if it's primitive then invoke the method accordignly
                            Object result = method.invoke(object, arg);

                            forwardTo = service.getForwardTo();
                            System.out.println("In Forward Handling the request is being forward to : " + forwardTo);

                            if (result != null)
                                System.out.println("Result class : " + result.getClass().getName());
                            if (forwardTo != null) {
                                // no need for this line, i'll handle it later why no need
                                // handleInjection(request, service, serviceClass, object);
                                try {
                                    handleRequestForwardTo(request, response, webRockModel, forwardTo, result);
                                } catch (ServiceException serviceException) {
                                    System.out.println(serviceException.getMessage());
                                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                }
                            }

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
                } else {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    throw new ServiceException("The Service (" + forwardTo + ") meant to be forward not found");
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void handleAutowiredProperties(Service service, HttpServletRequest request, Object obj)
            throws ServiceException {
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
                setterMethod.invoke(obj, object);

            } catch (InvocationTargetException | IllegalAccessException exception) {
                System.out.println("Exception raised");
                System.out.println(exception.getMessage());
                throw new ServiceException(exception.getMessage());
            }
        }
    }

    private void ensureSecuredAccess(HttpServletRequest request,Service service) throws ServiceException
    {
        SecuredAccessInfo securedAccessInfo=service.getSecuredAccessInfo();
        if(securedAccessInfo==null) return;
        Class clazz=securedAccessInfo.getClazz();
        Method method=securedAccessInfo.getMethod();
        Parameter pararmeters[]=method.getParameters();
        Object args[]=new Object[pararmeters.length];
        int i=0;
        Class<?> paramType;
        Object arg;
        for(Parameter parameter:pararmeters)
        {
            paramType=parameter.getType();
            arg=getScopeObject(request, paramType);
            if(arg==null)
            {
                //raise exception that method now allowed with parameters other than scope class one's
                throw new ServiceException("Parameter type not allowed");
            }
            args[i]=arg;
            i++;
        }
        //now invoke method 
        try{
            Object object=clazz.newInstance();
            System.out.println(args[0]);
            method.invoke(object,args);
        }catch(InvocationTargetException | IllegalAccessException | InstantiationException exception)
        {
            throw new ServiceException("Exception occured cause "+exception);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            System.out.println("GET TYPE REQUEST");
            WebRockModel webRockModel = (WebRockModel) getServletContext().getAttribute("webRockModel");
            // webRockModel.print();
            String path = request.getPathInfo();
            Service service = webRockModel.getService(path);
            if (service == null) {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            Class serviceClass = service.getServiceClass();
            String forwardTo = service.getForwardTo();
            if (!service.isGetAllowed()) {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            //perform security checks
            ensureSecuredAccess(request,service);
            // before invoking the service/method
            // set data against the all autowired properties
            Object obj = serviceClass.newInstance();
            handleAutowiredProperties(service, request, obj);

            Method serviceMethod = service.getService();

            System.out.println(request.getPathInfo());

            List<RequestParameterInfo> requestParameterInfoList = service.getRequestParameterInfoList();
            System.out.println("Size : " + requestParameterInfoList.size());
            Object args[] = new Object[requestParameterInfoList.size()];
            ArrayList<Object> injectRequestParameterList = new ArrayList<>();
            // Map<String,Object> reqParamMap=new LinkedHashMap<>();
            int i = 0;
            for (RequestParameterInfo requestParameterInfo : requestParameterInfoList) {
                String paramName = requestParameterInfo.getName();
                String reqParam = request.getParameter(paramName);
                Class<?> parameterClass = requestParameterInfo.getParameterClass();
                boolean isInjectParameter = (parameterClass == SessionScope.class
                        || parameterClass == ApplicationScope.class || parameterClass == RequestScope.class
                        || parameterClass == ApplicationDirectory.class);
                if ((reqParam == null && !isInjectParameter) || parameterClass == null) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "Bad Request : Required query parameter is misisng or does not match the expected name");
                    return;
                }

                // check if the parameter were of inject one's
                // (SessionScope,ApplicationScope,RequestScope and ApplicationDirectory)
                args[i] = parseParameterBasedOnType(request, parameterClass, reqParam);
                // if(!isInjectParameter) reqParamMap.put(paramName,args[i]);
                System.out.println(i);
                i++;
            }
            System.out.println("Object arguments length : " + args.length);

            System.out.println("Invoking method : " + serviceMethod.getName());
            handleInjection(request, service, serviceClass, obj);
            Object result = serviceMethod.invoke(obj, args);
            System.out.println("Result : " + result);
            if (result != null)
                System.out.println("Result class : " + result.getClass().getName());
            if (forwardTo != null) {
                // no need for this line after verifying i'll remove it
                // reason becuase handling injection is already done before
                // handleInjection(request, service, serviceClass, obj);
                try {
                    handleRequestForwardTo(request, response, webRockModel, forwardTo, result);
                } catch (ServiceException serviceException) {
                    System.out.println("Got Service exception");
                    System.out.println(serviceException.getMessage());
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            }

        } catch (ServiceException serviceException) {
            System.out.println(serviceException);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
            if(service==null)
            {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            Class serviceClass = service.getServiceClass();
            String forwardTo = service.getForwardTo();
            if (!service.isPostAllowed()) {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
               // before invoking the service/method
            // set data against the all autowired properties
            Object obj=serviceClass.newInstance();
            handleAutowiredProperties(service, request,obj);
            Method serviceMethod = service.getService();

            System.out.println(request.getPathInfo());
            
            //peform validation on method 
            Parameter parameters[]=serviceMethod.getParameters();
            // most of this validation done at the start of server
            //n denotes number of parameters

            //in case of zero parameters no issue

            //in case of parameter greater than 1, then (n-1) parameter should either be type of
            // SessionScope, RequestScope, ApplicationScope or ApplicationDirectory

            //before invoking, use Gson to convert the Json string into respective specified Parameter class type Object
            //if exception occurs, throw new ServiceExcepion and send 500 error page

            //and also don't allow this annotation with the parameter

            Object args[]=null;
            if(parameters.length>0)
            {
                Class<?> parameterClass;
                Parameter parameter=null;
                args=new Object[parameters.length];
                int i=0;
                int jsonParamIndex=-1;
                //search the parameter where we need to put the object
                for(Parameter p:parameters)
                {
                    parameterClass=p.getType();
                if(parameterClass==SessionScope.class || parameterClass==ApplicationScope.class || parameterClass==RequestScope.class || parameterClass==ApplicationDirectory.class)
                {
                    args[i++]=parseParameterBasedOnType(request,parameterClass,"");
                }else{
                    jsonParamIndex=i;
                parameter=p;
                }
                }
                if(parameter!=null && jsonParamIndex!=-1)
                {
                
                parameterClass=parameter.getType();
            //handle json here

            BufferedReader bufferedReader=request.getReader();
            StringBuffer stringBuffer=new StringBuffer();
            String d;
            while(true)
            {
                d=bufferedReader.readLine();
                if(d==null) break;
                stringBuffer.append(d);
            }
            String rawData=stringBuffer.toString();
            Gson gson=new Gson();
            try{
                System.out.println("JSON : "+rawData);
            Object arg=gson.fromJson(rawData,parameterClass);
            args[jsonParamIndex]=arg;
            System.out.println("Succesfully deserialized "+args[jsonParamIndex]);
            //now's time to invoke method
            }catch(Exception exp)
            {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                throw new ServiceException("JSON deserilization failed, cause "+exp);
            }        
        }
             }
             System.out.println("Args : "+args.length);
             if(args==null) args=new Object[0];
            System.out.println(args);
            System.out.println("Invoking method : " + serviceMethod.getName());
            handleInjection(request, service, serviceClass, obj);
            Object result = serviceMethod.invoke(obj, args);
            System.out.println("Result : " + result);
            if (forwardTo != null) {
                // no need for this line after verifying i'll remove it
                //reason becuase handling injection is already done before
                //handleInjection(request, service, serviceClass, obj);
                try {
                    handleRequestForwardTo(request, response, webRockModel, forwardTo, result);
                } catch (ServiceException serviceException) {
                    System.out.println(serviceException.getMessage());
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}