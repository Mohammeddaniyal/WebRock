package com.thinking.machines.webrock.pojo;

public class RequestParameterFieldInfo {
    private Method setterMethod;
    private String name;
    public RequestParameterFieldInfo()
    {
        this.setterMethod=null;
        this.name="";
    }
    public RequestParameterFieldInfo(Method setterMethod,String name)
    {
        this.setterMethod=setterMethod;
        this.name=name;
    }
    public void setSetterMethod(Method setterMethod)
    {
        this.setterMethod=setterMethod;
    }
    public void setName(String name)
    {
        this.name=name;
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
