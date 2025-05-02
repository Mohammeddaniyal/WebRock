package booby.test;

import java.sql.*;
import java.util.List;
import com.thinking.machines.webrock.annotations.GET;
import com.thinking.machines.webrock.annotations.POST;
import com.thinking.machines.webrock.annotations.Path;
import com.thinking.machines.webrock.annotations.RequestParameter;

@Path("/studentService")
public class StudentService {
    @Path("/add")
    @POST
    public void add(Student student)
    {
        try {
        
        } catch (SQLException e) {
            // TODO: handle exception
        }
    }
    @Path("/update")
    @POST
    public void update(Student student)
    {
        try {
            
        } catch (SQLException e) {
            // TODO: handle exception
        }
    }
    @Path("/getByCode")
    @GET
    public Student getByRollNumber(@RequestParameter("rollNumber") int code)
    {
        try {
            
        } catch (SQLException e) {
            // TODO: handle exception
        }
        return null;
    }
    @Path("/getAll")
    @GET
    public List<Student> getAll()
    {
        try
        {

        }catch(SQLException e)
        {
            
        }
        return null;
    }
}
