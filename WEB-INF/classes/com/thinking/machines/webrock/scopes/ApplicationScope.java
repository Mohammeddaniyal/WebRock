package com.thinking.machines.webrock.scopes;
import javax.servlet.*;
public class ApplicationScope {
    private ServletContext servletContext;
    public ApplicationScope()
    {
        this.servletContext=getServletContext();
    }
    public void setAttribute(String name,Object object)
    {
        servletContext.setAttribute(name,object);
    }
    public Object getAttribute(String name);
    {
        return servletContext.getAttribute(name);
    }
}
