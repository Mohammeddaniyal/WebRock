package com.thinking.machines.webrock.pojo;

import java.lang.reflect.Method;

public class RequestParameterFieldInfo {
    private Class<?> fieldClass;
    private Method setterMethod;
    private String name;
    public RequestParameterFieldInfo()
    {
        this.setterMethod=null;
        this.name="";
    }
    public RequestParameterFieldInfo(Class<?> fieldClass,Method setterMethod,String name)
    {
        this.fieldClass=fieldClass;
        this.setterMethod=setterMethod;
        this.name=name;
    }
    public void setFieldClass(Class<?> fieldClass)
    {
        this.fieldClass=fieldClass;
    }
    public void setSetterMethod(Method setterMethod)
    {
        this.setterMethod=setterMethod;
    }
    public void setName(String name)
    {
        this.name=name;
    }
    public Class<?> getFieldClass()
    {
        return this.fieldClass;
    }
    public Method getSetterMethod()
    {
        return this.setterMethod;
    }
    public String getName()
    {
        return this.name;
    }
}
