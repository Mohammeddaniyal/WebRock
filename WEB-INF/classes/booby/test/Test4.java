package booby.test;

import com.thinking.machines.webrock.annotations.GET;
import com.thinking.machines.webrock.annotations.Path;
import com.thinking.machines.webrock.annotations.RequestParameter;
import com.thinking.machines.webrock.annotations.InjectRequestParameter;

@GET
@Path("/test4")
public class Test4 {
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
        System.out.println("Setting flag: " + flag);
        this.flag = flag;
    }

    @Path("/handleAll")
    public void handleAllTypes(@RequestParameter("d") double dd) {
        System.out.println("All primitives handled:");
        System.out.println("byte: " + b);
        System.out.println("short: " + s);
        System.out.println("int: " + i);
        System.out.println("long: " + l);
        System.out.println("float: " + f);
        System.out.println("double (method param): " + dd);
        System.out.println("double (injected): " + d);
        System.out.println("char: " + c);
        System.out.println("boolean: " + flag);
    }

    @InjectRequestParameter("id")
    private int id;
    public void setId(int id) {
        System.out.println("Setting id: " + id);
        this.id = id;
    }

    @InjectRequestParameter("rate")
    private float rate;
    public void setRate(float rate) {
        System.out.println("Setting rate: " + rate);
        this.rate = rate;
    }

    @InjectRequestParameter("count")
    private short count;
    public void setCount(short count) {
        System.out.println("Setting count: " + count);
        this.count = count;
    }

    @Path("/handleNum")
    public void handleSomeNumericTypes() {
        System.out.println("Some numeric types handled:");
        System.out.println("id: " + id);
        System.out.println("rate: " + rate);
        System.out.println("count: " + count);
    }

    @InjectRequestParameter("gender")
    private char gender;
    public void setGender(char gender) {
        System.out.println("Setting gender: " + gender);
        this.gender = gender;
    }

    @InjectRequestParameter("active")
    private boolean isActive;
    public void setActive(boolean isActive) {
        System.out.println("Setting active: " + isActive);
        this.isActive = isActive;
    }

    @Path("/handleCharAndBoolean")
    public void handleCharAndBoolean() {
        System.out.println("Char and Boolean handled:");
        System.out.println("gender: " + gender);
        System.out.println("active: " + isActive);
    }

    @InjectRequestParameter("smallByte")
    private byte smallByte;
    public void setSmallByte(byte smallByte) {
        System.out.println("Setting smallByte: " + smallByte);
        this.smallByte = smallByte;
    }

    @InjectRequestParameter("smallShort")
    private short smallShort;
    public void setSmallShort(short smallShort) {
        System.out.println("Setting smallShort: " + smallShort);
        this.smallShort = smallShort;
    }

    @Path("/handleSmallTypes")
    public void handleSmallTypes() {
        System.out.println("Small data types handled:");
        System.out.println("smallByte: " + smallByte);
        System.out.println("smallShort: " + smallShort);
    }

    @InjectRequestParameter("timestamp")
    private long timestamp;
    public void setTimestamp(long timestamp) {
        System.out.println("Setting timestamp: " + timestamp);
        this.timestamp = timestamp;
    }

    @InjectRequestParameter("value")
    private double value;
    public void setValue(double value) {
        System.out.println("Setting value: " + value);
        this.value = value;
    }

    @Path("/handlDoubleCombo")
    public void handleLongDoubleCombo() {
        System.out.println("Long and Double handled:");
        System.out.println("timestamp: " + timestamp);
        System.out.println("value: " + value);
    }

    @InjectRequestParameter("flag")
    private boolean singleFlag;
    public void setSingleFlag(boolean singleFlag) {
        System.out.println("Setting singleFlag: " + singleFlag);
        this.singleFlag = singleFlag;
    }

    @Path("/handleJustOne")
    public void handleJustOne() {
        System.out.println("Single boolean parameter handled:");
        System.out.println("flag: " + singleFlag);
    }
}
