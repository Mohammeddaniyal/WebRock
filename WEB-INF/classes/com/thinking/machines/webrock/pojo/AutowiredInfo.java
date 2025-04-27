package com.thinking.machines.webrock.pojo;

import java.lang.reflect.*;
public class AutowiredInfo {
    private String beanName;
    private Field autowiredField;
    private Method setterMethod;
    public AutowiredInfo(String beanName,Field autowiredField,Method setterMethod)
    {
        this.beanName=beanName;
        this.autowiredField=autowiredField;
        this.setterMethod=setterMethod;
    }
    public void setBeanName(String beanName)
    {
        this.beanName=beanName;
    }
    public void setAutowiredField(Field autowirField)
    {
        this.autowiredField=autowirField;
    }
    public void setSetterMethod(Method setterMethod)
    {
        this.setterMethod=setterMethod;
    }
    public String getBeanName()
    {
        return this.beanName;
    }
    public Field getAutowiredField()
    {
        return this.autowiredField;
    }
    public Method getSetterMethod()
    {
        return this.setterMethod;
    }
    
}
