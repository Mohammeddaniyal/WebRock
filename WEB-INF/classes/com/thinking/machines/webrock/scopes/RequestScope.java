package com.thinking.machines.webrock.scopes;

import javax.servlet.http.HttpServletRequest;
public class RequestScope {
    HttpServletRequest request;
    public void setAttribute(String name,Object object)
    {
        request.setAttribute(name,object);
    }
    public Object getAttribute(String name)
    {
        return request.getAttribute(name);
    
    }
}