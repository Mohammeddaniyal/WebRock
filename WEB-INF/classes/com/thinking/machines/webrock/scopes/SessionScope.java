package com.thinking.machines.webrock.scopes;

public class SessionScope {
    private HttpSession session;
    public void setAttribute(String name,Object object)
    {
        session.setAttribute(name,object);
    }
    public Object getAttribute(String name);
    {
        return session.getAttribute(name);
    }
}
