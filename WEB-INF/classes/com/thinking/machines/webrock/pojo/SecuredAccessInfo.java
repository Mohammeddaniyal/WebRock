package com.thinking.machines.webrock.pojo;

import java.lang.reflect.Method;

public class SecuredAccessInfo {
    private Class<?> clazz;
    private Method method;
    public SecuredAccessInfo()
    {
        this.clazz=null;
        this.method=null;
    }
    public void setClazz(Class<?> clazz)
    {
        this.clazz=clazz;
    }
    public void setMethod(Method method)
    {
        this.method=method;
    }
    public Class<?> getClazz()
    {
        return this.clazz;
    }
    public Method getMethod()
    {
        return this.method;
    }
}
