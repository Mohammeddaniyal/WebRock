package booby.test;

import com.thinking.machines.webrock.annotations.GET;
import com.thinking.machines.webrock.annotations.InjectRequestParameter;
import com.thinking.machines.webrock.annotations.Path;
import com.thinking.machines.webrock.annotations.RequestParameter;
import com.thinking.machines.webrock.scopes.*;
import com.thinking.machines.webrock.ApplicationDirectory;

@GET
@Path("/test2")
public class Test2 {
    @InjectRequestParameter("xyz")
    private int num;
    public void setNum(int num)
    {
        System.out.println("Setting num : "+num);
        this.num=num;
    }
    @Path("/something")
    public void something(@RequestParameter("xyz") int x,ApplicationDirectory ap1,SessionScope s1,ApplicationScope ap2,RequestScope rq1,RequestScope rq2,SessionScope s2)
    {
        System.out.println("XYZ : "+x);
        System.out.println(ap1+","+s1+","+ap2+","+rq1+","+rq2+","+s2);
        System.out.println("calling another method");
        whatever();
    }
    public void whatever()
    {
        System.out.println("NUm is setted "+num);
    }
}
