package cxiao.sh.cn;

import cxiao.sh.cn.entity.Book;
import cxiao.sh.cn.entity.Booktype;
import cxiao.sh.cn.entity.Sex;
import cxiao.sh.cn.entity.Student;
import org.eclipse.persistence.eis.EISObjectPersistenceXMLProject;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.moxy.xml.MoxyXmlFeature;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.*;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Client {
    public static void main(String[] args) {
        Client client = new Client("user", "user123");
        client.doWork();
    }
    private String account;
    private String pwd;
    private final String folderOnClient = "D:\\ClientFiles\\";
    //这里我们随机选择数据格式来发送服务请求
    private final static MediaType[] mediaTypes = new MediaType[]{MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_JSON_TYPE};
    private MediaType randomMediaType(){
        return mediaTypes[new Random().nextInt(mediaTypes.length)];
    }
    private WebTarget target;
    public Client(String account, String pwd){
        this.account = account;
        this.pwd = pwd;
        ClientConfig clientConfig = new ClientConfig();
        //使用 Http 头部的AUTHORIZATION字段传输用户名和口令，BASE64编码；
        HttpAuthenticationFeature authenticationFeature = HttpAuthenticationFeature.basic(account, pwd);
        clientConfig.register(authenticationFeature);
        clientConfig.register(MoxyXmlFeature.class);
        clientConfig.register(JacksonFeature.class);
        clientConfig.register(MultiPartFeature.class);
        javax.ws.rs.client.Client rsClient = ClientBuilder.newClient(clientConfig);
        target = rsClient.target("http://localhost:8080/jersey/");
    }

    public void doWork(){
        getAllStudents();
        getStudentById(102);
        queryStudentById(103);
        addStudent();
        updateStudent(104);
        updateStudent(1050);
        deleteStudent(103);
        deleteStudent(1060);
        download("xyz.pdf");
        download("xyz.mp4");
        download("12345.pdf");
        upload("abc.docx");
        upload("abc.txt");
        upload("12345.mp4");
    }


    private List<Student> getAllStudents(){
        MediaType mediaType = randomMediaType();
        Response response = target
                .path("stu")
                .request(mediaType)
                .get();
        if (response.getStatus()==Response.Status.FORBIDDEN.getStatusCode()){
            System.out.println("权限不够，无法操作！");
            return null;
        }
        GenericType<List<Student>> genericType = new GenericType<>(){};
        List<Student> students= response.readEntity(genericType);
        System.out.println("学生列表：");
        for (Student stu : students) {
            System.out.println(stu);
        }
        System.out.println("列表结束。");
        return students;
    }

    private Student getStudentById(int sId){
        MediaType mediaType = randomMediaType();
        Response response = target
                .path("stu")
                .path(String.valueOf(sId))
                .request(mediaType)
                .get();
        if (response.getStatus()==Response.Status.FORBIDDEN.getStatusCode()){
            System.out.println("权限不够，无法操作！");
            return null;
        }
        Student st = response.readEntity(Student.class);
        System.out.println("学生信息：");
        if(st!=null) {
            System.out.println(st);
        }
        System.out.println("信息结束。");
        return  st;
    }

    private Student queryStudentById(int sId){
        MediaType mediaType = randomMediaType();
        Response response = target
                .path("stu")
                .path("query")
                .queryParam("uid", sId)
                .request(mediaType)
                .get();
        if (response.getStatus()==Response.Status.FORBIDDEN.getStatusCode()){
            System.out.println("权限不够，无法操作！");
            return null;
        }
        Student st = response.readEntity(Student.class);
        System.out.println("学生信息：");
        if(st!=null) {
            System.out.println(st);
        }
        System.out.println("信息结束。");
        return  st;
    }

    private Student addStudent(){
        Student student = newRandomStudent();
        MediaType mediaType = randomMediaType();
        Entity<Student> studentEntity = Entity.entity(student, mediaType);
        mediaType = randomMediaType();
        Response response = target
                .path("stu")
                .request(mediaType)
                .post(studentEntity);
        if (response.getStatus()==Response.Status.FORBIDDEN.getStatusCode()){
            System.out.println("权限不够，无法操作！");
            return null;
        }
        Student stu = response.readEntity(Student.class);
        if (stu!=null) {
            System.out.println("添加成功，编号：" + stu.getId());
        }else{
            System.out.println("添加失败，姓名：" + student.getName());
        }
        return stu;
    }

    private Student updateStudent(int id){
        Student st = newRandomStudent();
        st.setId(id);
        MediaType mediaType = randomMediaType();
        Entity<Student> studentEntity = Entity.entity(st, mediaType);
        mediaType = randomMediaType();
        Response response = target
                .path("stu")
                .request(mediaType)
                .put(studentEntity);
        if (response.getStatus()==Response.Status.FORBIDDEN.getStatusCode()){
            System.out.println("权限不够，无法操作！");
            return null;
        }
        Student stu = null;
        //根据响应的状态码作不同处理
        if (response.getStatus()==Response.Status.NOT_MODIFIED.getStatusCode()){
            System.out.println("修改失败，编号：" + id);
        }else{
            stu = response.readEntity(Student.class);
            System.out.println("修改成功，编号：" + stu.getId());
        }
        return stu;
    }

    private boolean deleteStudent(int id){
        MediaType mediaType = randomMediaType();
        Response response = target
                .path("stu")
                .path(String.valueOf(id))
                .request(mediaType)
                .delete();
        if (response.getStatus()==Response.Status.FORBIDDEN.getStatusCode()){
            System.out.println("权限不够，无法操作！");
            return false;
        }
        //根据响应的状态码作不同处理
        if (response.getStatus()==200){
            System.out.println("删除成功，编号：" + response.readEntity(String.class));
        }else{
            System.out.println("删除失败，编号：" + response.readEntity(String.class));
        }
        return (response.getStatus()==200);
    }

    private void download(String remoteFileName){
        Response response = target
                .path("file")
                .path(remoteFileName)
                .request(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                .get();
        if (response.getStatus()==Response.Status.FORBIDDEN.getStatusCode()){
            System.out.println("权限不够，无法操作！");
            return;
        }
        if (response.getStatus()==Response.Status.NOT_FOUND.getStatusCode()){
            System.out.println("远端文件不存在：" + remoteFileName);
        }
        if (response.getStatus()==Response.Status.OK.getStatusCode()) {
            String localFilePath = folderOnClient + UUID.randomUUID().toString() + "-" + remoteFileName;
            File localFile = new File(localFilePath);
            byte[] bytes = response.readEntity(byte[].class);
            try {
                OutputStream fos = new FileOutputStream(localFile);
                fos.write(bytes);
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("保存文件成功：" + localFilePath);
        }
    }

    private void upload(String localFileName){
        String localFilePath = folderOnClient + localFileName;
        File localFile =  new File(localFilePath);
        if (!localFile.exists()){
            System.out.println("本地文件不存在：" + localFilePath);
            return;
        }
        FormDataMultiPart part = new FormDataMultiPart();
        //"file"是控件命名，与服务端的@FormDataParam("file")一致
        part.bodyPart(new FileDataBodyPart("file",localFile));

        Response response = target
                .path("file")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(part, MediaType.MULTIPART_FORM_DATA_TYPE));

        if (response.getStatus() == Response.Status.FORBIDDEN.getStatusCode()) {
            System.out.println("权限不够，无法操作！");
            return;
        }
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            System.out.println("上传文件成功：" + localFilePath);
        } else {
            System.out.println("上传文件失败：" + localFilePath);
        }
    }

    private static Student newRandomStudent() {
        Student st = new Student();
        st.setBirthday(LocalDate.of(2003,8,1));
        st.setGender(Sex.FEMALE);
        st.setGpa(3.45);
        st.setName(getRandomString(10));

        List<Book> bookList = new ArrayList<>();
        st.setReads(bookList);

        for(int i=0;i<3;i++) {
            Book book = new Book();
            book.setAuthor("Author:" + getRandomString(6));
            book.setName("Name:" + getRandomString(20));
            book.setPrice(45.6);
            book.setType(Booktype.EDUCATION);
            book.setYear(2009);
            bookList.add(book);
        }

        return st;
    }

    //生成指定length的随机字符串（A-Z，a-z，0-9）
    private static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}