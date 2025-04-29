package booby.test;

import com.thinking.machines.webrock.annotations.InjectSessionScope;
import com.thinking.machines.webrock.annotations.Path;
import com.thinking.machines.webrock.annotations.Forward;
import com.thinking.machines.webrock.scopes.SessionScope;

@InjectSessionScope
@Path("/AutowiredTest")
public class AutowiredTest {
    private SessionScope session;
    public void setSessionScope(SessionScope session)
    {
        System.out.println("Setting session scope");
        this.session=session;
    }
    @Path("/sessionTest")
    @Forward("/calculator/greet")
    public String setStudentInSession()
    {
        System.out.println("HELLOOOOOOOOOOOO!!!");
        Student s=new Student(100,"Daniyal Ali");
        System.out.println("hi");
        System.out.println(session);
        session.setAttribute("std", s);
        System.out.println("LOOW");
        return "This is the student data that i setted in session";
    }
}
