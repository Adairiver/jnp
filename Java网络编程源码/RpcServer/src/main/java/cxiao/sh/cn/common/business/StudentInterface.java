package cxiao.sh.cn.common.business;

import cxiao.sh.cn.common.entity.Student;

import java.util.List;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public interface StudentInterface {
    List<Student> getAll();
    Student getOne(Integer id);
    boolean modifyStudent(Student student);
    boolean eraseStudent(Integer id);
    Student createStudent(Student student);
}