package booby.test;

import com.thinking.machines.webrock.annotations.GET;
import com.thinking.machines.webrock.annotations.Path;
import com.thinking.machines.webrock.annotations.RequestParameter;

@GET
@Path("/TestQueryString")
public class TestQueryString {

    @Path("/handleAll")
    public void handleAllTypes(
        @RequestParameter("b") byte b,
        @RequestParameter("s") short s,
        @RequestParameter("i") int i,
        @RequestParameter("l") long l,
        @RequestParameter("f") float f,
        @RequestParameter("d") double d,
        @RequestParameter("c") char c,
        @RequestParameter("flag") boolean flag
    ) {
        System.out.println("All primitives handled:");
        System.out.println("byte: " + b);
        System.out.println("short: " + s);
        System.out.println("int: " + i);
        System.out.println("long: " + l);
        System.out.println("float: " + f);
        System.out.println("double: " + d);
        System.out.println("char: " + c);
        System.out.println("boolean: " + flag);
    }

    @Path("/handleNum")
    public void handleSomeNumericTypes(
        @RequestParameter("id") int id,
        @RequestParameter("rate") float rate,
        @RequestParameter("count") short count
    ) {
        System.out.println("Some numeric types handled:");
        System.out.println("int id: " + id);
        System.out.println("float rate: " + rate);
        System.out.println("short count: " + count);
    }

    @Path("/handleCharAndBoolean")
    public void handleCharAndBoolean(
        @RequestParameter("gender") char gender,
        @RequestParameter("active") boolean isActive
    ) {
        System.out.println("Char and Boolean handled:");
        System.out.println("char gender: " + gender);
        System.out.println("boolean active: " + isActive);
    }

    @Path("/handleSmallTypes")
    public void handleSmallTypes(
        @RequestParameter("smallByte") byte smallByte,
        @RequestParameter("smallShort") short smallShort
    ) {
        System.out.println("Small data types handled:");
        System.out.println("byte smallByte: " + smallByte);
        System.out.println("short smallShort: " + smallShort);
    }

    @Path("/handlDoubleCombo")
    public void handleLongDoubleCombo(
        @RequestParameter("timestamp") long timestamp,
        @RequestParameter("value") double value
    ) {
        System.out.println("Long and Double handled:");
        System.out.println("long timestamp: " + timestamp);
        System.out.println("double value: " + value);
    }

    @Path("/handleJustOne")
    public void handleJustOne(
        @RequestParameter("flag") boolean flag
    ) {
        System.out.println("Single boolean parameter handled:");
        System.out.println("boolean flag: " + flag);
    }
}
