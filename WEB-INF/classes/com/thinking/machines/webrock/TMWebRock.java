package com.thinking.machines.webrock;
import javax.servlet.*;
import javax.servlet.http.*;
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
            String path=request.getPathInfo();
            Service service=webRockModel.get(path);
        }catch(Exception e){System.out.println(e);}
    }
}