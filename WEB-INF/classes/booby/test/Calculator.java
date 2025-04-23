package booby.test;
import com.thinking.machines.webrock.annotations.*;

@Path("/calculator")
public class Calculator 
{
@GET
@Path("/add")
@Forward("/calculator/sub")
public int add(int e,int f)
{
return e+f;
}
@POST
@Path("/sub")
public int subtract(int e,int f)
{
return e-f;
}
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
}
