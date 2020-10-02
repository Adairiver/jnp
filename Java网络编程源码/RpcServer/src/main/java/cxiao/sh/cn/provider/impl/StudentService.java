package cxiao.sh.cn.provider.impl;

import cxiao.sh.cn.common.business.StudentInterface;
import cxiao.sh.cn.common.entity.StuRepository;
import cxiao.sh.cn.common.entity.Student;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class StudentService implements StudentInterface {
    private static AtomicInteger idCounter = new AtomicInteger(1000);

    @Override
    public List<Student> getAll() {
        List<Student> students = StuRepository.getStudents();
        return students;
    }

    @Override
    public Student getOne(Integer id) {
        Student student = StuRepository.getStudent(id);
        return student;
    }

    @Override
    public boolean modifyStudent(Student student) {
        return StuRepository.updateStudent(student);
    }

    @Override
    public boolean eraseStudent(Integer id) {
        return StuRepository.deleteStudent(id);
    }

    @Override
    public Student createStudent(Student student) {
        student.setId(idCounter.incrementAndGet());
        StuRepository.insertStudent(student);
        return student;
    }
}