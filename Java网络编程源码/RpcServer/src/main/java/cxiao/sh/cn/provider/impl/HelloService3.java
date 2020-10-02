package cxiao.sh.cn.provider.impl;

import cxiao.sh.cn.common.business.HelloInterface;

import java.awt.*;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class HelloService3 implements HelloInterface {
    @Override
    public String sayHello(String name) {
        System.out.println("HelloService3被调用");
        return "Good morning, " + name;
    }

    @Override
    public Point multiPoint(Point p, int multi) {
        p.x = 300 + p.x * multi;
        p.y = 300 + p.y * multi;
        return p;
    }
}