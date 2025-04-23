package com.thinking.machines.webrock.pojo;
import java.lang.reflect.*;
public class Service
{
private Class serviceClass;
private  String path;
private Method service;
private boolean isGetAllowed;
private boolean isPostAllowed;
private String forwardTo;
public Service()
{
this.serviceClass=null;
this.path="";
this.service=null;
this.forwardTo=null;
}
public void setServiceClass(java.lang.Class serviceClass)
{
this.serviceClass=serviceClass;
}
public java.lang.Class getServiceClass()
{
return this.serviceClass;
}
public void setPath(java.lang.String path)
{
this.path=path;
}
public void setIsGetAllowed(boolean isGetAllowed)
{
    this.isGetAllowed=isGetAllowed;
}
public void setIsPostAllowed(boolean isPostAllowed)
{
    this.isPostAllowed=isPostAllowed;
}
public void setForwardTo(String forwardTo)
{
    this.forwardTo=forwardTo;
}
public java.lang.String getPath()
{
return this.path;
}
public void setService(java.lang.reflect.Method service)
{
this.service=service;
}
public java.lang.reflect.Method getService()
{
return this.service;
}
public boolean isGetAllowed()
{
    return this.isGetAllowed;
}
public 
public String getForwardTo()
{
    return this.forwardTo;
}
}