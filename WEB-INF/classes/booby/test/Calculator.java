
package booby.test;
import java.lang.reflect.Method;

import com.thinking.machines.webrock.ApplicationDirectory;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.scopes.ApplicationScope;
import com.thinking.machines.webrock.scopes.RequestScope;
@GET
@InjectRequestScope
@InjectApplicationScope
@InjectApplicationDirectory
@Path("/calculator")
public class Calculator 
{
   @Autowired(name = "std")
   private Student student;
   private RequestScope requestScope;
   private ApplicationScope applicationScope;
   private ApplicationDirectory applicationDirectory;
   public void setStudent(Student student)
   {
      System.out.println("STUDENT "+student);
      this.student=student;
      System.out.println("Student Data ");
    //  System.out.println("Roll number : "+student.getRollNumber()+", Name : "+student.getName());
   }
   public void setRequestScope(RequestScope requestScope)
   {
      System.out.println("Setting requestScope " +requestScope);
      this.requestScope=requestScope;
   }
   public void setApplicationDirectory(ApplicationDirectory applicationDirectory)
   {
      System.out.println("Setting Application directory, the real path is : "+applicationDirectory.getDirectory().getAbsolutePath());
      this.applicationDirectory=applicationDirectory;
   }
   public void setApplicationScope(ApplicationScope applicationScope)
   {
      System.out.println("Setting application scope " + applicationScope);
      this.applicationScope=applicationScope;
   }
@GET
@Path("/add")
@Forward("/calculator/result")
public int add(@RequestParameter("a") int e,@RequestParameter("b") int f)
{
   for (Method method : Calculator.class.getDeclaredMethods()) {
    System.out.println("Method: " + method.getName());
}

    System.out.println("arrived for add");
return e+f;
}
@Path("/sub")
public int subtract(@RequestParameter("a") int e,@RequestParameter("b") int f)
{
return e-f;
}
@Forward("/test.jsp")
@Path("/mul")
public int multiply(@RequestParameter("a") int e,@RequestParameter("b") int f)
{
return e*f;
}
@GET
@Path("/div")
public int divide(@RequestParameter("a") int e,@RequestParameter("b") int f)
{
return e/f;
}
@GET
@Path("/forward")
public void forwardTo(@RequestParameter("a") int e,@RequestParameter("b") int f)
{
    System.out.println("Request arrived by forwarding : "+e+","+f);
}
@Path("/hello")
@Forward("/test.jsp")
public void hello()
{
   System.out.println("HELLO HI EVERYONE");
}
@Path("/hell")
@Forward("/calculator/hello")
public void hell()
{
   System.out.println("HELL YEAH");
}
@Path("/greet")
@Forward("/test1/eg1")
public String greet(@RequestParameter("msg") String messageString)
{
   System.out.println(messageString);
   System.out.println("Greetings");
   Course course=new Course(1, "Java");
   System.out.println("Setting course into request scope");
   requestScope.setAttribute("crs", course);
   System.out.println(student.getRollNumber()+student.getName());
   return "Hey i'm greet i've setted the course into the request scope";
}
@GET
@Path("/result")
@Forward("/calculator/greet")
public void showResult(@RequestParameter("result") int result)
{
   System.out.println("The request is forwarded to showResult and result is "+result);
}
@GET
@Forward("/calculator/studentTest")
@Path("/addStudent")
public Student addStudent()
{
   System.out.println("Add Student got invoked");
Student s=new Student(101,"Daniyal Ali");
System.out.println("Student created "+s);
return s;
}

@GET
@Path("/studentTest")
public void studentTest(@RequestParameter("s") String s)
{
   System.out.println("Student arrived");
   //System.out.println(s.getRollNumber()+","+s.getName());
}
}
