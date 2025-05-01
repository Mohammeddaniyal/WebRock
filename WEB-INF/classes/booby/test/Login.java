package booby.test;

import java.util.UUID;

import com.thinking.machines.webrock.annotations.POST;
import com.thinking.machines.webrock.annotations.Path;
import com.thinking.machines.webrock.scopes.SessionScope;

@POST
@Path("/login")
public class Login {
    @Path("/login")
    public void login(SessionScope sessionScope)
    {
        //perfrom verification of username and password
        String uuid=UUID.randomUUID().toString();
        sessionScope.setAttribute("token", uuid);
        System.out.println("Token set : "+uuid); 
    }
}
