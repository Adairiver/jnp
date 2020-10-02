package cxiao.sh.cn.common.util;

import java.io.IOException;
import java.util.Properties;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class PropertiesUtils {
    private static Properties properties;

    static {
        try {
            properties = new Properties();
            properties.load(PropertiesUtils.class.getResourceAsStream("/app.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperties(String key) {
        return (String) properties.getProperty(key);
    }


}