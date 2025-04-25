package com.thinking.machines.webrock.scopes;
import javax.servlet.*;
public class ApplicationScope {
    private ServletContext servletContext;
    public ApplicationScope()
    {
        this.servletContext=getServletContext();
    }
    public void setAttribute()
    {

    }
    public Object getAttribute()
    {
        
    }
}
