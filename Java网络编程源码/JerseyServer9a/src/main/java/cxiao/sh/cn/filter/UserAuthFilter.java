package cxiao.sh.cn.filter;

import cxiao.sh.cn.security.BasicSecurityConext;
import cxiao.sh.cn.service.AccountService;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.util.Base64;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

@Priority(Priorities.AUTHENTICATION)
public class UserAuthFilter implements ContainerRequestFilter {

    private AccountService accountService = new AccountService();

    @Override
    public void filter(ContainerRequestContext req) throws IOException {

        String authzHeader = req.getHeaderString(HttpHeaders.AUTHORIZATION);
        System.out.println(authzHeader);

        final Base64.Decoder decoder = Base64.getDecoder();
        String decoded = new String(decoder.decode(authzHeader.split(" ")[1]), "UTF-8");
        System.out.println(decoded);

        String[] split = decoded.split(":");
        String account = split[0];
        String pwd = split[1];

        // Authentication
        if (account==null || !pwd.equals(accountService.getPwd(account))){
            Response resp = Response.status(Response.Status.FORBIDDEN)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new String("no permission."))
                    .build();
            req.abortWith(resp);
        }

        // Authorization
        SecurityContext oldContext = req.getSecurityContext();
        req.setSecurityContext(new BasicSecurityConext(account, accountService.getRole(account), oldContext.isSecure()));
    }
}