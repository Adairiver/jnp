package cxiao.sh.cn.provider.impl;

import cxiao.sh.cn.common.business.HelloInterface;

import java.awt.*;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class HelloService1 implements HelloInterface {
    @Override
    public String sayHello(String name) {
        return "Hello " + name;
    }

    @Override
    public Point multiPoint(Point p, int multi) {
        p.x = p.x * multi;
        p.y = p.y * multi;
        return p;
    }
}