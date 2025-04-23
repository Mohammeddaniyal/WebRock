package com.thinking.machines.webrock;
import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.reflect.*;
import java.io.*;
import com.thinking.machines.webrock.pojo.*;
import com.thinking.machines.webrock.model.*;
import com.thinking.machines.webrock.annotations.*;
import java.util.logging.*;
public class TMWebRockStarter extends HttpServlet
{
private static final Logger logger = Logger.getLogger(TMWebRockStarter.class.getName());

    static {
        try {
            // Setup the file handler for logging
            FileHandler fileHandler = new FileHandler("_tmwebrock.log", true); // appends to log file
            fileHandler.setFormatter(new SimpleFormatter()); // Simple formatter for log messages
            logger.addHandler(fileHandler); // Add the handler to the logger
        } catch (IOException e) {
            e.printStackTrace(); // If logging setup fails, print the stack trace
        }
    }


public void init() throws ServletException
{
super.init();
System.out.println("hi");
WebRockModel webRockModel=new WebRockModel();
getServletContext().setAttribute("webRockModel",webRockModel);
getServletContext().setAttribute("hello","How are you");

String rootFolderName=getServletContext().getInitParameter("SERVICE_PACKAGE_PREFIX");
if(rootFolderName==null)
{
logger.severe("No root folder specified");
throw new ServletException("No root folder specified");
}
String absolutePath=getServletContext().getRealPath("/WEB-INF/classes/"+rootFolderName);
if(absolutePath==null)
{
logger.severe("Root folder not found in web app");
throw new ServletException("Root folder not found in web app");
}

File rootFolder=new File(absolutePath);
if(!rootFolder.exists() || !rootFolder.isDirectory())
{
           logger.severe("Specified root is not a valid directory: " + absolutePath);
throw new ServletException("Specified root is not a valid directory: "+absolutePath);
}
loadServices(rootFolder);
}
private void loadServices(File folder) throws ServletException
{
if(folder==null || !folder.exists()) return;

File files[]=folder.listFiles();
for(File file:files)
{
if(file.isDirectory())
{
loadServices(file);
}
else
{
if(!file.getName().endsWith(".class")) continue;
System.out.println("Found file: " + file.getAbsolutePath());
Class serviceClass=getServiceClass(file);
if(serviceClass==null) continue;
WebRockModel webRockModel=(WebRockModel)getServletContext().getAttribute("webRockModel");

Path path=(Path)serviceClass.getAnnotation(Path.class);
if(path==null) continue;

boolean isGetAllowed=serviceClass.isAnnotationPresent(GET.class);

boolean isPostAllowed=serviceClass.isAnnotationPresent(POST.class);


Method []methods=serviceClass.getMethods();
for(Method method:methods)
{
Path p=method.getAnnotation(Path.class);
if(p==null) continue;
boolean isGetAllowed=method
Forward forward=method.getAnnotation(Forward.class);

Service service=new Service();
service.setServiceClass(serviceClass);
service.setService(method);
service.setPath(path.value()+p.value());
if(forward!=null) service.setForwardTo(forward.value());
logger.info("Path : " + path.value() + p.value());
System.out.println("Path : "+path.value()+p.value());
webRockModel.putService(service);
}
}
}

}
private Class getServiceClass(File file) throws ServletException
{
try
{
String absolutePath=file.getAbsolutePath();
String rootFolderName=getServletContext().getInitParameter("SERVICE_PACKAGE_PREFIX");
int index=absolutePath.indexOf(rootFolderName+"\\");
if(index==-1)
{
logger.severe("Root folder not found in path : " + rootFolderName + " , " + absolutePath);
throw new ServletException("Root folder not found in path : "+rootFolderName+" , "+absolutePath);
}
//c:\tomcat9\webapps\dan\WEB-INF\classes\booby\com...
// +1 for backslash
//index+=rootFolderName.length()+1;
String packageName=absolutePath.substring(index).replace("\\",".");
packageName=packageName.substring(0,packageName.lastIndexOf("."));
 logger.info("packageName : " + packageName);
System.out.println("packageName : "+packageName);
return Class.forName(packageName);
}catch(Exception e)
{
            logger.severe("Error loading class: " + e.getMessage());
return null;
}
}
}