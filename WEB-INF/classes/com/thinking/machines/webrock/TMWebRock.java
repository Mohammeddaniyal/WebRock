package com.thinking.machines.webrock;
import javax.servlet.*;
import javax.servlet.http.*;

import com.thinking.machines.webrock.model.WebRockModel;
public class TMWebRock extends HttpServlet
{
    public void doGet(HttpServletRequest request,HttpServletResponse response)
    {
        try{
            WebRockModel webRockModel=(WebRockModel)getServletContext().getAttribute("webRockModel");
            webRockModel.print();
        }catch(Exception e){System.out.println(e);}
    }
}