class Student{
    constructor(rollNumber,name)
    {
        this.rollNumber=rollNumber;
        this.name=name;
    }
}
class StudentService{
    add(student)
    {
        var prm=new Promise(function(done,problem){
            $.ajax({
                type:"POST",
                url:"services/studentService/add",
                data: JSON.stringify(student),
                success: function(data)
                {
                    alert('Added successfully');
                },
                failure: function()
                {
                    alert('Failed');
                },
                dataType:"json"
            });
        });
        return prm;
    }
    update(student)
    {
        var prm=new Promise(function(done,problem){
            $.ajax({
                type:"POST",
                url:"services/studentService/update",
                data: JSON.stringify(student),
                success: function(data)
                {
                    alert('Updated successfully');
                },
                failure: function()
                {
                    alert('Failed');
                },
                dataType:"json"
            });
        });
        return prm;
    }
    delete(rollNumber)
    {
        var prm=new Promise(function(done,problem){
            $.ajax({
                type:"POST",
                url:"services/studentService/delete",
                data: JSON.stringify(rollNumber),
                success: function(data)
                {
                    alert('Deleted successfully');
                },
                failure: function()
                {
                    alert('Failed');
                },
                dataType:"json"
            });
        });
        return prm;
    }
    getByRollNumber(rollNumber)
    {
        var prm=new Promise(function(done,problem){
            $.ajax({
                url:"services/studentService/getByRollNumber",
                data:{"rollNumber":rollNumber},
                success:function(data)
                {
                    alert(data);
                    var student=data;
                    alert(student.rollNumber);
                    alert(student.name);
                    
                },
                failure:function()
                {
                    alert("Failed");
                }
            });
        });
        return prm;
    }
    
    getAll()
    {
        var prm=new Promise(function(done,problem){
            $.get('services/studentService/getAll',function(data){
                done(data);
            });
        });
        return prm;
    
    }
}