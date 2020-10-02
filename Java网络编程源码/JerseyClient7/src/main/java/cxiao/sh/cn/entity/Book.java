package cxiao.sh.cn.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name="Book")
public class Book {
    String name;
    String author;
    Double price;
    int year;
    Booktype type;
}