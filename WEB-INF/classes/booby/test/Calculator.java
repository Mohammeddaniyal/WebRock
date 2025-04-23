package booby.test;
import com.thinking.machines.webrock.annotations.*;

@Path("/calculator")
public class Calculator 
{
@GET
@Path("/add")
@Forward("/calculator/forward")
public int add(int e,int f)
{
    System.out.println("arrived for add");
return e+f;
}
@POST
@Path("/sub")
public int subtract(int e,int f)
{
return e-f;
}
@Forward("/test.jsp")
@Path("/mul")
public int multiply(int e,int f)
{
return e*f;
}
@GET
@Path("/div")
public int divide(int e,int f)
{
return e/f;
}
@Path("/forward")
public void forwardTo(int a,int b)
{
    System.out.println("Request arrived by forwarding : "+a+","+b);
}
}
