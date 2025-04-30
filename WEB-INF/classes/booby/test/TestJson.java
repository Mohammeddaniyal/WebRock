package booby.test;


import com.thinking.machines.webrock.annotations.POST;
import com.thinking.machines.webrock.annotations.Path;


@POST
@Path("/testJson")
public class TestJson {
    @Path("/testStudent")
    public void testStudent(Student student)
    {
        System.out.println("test student "+student);
        System.out.println("Roll no. : "+student.getRollNumber()+" ,Name : "+student.getName());
    }
}

