# 《Java网络编程进阶——从BIO到RPC》一书的源码及ppt
## ISBN：978-7-302-57575-7
随着网络及Java技术的发展，分布式和微服务成了企业信息部门在技术选型时的首选。网络编程技术是分布式系统开发的基石，无论是使用现有的微服务框架开发业务应用还是自行研发底层的服务框架，了解、掌握底层的网络编程技术如AIO、NIO等，对开发者来说都是必不可少且多多益善的技术修炼。基础不牢，地动山摇，丰富自己的技术知识栈将为开发者的职业生涯提供更加广阔的发展空间。
## 本书特色
网络编程是一门实用型技术，必须理论和实践相结合。本书在阐述理论知识或设计思路时，辅以更为直观的图解，使其更易理解；本书亦用大量的篇幅展示落地实用的Java代码并对其进行分析和解释；通过案例的开发和分析，本书还向读者展示了Java网络技术与Java其它技术如线程、同步器、泛型、反射等的关联使用。
## 读者对象
  	Java程序员  
  	分布式系统架构师  
  	高校“网络程序设计”课程的学生  
  	其他对Java网络编程感兴趣的读者  
## 本书内容
基于学以致用的原则，本书通过八章内容来介绍Java网络编程相关的技术。  
第一章至第四章依次介绍基于BIO、NIO、AIO及Netty每种技术实现三个案例的设计和编码。  
第五章与读者分享RESTful应用轻量级框架Jersey的使用经验，包括同步请求及应答、异步请求及应答、基本认证和授权、以及如何替换某些部件，这些经验使得Jersey应用的开发更加高效和鲁棒。  
第六章介绍Web服务消息推送规范SSE的使用，基于Jersey的SSE机制实现订阅-发布功能以及一个可重入的分布式锁。  
第七章自行设计一个RPC框架并进行代码实现，阐述了设计方案，并对关键代码进行解释。  
第八章开发两个简单常见的应用，一个是基于WebSocket的聊天室，一个是邮件发送程序。  
附录解答了Java开发时常见的若干问题。  
本书所关注的网络编程技术符合业界当今主流，并有一定的前瞻性，可以有效提高读者的Java网络技术水平及核心竞争力。
## 勘误和交流
由于笔者的水平有限，书中难免不足之处，还望读者海涵和指正。非常期待能够得到你们的反馈，在技术之路上互勉共进。若读者想与我进行技术交流，可发电子邮件到 cxiao@fudan.edu.cn 。  
## 致谢
感谢我的家人。本书的写作占用了大量的业余时间，没有家人的支持和理解，这本书不可能完成。  
感谢清华大学出版社的编辑向威博士。因为您的一直鼓励和帮助，本书才会如此顺利地出版。

# 目 录

## 第一章 BIO	
    1.1 Socket通信模型  	
    1.2 完善通信框架  	
    1.3 升级write与read	
    1.4 案例一：传输字符串的会话	
    1.5 案例二：传输对象的会话	
    1.6 案例三：传输文件的会话	
    习题	
## 第二章 NIO	
    2.1 NIO模型	
    2.2 NIO服务端框架代码	
    2.3 NIO客户端框架代码	
    2.4 ByteBuffer及其在NIO中使用的问题	
    2.5 NIO的分帧处理	
    2.6 案例一：传输字符串的会话	
    2.7 案例二：传输对象的会话	
    2.8 案例三：传输文件的会话	
    2.9 设计多线程服务器	
    习题	
## 第三章 AIO	
    3.1 异步操作概述	
    3.2 AIO服务端框架代码	
    3.3 AIO客户端框架代码	
    3.4 AIO的分帧问题	
    3.5 案例一：传输字符串的会话	
    3.6 案例二：传输对象的会话	
    3.7 案例三：传输文件的会话	
    习题	
## 第四章 Netty	
    4.1 Netty的使用模型	
    4.2 Netty的入站与出站	
    4.3 服务端框架代码	
    4.4 客户端框架代码	
    4.5 ByteBuf、分帧以及ChannelHandler链	
    4.6 案例一：传输字符串的会话	
    4.7 案例二：传输对象的会话	
    4.8 案例三：传输文件的会话	
    习题	
## 第五章 Jersey	
    5.1 概述	
    5.2 案例一：对象资源的操作	
        5.2.1 服务端基本框架	
        5.2.2 客户端基本框架	
        5.2.3 逐项添加URI功能	
    5.3 案例二：异步请求与异步应答	
        5.3.1 服务端基本框架	
        5.3.2 客户端基本框架	
        5.3.3 逐项添加URI功能	
    5.4 案例三：基本认证和授权	
        5.4.1 服务端基本框架	
        5.4.2 客户端基本框架	
        5.4.3 服务端认证项	
        5.4.4 客户端认证项	
    5.5 案例四：替换某些部件	
        5.5.1 替换JSON解析器	
        5.5.2 替换Servlet容器	
        5.5.3 替换Web服务器	
        5.5.4 完全剥离Spring	
    习题	
## 第六章 SSE	
    6.1 SSE概述	
    6.2 订阅-发布功能	
        6.2.1 服务端代码	
        6.2.2 客户端代码	
    6.3 实现分布式锁	
        6.3.1 分布式锁服务端	
        6.3.2 分布式锁客户端	
        6.3.3 分布式锁的使用	
    习题	
## 第七章 实现RPC框架	
    7.1 RPC框架概述	
    7.2 框架的客户端设计	
        7.2.1 序列化器	
        7.2.2 代理层
        7.2.3 通信层	
    7.3 框架的服务端设计	
        7.3.1 序列化器	
        7.3.2 反射层	
        7.3.3 通信层	
    7.4 服务消费者	
    7.5 服务发布者	
    习题	
## 第八章 两个简单应用
    8.1 WebSocket应用	
    8.2 邮件发送程序	
    习题	
## 附录	
