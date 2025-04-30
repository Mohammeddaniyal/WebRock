package booby.test;

import com.thinking.machines.webrock.annotations.GET;
import com.thinking.machines.webrock.annotations.Path;
import com.thinking.machines.webrock.annotations.RequestParameter;
import com.thinking.machines.webrock.annotations.InjectRequestParameter;

@GET
@Path("/Test3")
public class Test3 {

    @InjectRequestParameter("b")
    private byte b;
    public void setB(byte b) {
        System.out.println("Setting byte: " + b);
        this.b = b;
    }

    @InjectRequestParameter("s")
    private short s;
    public void setS(short s) {
        System.out.println("Setting short: " + s);
        this.s = s;
    }

    @InjectRequestParameter("i")
    private int i;
    public void setI(int i) {
        System.out.println("Setting int: " + i);
        this.i = i;
    }

    @InjectRequestParameter("l")
    private long l;
    public void setL(long l) {
        System.out.println("Setting long: " + l);
        this.l = l;
    }

    @InjectRequestParameter("f")
    private float f;
    public void setF(float f) {
        System.out.println("Setting float: " + f);
        this.f = f;
    }

    @InjectRequestParameter("d")
    private double d;
    public void setD(double d) {
        System.out.println("Setting double: " + d);
        this.d = d;
    }

    @InjectRequestParameter("c")
    private char c;
    public void setC(char c) {
        System.out.println("Setting char: " + c);
        this.c = c;
    }

    @InjectRequestParameter("flag")
    private boolean flag;
    public void setFlag(boolean flag) {
        System.out.println("Setting boolean: " + flag);
        this.flag = flag;
    }

    private void printInjectedValues() {
        System.out.println("Injected values:");
        System.out.println("b: " + b);
        System.out.println("s: " + s);
        System.out.println("i: " + i);
        System.out.println("l: " + l);
        System.out.println("f: " + f);
        System.out.println("d: " + d);
        System.out.println("c: " + c);
        System.out.println("flag: " + flag);
    }

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
        System.out.println("All primitives handled in method.");
        printInjectedValues();
    }

    @Path("/handleNum")
    public void handleSomeNumericTypes(
        @RequestParameter("id") int id,
        @RequestParameter("rate") float rate,
        @RequestParameter("count") short count
    ) {
        System.out.println("Some numeric types handled.");
        System.out.println("id: " + id + ", rate: " + rate + ", count: " + count);
        printInjectedValues();
    }

    @Path("/handleCharAndBoolean")
    public void handleCharAndBoolean(
        @RequestParameter("gender") char gender,
        @RequestParameter("active") boolean isActive
    ) {
        System.out.println("Char and Boolean handled.");
        System.out.println("gender: " + gender + ", active: " + isActive);
        printInjectedValues();
    }

    @Path("/handleSmallTypes")
    public void handleSmallTypes(
        @RequestParameter("smallByte") byte smallByte,
        @RequestParameter("smallShort") short smallShort
    ) {
        System.out.println("Small data types handled.");
        System.out.println("smallByte: " + smallByte + ", smallShort: " + smallShort);
        printInjectedValues();
    }

    @Path("/handlDoubleCombo")
    public void handleLongDoubleCombo(
        @RequestParameter("timestamp") long timestamp,
        @RequestParameter("value") double value
    ) {
        System.out.println("Long and Double combo handled.");
        System.out.println("timestamp: " + timestamp + ", value: " + value);
        printInjectedValues();
    }

    @Path("/handleJustOne")
    public void handleJustOne(
        @RequestParameter("flag") boolean flag
    ) {
        System.out.println("Single boolean handled.");
        System.out.println("flag: " + flag);
        printInjectedValues();
    }
}
