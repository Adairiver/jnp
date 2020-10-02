package cxiao.sh.cn.security;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class BasicSecurityConext implements SecurityContext {
    private String accountName;
    private String roleName;
    private boolean secure;
    public BasicSecurityConext(String accountName, String roleName, boolean secure) {
        this.accountName = accountName;
        this.roleName = roleName;
        this.secure = secure;
    }
    @Override
    public Principal getUserPrincipal() {
        return new Principal() {
            @Override
            public String getName() {
                return accountName;
            }
        };
    }
    @Override
    public boolean isUserInRole(String s) {
        return s.equals(this.roleName);
    }
    @Override
    public boolean isSecure() {
        return secure;
    }
    @Override
    public String getAuthenticationScheme() {
        return SecurityContext.BASIC_AUTH;
    }
}