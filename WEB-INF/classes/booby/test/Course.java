package booby.test;

public class Course {
    private int code;
    private String name;
    public Course(int code,String name)
    {
        this.code=code;
        this.name=name;
    }
    public void setCode(int code)
    {
        this.code=code;
    }
    public void setName(String name)
    {
        this.name=name;
    }
    public int getCode()
    {
        return this.code;
    }
    public String getName()
    {
        return this.name;
    }
}

