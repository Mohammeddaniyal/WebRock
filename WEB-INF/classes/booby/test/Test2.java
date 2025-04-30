package booby.test;

import com.thinking.machines.webrock.annotations.GET;
import com.thinking.machines.webrock.annotations.Path;
import com.thinking.machines.webrock.annotations.RequestParameter;
import com.thinking.machines.webrock.scopes.*;
import com.thinking.machines.webrock.ApplicationDirectory;

@GET
@Path("/test2")
public class Test2 {
    @Path("/something")
    public void something(@RequestParameter("xyz") int x,ApplicationDirectory ap1,SessionScope s1,ApplicationScope ap2,RequestScope rq1,RequestScope rq2,SessionScope s2,int y)
    {
        System.out.println("XYZ : "+x);
        System.out.println(ap1+","+s1+","+ap2+","+rq1+","+rq2+","+s2);
    }
}
