package booby.test;

import com.thinking.machines.webrock.exceptions.ServiceException;
import com.thinking.machines.webrock.scopes.SessionScope;

public class SecureAccessTest {
    public void performGuard(SessionScope sessionScope) throws ServiceException
    {
        System.out.println("Method got inbvoked (Perform guard)");
        System.out.println("Session Scope :"+sessionScope);
        String token=(String)sessionScope.getAttribute("token");
        System.out.println("Hello");
        if(token==null)
        {
            throw new ServiceException("Invalid user, session not found");
        }
        System.out.println("Hi");
        if(token.trim().length()==0)
        {
            throw new ServiceException("Invalid user, session not found");
        }
        System.out.println("why");
        System.out.println("Token : "+token);
    }
}
