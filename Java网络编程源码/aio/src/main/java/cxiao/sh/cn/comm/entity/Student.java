package cxiao.sh.cn.comm.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student implements Serializable {
    Integer id;
    String name;
    Sex gender;
    LocalDate birthday;
    double gpa;
    List<Book> reads;

}