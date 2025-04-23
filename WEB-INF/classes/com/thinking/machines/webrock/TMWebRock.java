package com.thinking.machines.webrock;
import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.reflect.*;
import com.thinking.machines.webrock.pojo.Service;
import com.thinking.machines.webrock.annotations.POST;
import com.thinking.machines.webrock.annotations.GET;
import com.thinking.machines.webrock.model.WebRockModel;
public class TMWebRock extends HttpServlet
{
    private void handleRequestForwardTo(HttpServletRequest request,HttpServletResponse response,WebRockModel webRockModel,String forwardTo)
    {
        try
        {
            RequestDispatcher requestDispatcher=request.getRequestDispatcher(forwardTo);
            if(webRockModel.getService(forwardTo)!=null)
            {
                requestDispatcher.forward(request,response);   
            }else if(requestDispatcher!=null){
                requestDispatcher.forward(request,response);
            }else{
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
            if(serviceClass.isAnnotationPresent(POST.class))
            {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            Method serviceMethod=service.getService();
            if(serviceMethod.isAnnotationPresent(POST.class))
            {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            System.out.println(request.getPathInfo());
            int a=Integer.parseInt(request.getParameter("a"));
            int b=Integer.parseInt(request.getParameter("b"));
            System.out.println("Values : "+a+","+b);
            
            Object obj=serviceClass.newInstance();
            Object result=serviceMethod.invoke(obj,a,b);
            System.out.println("Result : "+result);
            if(forwardTo!=null) handleRequestForwardTo(request,response,webRockModel,forwardTo);
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
            if(serviceClass.isAnnotationPresent(GET.class))
            {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            Method serviceMethod=service.getService();
            if(serviceMethod.isAnnotationPresent(GET.class))
            {
                System.out.println("GET annotation is with the method");
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            System.out.println(request.getPathInfo());
            int a=Integer.parseInt(request.getParameter("a"));
            int b=Integer.parseInt(request.getParameter("b"));
            System.out.println("Values : "+a+","+b);
            
            Object obj=serviceClass.newInstance();
            Object result=serviceMethod.invoke(obj,a,b);
            System.out.println("Result : "+result);
            if(forwardTo!=null) handleRequestForwardTo(request,response,webRockModel,forwardTo);
        }catch(Exception e){System.out.println(e);}
    }
}