package booby.test;
import com.thinking.machines.webrock.annotations.*;
@Path("/calculator")
public class Calculator 
{
@Path("/add")
public int add(int e,int f)
{
return e+f;
}
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
@Path("/div")
public int divide(int e,int f)
{
return e/f;
}
}
