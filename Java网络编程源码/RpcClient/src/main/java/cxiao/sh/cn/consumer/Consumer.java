package cxiao.sh.cn.consumer;

import cxiao.sh.cn.client.ClientStubProxyFactory;
import cxiao.sh.cn.client.NettyNetClient;
import cxiao.sh.cn.common.business.HelloInterface;
import cxiao.sh.cn.common.business.StudentInterface;
import cxiao.sh.cn.common.entity.Book;
import cxiao.sh.cn.common.entity.Booktype;
import cxiao.sh.cn.common.entity.Sex;
import cxiao.sh.cn.common.entity.Student;
import cxiao.sh.cn.common.protocol.*;
import cxiao.sh.cn.common.serializable.ClientJavaSerializer;
import cxiao.sh.cn.common.serializable.ClientXMLSerializer;
import cxiao.sh.cn.common.serializable.IClientSerializer;
import cxiao.sh.cn.discovery.DefaultServiceInfoDiscovery;

import java.awt.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Consumer {
    public static void main(String[] args) {
        ClientStubProxyFactory cspf = new ClientStubProxyFactory();
        // 设置服务发现者
        cspf.setSid(new DefaultServiceInfoDiscovery());

        Map<String, IClientSerializer<Request, Response>> supportingSerializers = new HashMap<>();
        supportingSerializers.put("javas", new ClientJavaSerializer());
        supportingSerializers.put("xml", new ClientXMLSerializer());

        cspf.setSupportingSerializers(supportingSerializers);

        // 设置网络层实现
        cspf.setNetClient(new NettyNetClient());

        // 获取远程服务的代理，参数可调
        HelloInterface hello = cspf.getProxy(HelloInterface.class, "2.0");
        // 像调用本地方法一样执行远程方法
        String msg = hello.sayHello("World!");
        System.out.println(msg);
        System.out.println(hello.multiPoint(new Point(5,10), 2));

        StudentInterface stUtils = cspf.getProxy(StudentInterface.class, "1.0");
        System.out.println(stUtils.getAll());
        System.out.println(stUtils.getOne(102));
        System.out.println(stUtils.createStudent(
                new Student(
                        null, "李清华", Sex.MALE,
                        LocalDate.of(2002,6,1),
                        3.78,
                        Arrays.asList(
                                new Book("叔本华思想随笔", "叔本华", 61.40, 2019, Booktype.PHILOSOPHY ),
                                new Book("哲学的殿堂", "威尔杜兰特", 81.00, 2016, Booktype.PHILOSOPHY ),
                                new Book("概率论与数理统计", "周知文", 38.60, 2017, Booktype.ENGINEERING )
                        )
                )
        ));
        System.out.println(stUtils.getAll());
        System.out.println(stUtils.eraseStudent(104));
        System.out.println(stUtils.modifyStudent(
                new Student(
                        103, "Christin Jersey", Sex.FEMALE,
                        LocalDate.of(2000,1,1),
                        3.33,
                        Arrays.asList(
                                new Book("Java核心", "Cay S. Horstmann", 139.00, 2018, Booktype.ENGINEERING ),
                                new Book("雅舍小品", "梁实秋", 17.90, 2010, Booktype.LITERATURE )
                        )
                )
        ));
        System.out.println(stUtils.getOne(103));
        System.out.println(stUtils.getAll());
    }
}