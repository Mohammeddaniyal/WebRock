package com.thinking.machines.webrock.scopes;

import javax.servlet.http.HttpSession;
public class SessionScope {
    private HttpSession session;

    public SessionScope(HttpSession session)
    {
        this.session=session;
    }
    public void setAttribute(String name,Object object)
    {
        session.setAttribute(name,object);
    }
    public Object getAttribute(String name)
    {
        return session.getAttribute(name);
    }
}
