package com.thinking.machines.webrock.pojo;

public class RequestParameterInfo {
    private Class<?> parameterClass;
    private String name;
    public RequestParameterInfo(Class<?> parameterClass,String name)
    {
        this.parameterClass=parameterClass;
        this.name=name;
    }
    public void setParameterClass(Class<?> parameterClass)
    {
        this.parameterClass=parameterClass;
    }
    public void setName(String name)
    {
        this.name=name;
    }
    public Class<?> getParameterClass()
    {
        return this.parameterClass;
    }
    public String getName()
    {
        return this.name;
    }
}
