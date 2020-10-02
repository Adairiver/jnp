package cxiao.sh.cn.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book implements Serializable{
    String name;
    String author;
    Double price;
    int year;
    Booktype type;
}