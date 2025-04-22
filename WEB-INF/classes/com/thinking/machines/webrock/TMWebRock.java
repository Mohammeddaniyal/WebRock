package com.thinking.machines.webrock;
import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.reflect.*;
import com.thinking.machines.webrock.pojo.Service;
import com.thinking.machines.webrock.model.WebRockModel;
public class TMWebRock extends HttpServlet
{
    public void doGet(HttpServletRequest request,HttpServletResponse response)
    {
        try{
            WebRockModel webRockModel=(WebRockModel)getServletContext().getAttribute("webRockModel");
            //webRockModel.print();
            System.out.println(request.getPathInfo());
            int a=Integer.parseInt(request.getParameter("a"));
            int b=Integer.parseInt(request.getParameter("b"));
            System.out.println("Values : "+a+","+b);
            String path=request.getPathInfo();
            Service service=webRockModel.get(path);
            Class serviceClass=service.getServiceClass();
            Method serviceMethod=service.getService();
            Object obj=serviceClass.newInstance();
            Object result=serviceMethod.invoke(obj,a,b);
            System.out.println("Result : "+result);
        }catch(Exception e){System.out.println(e);}
    }
}