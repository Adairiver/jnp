package cxiao.sh.cn;

import cxiao.sh.cn.entity.Book;
import cxiao.sh.cn.entity.Booktype;
import cxiao.sh.cn.entity.Sex;
import cxiao.sh.cn.entity.Student;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.moxy.xml.MoxyXmlFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.DrbgParameters;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Future;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

public class Client {
    public static void main(String[] args) {
        Client client = new Client();
        client.doWork();
    }
    private final String folderOnClient = "D:\\ClientFiles\\";
    //随机选择数据格式来发送服务请求
    private final static MediaType[] mediaTypes = new MediaType[]{MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_JSON_TYPE};
    private MediaType randomMediaType(){
        return mediaTypes[new Random().nextInt(mediaTypes.length)];
    }
    private WebTarget target;
    public Client(){
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(MoxyXmlFeature.class);
        clientConfig.register(JacksonFeature.class);
        clientConfig.register(MultiPartFeature.class);
        javax.ws.rs.client.Client rsClient = ClientBuilder.newClient(clientConfig);
        target = rsClient.target("http://localhost:8080/jersey/");
    }

    public void doWork(){
        getAllStudents_Asyn();
        getStudentById_Asyn(102);
        getStudentById_Asyn(1020);
        queryStudentById_Asyn(102);
        queryStudentById_Asyn(1020);
        addStudent_Asyn();
        addStudent_Asyn();
        addStudent_Asyn();
        addStudent_Asyn();
        deleteStudent_Asyn(102);
        deleteStudent_Asyn(1020);
        download_Asyn("xyz.pdf");
        download_Asyn("xyz.mp4");
        download_Asyn("xyz111.pdf");
        upload_Asyn("abc.docx");
        upload_Asyn("abc.txt");
        upload_Asyn("abc111.mp4");
    }

    private List<Student> getAllStudents_Asyn(){
        final AsyncInvoker async = target
                .path("stu")
                .request() //头部属性Accept对应的参数默认值为json
                .async();

        final Future<Response> responseFuture = async
                .get(
                        new InvocationCallback<Response>(){
                            @Override
                            public void completed(Response response) {
                                System.out.println("getAllStudents_Asyn 收到的数据格式：" + response.getMediaType());
                            }
                            @Override
                            public void failed(Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                );
        GenericType<List<Student>> genericType = new GenericType<>(){};
        List<Student> students = null;

        try{
            Response response = responseFuture.get();
            students = response.readEntity(genericType);
        }catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("学生列表：");
        if (students!=null) {
            for (Student stu : students) {
                System.out.println(stu);
            }
        }
        System.out.println("列表结束。");

        return students;
    }

    private Student getStudentById_Asyn(int sId){
        MediaType mediaType = randomMediaType();
        final AsyncInvoker async = target
                .path("stu")
                .path(String.valueOf(sId))
                .request(mediaType)
                .async();
        //虽然服务端功能函数的返回值为Student类型，
        //但是客户端还是可以使用泛型Future<Response>
        final Future<Response> responseFuture = async
                .get(
                        new InvocationCallback<Response>() {
                            @Override
                            public void completed(Response response) {
                                System.out.println("getStudentById_Asyn 收到的数据格式：" + response.getMediaType());
                            }
                            @Override
                            public void failed(Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                );
        Student st=null;
        try {
            Response response = responseFuture.get();
            st = response.readEntity(Student.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("学生信息：");
        if(st!=null) {
            System.out.println(st);
        }
        System.out.println("信息结束。");
        return st;
    }

    private Student queryStudentById_Asyn(int sId){
        MediaType mediaType = randomMediaType();
        final AsyncInvoker async = target
                .path("stu")
                .path("query")
                .queryParam("uid", sId)
                .request(mediaType)
                .async();
        final Future<Response> responseFuture = async
                .get(
                        new InvocationCallback<Response>() {
                            @Override
                            public void completed(Response response) {
                                System.out.println("queryStudentById_Asyn 收到的数据格式：" + response.getMediaType());
                                //主线程调用了response.readEntity()，
                                // 这里就不能再调用.readEntity()
                                // 因为.readEntity只能调用一次。
                            }
                            @Override
                            public void failed(Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                );
        Student st=null;
        try {
            Response response = responseFuture.get();
            st = response.readEntity(Student.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("学生信息：");
        if(st!=null) {
            System.out.println(st);
        }
        System.out.println("信息结束。");
        return st;
    }


    private Student addStudent_Asyn(){
        Student student = newRandomStudent();
        MediaType mediaType = randomMediaType();
        Entity<Student> studentEntity = Entity.entity(student, mediaType);
        mediaType = randomMediaType();
        final AsyncInvoker async = target
                .path("stu")
                .request(mediaType)
                .async();
        //因为服务端功能函数的返回值为Student类型，
        //所以客户端可以直接使用泛型Future<Student>
        final Future<Student> studentFuture = async
                .post(
                        studentEntity,
                        new InvocationCallback<Student>() {
                            @Override
                            public void completed(Student stu) {
                                if (stu!=null) {
                                    System.out.println("添加成功，编号：" + stu.getId());
                                    System.out.println(stu);
                                }else{
                                    System.out.println("添加用户失败！" + student.getName());
                                }
                            }
                            @Override
                            public void failed(Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                );

        Student stu = null;
        try{
            //若添加不成功，得到的stu是null。
            stu = studentFuture.get();
            if (stu!=null) {
                System.out.println(stu);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return stu;
    }


    private Student updateStudent_Asyn(int id){
        Student st = newRandomStudent();
        st.setId(id);
        MediaType mediaType = randomMediaType();
        Entity<Student> studentEntity = Entity.entity(st, mediaType);
        mediaType = randomMediaType();
        final AsyncInvoker async  = target
                .path("stu")
                .request(mediaType)
                .async();
        //因为服务端功能函数的返回值为Response类型，
        //所以客户端使用泛型Future<Response>
        final Future<Response> responseFuture = async
                .put(
                        studentEntity,
                        new InvocationCallback<Response>() {
                            @Override
                            public void completed(Response response) {
                                //根据响应的状态码作不同处理
                                if (response.getStatus()==Response.Status.NOT_MODIFIED.getStatusCode()){
                                    System.out.println("修改失败，编号：" + id);
                                }else{
                                    System.out.println("修改成功，编号：" + id);
                                }
                            }
                            @Override
                            public void failed(Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                );
        Response response = null;
        Student stu = null;
        try{
            response = responseFuture.get();
        }catch (Exception e){
            e.printStackTrace();
        }
        if (response.getStatus()==Response.Status.OK.getStatusCode()){
            stu = response.readEntity(Student.class);
        }
        System.out.println(stu);
        return stu;
    }

    private boolean deleteStudent_Asyn(int id){
        MediaType mediaType = randomMediaType();
        final AsyncInvoker async = target
                .path("stu")
                .path(String.valueOf(id))
                .request(mediaType)
                .async();
        final Future<Response> responseFuture = async
                .delete(
                        new InvocationCallback<Response>() {
                            @Override
                            public void completed(Response response) {
                                //根据响应的状态码作不同处理
                                if (response.getStatus()==200){
                                    System.out.println("删除成功，编号：" + response.readEntity(String.class));
                                }else{
                                    System.out.println("删除失败，编号：" + response.readEntity(String.class));
                                }
                            }
                            @Override
                            public void failed(Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                );

        Response response = null;
        try{
            response = responseFuture.get();
        }catch (Exception e){
            e.printStackTrace();
        }
        return (response.getStatus()==200);
    }

    private void download_Asyn(String remoteFileName){
        final AsyncInvoker async = target
                .path("file")
                .path(remoteFileName)
                .request(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                .async();
        final Future<Response> responseFuture = async
                .get(
                        new InvocationCallback<Response>() {
                            @Override
                            public void completed(Response response) {
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
                            @Override
                            public void failed(Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                );
    }

    private void upload_Asyn(String localFileName){
        String localFilePath = folderOnClient + localFileName;
        File localFile =  new File(localFilePath);
        if (!localFile.exists()){
            System.out.println("本地文件不存在：" + localFilePath);
            return;
        }
        FormDataMultiPart part = new FormDataMultiPart();
        //"file"是控件命名，与服务端的@FormDataParam("file")一致
        part.bodyPart(new FileDataBodyPart("file",localFile));

        final AsyncInvoker async = target
                .path("file")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .async();
        final Future<Response> responseFuture = async
                .post(
                        Entity.entity(part, MediaType.MULTIPART_FORM_DATA_TYPE),
                        new InvocationCallback<Response>() {
                            @Override
                            public void completed(Response response) {
                                if (response.getStatus()==Response.Status.OK.getStatusCode()){
                                    System.out.println("上传文件成功：" + localFilePath);
                                }else{
                                    System.out.println("上传文件失败：" + localFilePath);
                                }
                            }
                            @Override
                            public void failed(Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                );
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