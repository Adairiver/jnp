package cxiao.sh.cn.service;


import java.util.HashMap;
import java.util.Map;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class AccountService {
    Map<String, String> accountDB = new HashMap<>();
    public AccountService(){
        accountDB.put("admin", "admin123");
        accountDB.put("user","user123");
        accountDB.put("abc", "abc123");
        accountDB.put("xyz","xyz123");
    }
    public String getPwd(String userName){
        if (!accountDB.containsKey(userName)){
            return "";
        }
        return accountDB.get(userName);
    }
    public String getRole(String userName){
        String role = "COMMON_USER";
        if (userName.equals("admin")){
            role = "SUPER_USER";
        }

        if (userName.equals("xyz")){
            role = "GUEST";
        }

        return role;
    }
}