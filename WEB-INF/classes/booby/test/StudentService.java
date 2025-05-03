package booby.test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.naming.spi.DirStateFactory.Result;

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
        System.out.println("Student arrived : ");
        System.out.println(student.getRollNumber()+","+student.getName());
        if(student==null) return;
        int rollNumber=student.getRollNumber();
        if(rollNumber<=0) return;
        String name=student.getName();
        if(name==null) return;
        name=name.trim();
        if(name.length()==0)return;
        try{
            Connection connection=DAOConnection.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement("select roll_number from student where name=?");
            preparedStatement.setString(1,name);
            ResultSet resultSet=preparedStatement.executeQuery();
            if(resultSet.next())
            {
                resultSet.close();
                preparedStatement.close();
                connection.close();
                System.out.println("Already exists");
                return;
            }
            resultSet.close();
            preparedStatement.close();
            preparedStatement=connection.prepareStatement("insert into student (roll_number,name) values(?,?)");
            preparedStatement.setInt(1,rollNumber);
            preparedStatement.setString(2,name);
            preparedStatement.executeUpdate();
            System.out.println("Student added");
            preparedStatement.close();
            connection.close();
        }
         catch (SQLException e) {
            // TODO: handle exception
        }
    }
    @Path("/update")
    @POST
    public void update(Student student)
    {
        System.out.println("Update arrived");
        System.out.println("Student "+student);
        System.out.println(student.getRollNumber()+","+student.getName());
        if(student==null) 
        {
            System.out.println("NULL");
            return;
        }    
        int rollNumber=student.getRollNumber();
        if(rollNumber<=0) {
            System.out.println("Invalid roll number");
            return;
        }    String name=student.getName();
        if(name==null) 
        {
         System.out.println("Name is null");
            return;
        }
            name=name.trim();
        if(name.length()==0)
        {
            System.out.println("Name length is zero");
            return;
        }
        try{
            Connection connection=DAOConnection.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement("select roll_number from student where roll_number=?");
            preparedStatement.setInt(1,rollNumber);
            ResultSet resultSet=preparedStatement.executeQuery();
            if(!resultSet.next())
            {
                resultSet.close();
                preparedStatement.close();
                connection.close();
                System.out.println("(Update)Not exists");
                return;
            }
            resultSet.close();
            preparedStatement.close();

            preparedStatement=connection.prepareStatement("select roll_number from student where name=? and roll_number<>?");
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, rollNumber);

            resultSet=preparedStatement.executeQuery();
            if(resultSet.next())
            {
                resultSet.close();
                preparedStatement.close();
                connection.close();
                System.out.println("Already exists(update)");
                return;
            }
            resultSet.close();
            preparedStatement.close();
            preparedStatement=connection.prepareStatement("update student set name=? where roll_number=?");
            preparedStatement.setString(1,name);
            preparedStatement.setInt(2, rollNumber);
            preparedStatement.executeUpdate();
            System.out.println("Student Updated");
            preparedStatement.close();
            connection.close();
            
        } catch (SQLException e) {
            // TODO: handle exception
            System.out.println("SqlException"+e);
        }
        
    }
    @Path("/getByRollNumber")
    @GET
    public Student getByRollNumber(@RequestParameter("rollNumber") int rollNumber)
    {
        Student student=null;
        if(rollNumber<=0) return student;

        try{
            Connection connection=DAOConnection.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement("select * from student where roll_number=?");
            preparedStatement.setInt(1, rollNumber);
            ResultSet resultSet=preparedStatement.executeQuery();
            if(resultSet.next())
            {
              
                rollNumber = resultSet.getInt("roll_number");
                String name = resultSet.getString("name").trim();
                student=new Student(rollNumber, name);
                resultSet.close();
                preparedStatement.close();
                connection.close();
                System.out.println(" found");
                return student;
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
            System.out.println("Not foudn");
            return student;
        
        } catch (SQLException e) {
            System.out.println("Get By rollnumber "+e);
            // TODO: handle exception
        }
        return student;
    }
    @Path("/getAll")
    @GET
    public List<Student> getAll()
    {
        List<Student> students=new ArrayList<>();
        Student student;
        int rollNumber;
        String name;
        try
        {
            Connection connection=DAOConnection.getConnection();
            Statement statement=connection.createStatement();
            ResultSet resultSet=statement.executeQuery("select * from student");
            while(resultSet.next())
            {
                rollNumber=resultSet.getInt("roll_number");
                name=resultSet.getString("name").trim();
                student=new Student(rollNumber, name);
                students.add(student);
            }
        }catch(SQLException e)
        {
            System.out.println("Exception getAll "+e);
        }
        return students;
    }
    @Path("/delete")
    @POST
    public void delete(int rollNumber)
    {
        if(rollNumber<=0)
        {
            System.out.println("Invalid roll number");
            return;
        }
        try
        {
            Connection connection=DAOConnection.getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement("select roll_number from student where roll_number=?");
            preparedStatement.setInt(1,rollNumber);
            ResultSet resultSet=preparedStatement.executeQuery();
            if(!resultSet.next())
            {
                resultSet.close();
                preparedStatement.close();
                connection.close();
                System.out.println("not found");
                return;
            }
            resultSet.close();
            preparedStatement.close();
            preparedStatement=connection.prepareStatement("delete from student where roll_number=?");
            preparedStatement.setInt(1,rollNumber);
            preparedStatement.executeUpdate();
            System.out.println("DELETED");
            resultSet.close();
            preparedStatement.close();
            connection.close();
        }catch(SQLException e)
        {
            System.out.println("Delete Exception : "+e);
        }
    }
}
