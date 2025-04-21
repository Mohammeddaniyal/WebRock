package com.thinking.machines.webrock;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import com.google.gson.*;
import com.thinking.machines.webrock.model.*;
public class ServletOne extends HttpServlet
{
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
try
{
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
}catch(Exception exception)
{
}
}
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
try
{
System.out.println("HELLLOLLLLO");
PrintWriter pw=response.getWriter();
System.out.println("200000");
response.setContentType("text/plain");
System.out.println("100000");
Gson gson=new Gson();
System.out.println("1111");
WebRockModel webRockModel=(WebRockModel)getServletContext().getAttribute("webRockModel");
System.out.println("2222");
System.out.println(webRockModel);

webRockModel.print();
 
pw.println(getServletContext().getAttribute("hello"));
}catch(Exception exception)
{
System.out.println(exception.getMessage());
try
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
}catch(Exception e)
{
}
}
}
}