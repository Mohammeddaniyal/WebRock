package com.thinking.machines.webrock.model;
import com.thinking.machines.webrock.pojo.*;
import java.util.*;
public class WebRockModel
{
private Map<String,Service> services;
public WebRockModel()
{
this.services=new HashMap<>();
}
public void putService(Service service)
{
this.services.put(service.getPath(),service);
}
public Service getService(String path)
{
return this.services.get(path);
}
public void print()
{
 for (Map.Entry<String, Service> entry : services.entrySet()) {
                String key = entry.getKey();
                Service service = entry.getValue();

                // Print the key (service name or identifier)
                System.out.println("Service Key: " + key);

                // Print serviceClass, serviceMethod, and servicePath
                System.out.println("  Service Class: " + service.getServiceClass().getName());
                System.out.println("  Service Method: " + service.getService().getName());
                System.out.println("  Service Path: " + service.getPath());
                System.out.println("  Service Forward To : "+service.getForwardTo());
                System.out.println("  Service isGetAllowed : "+service.isGetAllowed());
                System.out.println("  Service isPostAllowed : "+service.isPostAllowed());
                System.out.println("-----------------------------");
            }
}
}
