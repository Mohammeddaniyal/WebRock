package com.thinking.machines.webrock;

import java.io.File;
import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.reflect.*;
import com.thinking.machines.webrock.pojo.Service;
import com.thinking.machines.webrock.scopes.ApplicationScope;
import com.thinking.machines.webrock.scopes.RequestScope;
import com.thinking.machines.webrock.scopes.SessionScope;
import com.thinking.machines.webrock.annotations.POST;
import com.thinking.machines.webrock.annotations.GET;
import com.thinking.machines.webrock.model.WebRockModel;
public class TMWebRock extends HttpServlet
{
    private Method getMethod(Class serviceClass,String name,Class paramType)
    {
        try{
            return serviceClass.getMethod(name,paramType);
        }catch(NoSuchMethodException noSuchMethodException)
        {
            return null;
        }
    }
    private void handleInjection(HttpServletRequest request, Service service , Class serviceClass,Object object){
        try {
            Method method;
            if(service.getInjectSessionScope())
            {
                method=getMethod(serviceClass, "setSessionScope",SessionScope.class);
                //if setter method not found ignore
                if(method!=null)
                {
                    SessionScope sessionScope=new SessionScope(request.getSession());
                    method.invoke(object, sessionScope);
                }
            }
            if(service.getInjectApplicationScope())
            {
                method=getMethod(serviceClass,"setApplicationScope",ApplicationScope.class);
                //if setter method not found ignore
                if(method!=null)
                {
                    ApplicationScope applicationScope=new ApplicationScope(getServletContext());
                    method.invoke(object, applicationScope);
                }
             }
            if(service.getInjectRequestScope())
            {
                method=getMethod(serviceClass, "setRequestScope",RequestScope.class);
                //if setter method not found ignore
                if(method!=null)
                {
                    RequestScope requestScope=new RequestScope(request);
                    method.invoke(object, requestScope);
                }

            }
            if(service.getInjectApplicationDirectory())
            {
                method=getMethod(serviceClass,"setApplicationDirectory",ApplicationDirectory.class);
                if(method!=null)
                {
                    String directoryPath=getServletContext().getRealPath("/");
                    File directory=new File(directoryPath); 
                    ApplicationDirectory applicationDirectory=new ApplicationDirectory(directory);
                    method.invoke(object, applicationDirectory);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void handleRequestForwardTo(HttpServletRequest request,HttpServletResponse response,WebRockModel webRockModel,String forwardTo)
    {
        try
        {
            boolean exists=(webRockModel.getService(forwardTo)!=null);
            RequestDispatcher requestDispatcher=null;
            if(exists)
            {
                // get the servlet mapping part of the url (based on how servlet is mapped)
                // for instance it'll return /calculatorService
                String servletPath=request.getServletPath(); 
                requestDispatcher=request.getRequestDispatcher(servletPath+forwardTo);
                requestDispatcher.forward(request,response);    
            }else{
                //check whether the resource exists or not
                if(getServletContext().getResource(forwardTo)!=null)
                    {
                    requestDispatcher=request.getRequestDispatcher(forwardTo);  
                    requestDispatcher.forward(request,response);
                    }
                else response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            }catch(Exception e)
        {
            System.out.println(e);  
        }
    }
    public void doGet(HttpServletRequest request,HttpServletResponse response)
    {
        try{
            System.out.println("GET TYPE REQUEST");
            WebRockModel webRockModel=(WebRockModel)getServletContext().getAttribute("webRockModel");
            webRockModel.print();
            String path=request.getPathInfo();
            Service service=webRockModel.getService(path);
            Class serviceClass=service.getServiceClass();
            String forwardTo=service.getForwardTo();
            if(!service.isGetAllowed())
            {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            Method serviceMethod=service.getService();
            
            System.out.println(request.getPathInfo());
            int a=Integer.parseInt(request.getParameter("a"));
            int b=Integer.parseInt(request.getParameter("b"));
            System.out.println("Values : "+a+","+b);
            
            Object obj=serviceClass.newInstance();
            Object result=serviceMethod.invoke(obj,a,b);
            System.out.println("Result : "+result);
            if(forwardTo!=null)
            {
                handleInjection(request,service,serviceClass,obj);
                handleRequestForwardTo(request,response,webRockModel,forwardTo);
            } 
        }catch(Exception e){System.out.println(e);}
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)
    {
        try{
            System.out.println("POST TYPE REQUEST");
            WebRockModel webRockModel=(WebRockModel)getServletContext().getAttribute("webRockModel");
            String path=request.getPathInfo();
            Service service=webRockModel.getService(path);
            Class serviceClass=service.getServiceClass();
            String forwardTo=service.getForwardTo();
            if(!service.isPostAllowed())
            {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            Method serviceMethod=service.getService();
            
            System.out.println(request.getPathInfo());
            int a=Integer.parseInt(request.getParameter("a"));
            int b=Integer.parseInt(request.getParameter("b"));
            System.out.println("Values : "+a+","+b);
            
            Object obj=serviceClass.newInstance();
            Object result=serviceMethod.invoke(obj,a,b);
            System.out.println("Result : "+result);
            if(forwardTo!=null) 
            {
                handleInjection(request,service,serviceClass,obj);
                handleRequestForwardTo(request,response,webRockModel,forwardTo);
            }
            }catch(Exception e){System.out.println(e);}
    }
}