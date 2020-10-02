package cxiao.sh.cn.comm.entity;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class StuRepository {
    public static Student getStudent(){
        Random rand = new Random();
        return stuList.get(rand.nextInt(stuList.size()));
    }

    public static List<Student> getStudents(){
        return stuList;
    }

    private static List<Student> stuList = Arrays.asList(
            new Student(101, "李嘉威", Sex.MALE,
                    LocalDate.of(2001, 4, 15), 2.89,
                    Arrays.asList(
                            new Book("平凡的世界", "路遥", 74.50, 2017, Booktype.LITERATURE ),
                            new Book("哈利波特与密室", "J.K.罗琳", 23.50, 2018, Booktype.LITERATURE ),
                            new Book("从常青藤到华尔街", "赵佳妮", 34.80, 2020, Booktype.EDUCATION )
                    )),
            new Student(
                    102, "孙小婧", Sex.FEMALE,
                    LocalDate.of(2000, 5, 28), 3.76,
                    Arrays.asList(
                            new Book("叔本华思想随笔", "叔本华", 61.40, 2019, Booktype.PHILOSOPHY ),
                            new Book("哲学的殿堂", "威尔杜兰特", 81.00, 2016, Booktype.PHILOSOPHY ),
                            new Book("概率论与数理统计", "周知文", 38.60, 2017, Booktype.ENGINEERING )
                    )
            ),
            new Student(
                    103, "Peter Kevin", Sex.MALE,
                    LocalDate.of(2002, 12, 6), 3.28,
                    Arrays.asList(
                            new Book("Java核心技术", "Cay S. Horstmann", 139.00, 2018, Booktype.ENGINEERING ),
                            new Book("雅舍小品", "梁实秋", 17.90, 2010, Booktype.LITERATURE )
                    )
            ),
            new Student(
                    104, "陆伟", Sex.MALE,
                    LocalDate.of(2001, 7, 21), 3.35,
                    null
            ),
            new Student(
                    105, "董丽仪", Sex.FEMALE,
                    LocalDate.of(2000, 8, 7), 2.95,
                    Arrays.asList(
                            new Book("教育学原理（第2版）", "柳海民", 42.10, 2019, Booktype.EDUCATION ),
                            new Book("茶花女", "小仲马", 20.00, 2018, Booktype.LITERATURE )
                    )
            )
    );
}