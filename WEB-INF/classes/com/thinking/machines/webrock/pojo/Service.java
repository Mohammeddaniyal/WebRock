package com.thinking.machines.webrock.pojo;

import java.lang.reflect.*;
import java.util.List;

public class Service {
    private Class serviceClass;
    private String path;
    private Method service;
    private boolean isGetAllowed;
    private boolean isPostAllowed;
    private List<RequestParameterInfo> requestParameterInfoList;
    private String forwardTo;
    private boolean runOnStart;
    private int priority;
    private boolean injectSessionScope;
    private boolean injectApplicationScope;
    private boolean injectRequestScope;
    private boolean injectApplicationDirectory;
    private List<AutowiredInfo> autowiredList;
    private List<RequestParameterFieldInfo> requestParameterFieldInfoList;
    private SecuredAccessInfo securedAccessInfo;

    public Service() {
        this.serviceClass = null;
        this.path = "";
        this.service = null;
        this.forwardTo = null;
        this.isGetAllowed = false;
        this.isPostAllowed = false;
        this.requestParameterInfoList=null;
        this.runOnStart = false;
        this.priority = 0;
        this.injectSessionScope=false;
        this.injectApplicationScope=false;
        this.injectRequestScope=false;
        this.injectApplicationDirectory=false;
        this.autowiredList=null;
        this.requestParameterFieldInfoList=null;
        this.securedAccessInfo=null;
    }

    public void setServiceClass(java.lang.Class serviceClass) {
        this.serviceClass = serviceClass;
    }

    public void setService(java.lang.reflect.Method service) {
        this.service = service;
    }

    public void setPath(java.lang.String path) {
        this.path = path;
    }

    public void setIsGetAllowed(boolean isGetAllowed) {
        this.isGetAllowed = isGetAllowed;
    }

    public void setIsPostAllowed(boolean isPostAllowed) {
        this.isPostAllowed = isPostAllowed;
    }

    public void setRequestParameterInfoList(List<RequestParameterInfo> requestParameterInfoList)
    {
        this.requestParameterInfoList=requestParameterInfoList;
    }

    public void setForwardTo(String forwardTo) {
        this.forwardTo = forwardTo;
    }

    public void setRunOnStart(boolean runOnStart) {
        this.runOnStart = runOnStart;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setInjectSessionScope(boolean injectSessionScope)
    {
        this.injectSessionScope=injectSessionScope;
    }

    public void setInjectApplicationScope(boolean injectApplicationScope)
    {
        this.injectApplicationScope=injectApplicationScope;
    }

    public void setInjectRequestScope(boolean injectRequestScope)
    {
        this.injectRequestScope=injectRequestScope;
    }

    public void setInjectApplicationDirectory(boolean injectApplicationDirectory)
    {
        this.injectApplicationDirectory=injectApplicationDirectory;
    }

    public void setAutowiredList(List<AutowiredInfo> autowiredList)
    {
        this.autowiredList=autowiredList;
    }

    public void setRequestParameterFieldInfoList(List<RequestParameterFieldInfo> requestParameterFieldInfoList)
    {
        this.requestParameterFieldInfoList=requestParameterFieldInfoList;
    }

    public void setSecuredAccessInfo(SecuredAccessInfo securedAccessInfo)
    {
        this.securedAccessInfo=securedAccessInfo;
    }

    public java.lang.String getPath() {
        return this.path;
    }

    public java.lang.Class getServiceClass() {
        return this.serviceClass;
    }

    public java.lang.reflect.Method getService() {
        return this.service;
    }

    public boolean isGetAllowed() {
        return this.isGetAllowed;
    }

    public boolean isPostAllowed() {
        return this.isPostAllowed;
    }

    public List<RequestParameterInfo> getRequestParameterInfoList()
    {
        return this.requestParameterInfoList;
    }

    public String getForwardTo() {
        return this.forwardTo;
    }

    public boolean getRunOnStart() {
        return this.runOnStart;
    }

    public int getPriority() {
        return this.priority;
    }

    public boolean getInjectSessionScope()
    {
        return this.injectSessionScope;
    }

    public boolean getInjectApplicationScope()
    {
        return this.injectApplicationScope;
    }

    public boolean getInjectRequestScope()
    {
        return this.injectRequestScope;
    }

    public boolean getInjectApplicationDirectory()
    {
        return this.injectApplicationDirectory;
    }

    public List<AutowiredInfo> getAutowiredList()
    {
        return this.autowiredList;
    }

    public List<RequestParameterFieldInfo> getRequestParameterFieldInfoList()
    {
        return this.requestParameterFieldInfoList;
    }

    public SecuredAccessInfo getSecuredAccessInfo()
    {
        return this.securedAccessInfo;
    }
}