package booby.test;

import com.thinking.machines.webrock.ApplicationDirectory;
import com.thinking.machines.webrock.annotations.Autowired;
import com.thinking.machines.webrock.annotations.Forward;
import com.thinking.machines.webrock.annotations.GET;
import com.thinking.machines.webrock.annotations.InjectApplicationDirectory;
import com.thinking.machines.webrock.annotations.InjectSessionScope;
import com.thinking.machines.webrock.annotations.Path;
import com.thinking.machines.webrock.scopes.SessionScope;

@Path("/test1")
@GET
@InjectSessionScope
@InjectApplicationDirectory
public class Test1 {
    @Autowired(name = "crs")
    private Course course;
     private SessionScope session;
     private ApplicationDirectory applicationDirectory;
    public void setCourse(Course course)
    {
        System.out.println("Setting the course "+course);
        this.course=course;
        System.out.println("Code "+course.getCode()+", Name "+course.getName());
    }
     public void setSessionScope(SessionScope session)
    {
        System.out.println("Setting session scope "+session);
        this.session=session;
    }
    public void setApplicationDirectory(ApplicationDirectory applicationDirectory)
    {   
        System.out.println("Setting application directory "+applicationDirectory);
        this.applicationDirectory=applicationDirectory;
    }
    @Path("/eg1")
    @Forward("/calculator/hell")
    public void eg1(String messageString)
    {
        System.out.println(messageString);
        System.out.println("hi "+course);
        System.out.println("Code "+course.getCode()+", Name "+course.getName());

        System.out.println("NIce");
    }
}
