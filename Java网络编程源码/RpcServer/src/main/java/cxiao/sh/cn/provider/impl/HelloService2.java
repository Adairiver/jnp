package cxiao.sh.cn.provider.impl;

import cxiao.sh.cn.common.business.HelloInterface;

import java.awt.*;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class HelloService2 implements HelloInterface {
    @Override
    public String sayHello(String name) {
        System.out.println("HelloService2被调用");
        return "你好, " + name;
    }

    @Override
    public Point multiPoint(Point p, int multi) {
        p.x = 100 + p.x * multi;
        p.y = 100 + p.y * multi;
        return p;
    }
}