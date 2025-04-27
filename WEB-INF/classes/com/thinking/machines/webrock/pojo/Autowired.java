package com.thinking.machines.webrock.pojo;

import java.lang.reflect.*;
public class Autowired {
    private Field autowiredField;
    private Method setterMethod;
    public Autowired(Field autowiredField,Method setterMethod)
    {
        this.autowiredField=autowiredField;
        this.setterMethod=setterMethod;
    }
    public void setAutowiredField(Field autowirField)
    {
        this.autowiredField=autowirField;
    }
    public void setSetterMethod(Method setterMethod)
    {
        this.setterMethod=setterMethod;
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
