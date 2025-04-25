
package booby.test;
import java.lang.reflect.Method;

import com.thinking.machines.webrock.annotations.*;
@POST
@Path("/calculator")
public class Calculator 
{
@GET
@Path("/add")
@Forward("/calculator/forward")
public int add(int e,int f)
{
   for (Method method : Calculator.class.getDeclaredMethods()) {
    System.out.println("Method: " + method.getName());
}

    System.out.println("arrived for add");
return e+f;
}
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
@GET
@Path("/forward")
public void forwardTo(int a,int b)
{
    System.out.println("Request arrived by forwarding : "+a+","+b);
}
@OnStartUp(priority = 1)
public void hello()
{
   System.out.println("HELLO HI EVERYONE");
}
@OnStartUp(priority = 3)
public void hell()
{
   System.out.println("HELL YEAH");
}
@Forward("/calculator/forward")
@OnStartUp(priority = 2)
public void greet()
{
   System.out.println("Greetings");
}
}
