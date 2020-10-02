package cxiao.sh.cn.resource;

import cxiao.sh.cn.Server;
import cxiao.sh.cn.entity.StuRepository;
import cxiao.sh.cn.entity.Student;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

@Component
@Path("stu")
public class StudentResource {
    public static void fakeLongTimeOperation(){
        try {
            //Thread.sleep((1 + new Random().nextInt(6)) * 1000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private static AtomicInteger idCounter = new AtomicInteger(1000);
    private final static MediaType[] mediaTypes = new MediaType[]{MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_JSON_TYPE};
    private MediaType randomMediaType(){
        return mediaTypes[new Random().nextInt(mediaTypes.length)];
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void getAll_Asyn(@Suspended final AsyncResponse asyncResponse){
        Server.pool.submit(
                ()->{
                    asyncResponse.resume(getAll());
                }
        );
    }
    private List<Student> getAll(){
        fakeLongTimeOperation();
        List<Student> students = StuRepository.getStudents();
        return students;
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void getOne_Asyn(@Suspended final AsyncResponse asyncResponse, @PathParam("id") Integer id) {
        Server.pool.submit(
                ()->{
                    asyncResponse.resume(getOne(id));
                }
        );
    }
    private Student getOne(Integer id) {
        fakeLongTimeOperation();
        Student student = StuRepository.getStudent(id);
        return student;
    }

    @GET
    @Path("query")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void getOneByQuery_Asyn(@Suspended final AsyncResponse asyncResponse, @QueryParam("uid") Integer id) {
        Server.pool.submit(
                ()->{
                    asyncResponse.resume(getOneByQuery(id));
                }
        );
    }
    private Student getOneByQuery(Integer id) {
        fakeLongTimeOperation();
        Student student = StuRepository.getStudent(id);
        return student;
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void createStudent_Asyn(@Suspended final AsyncResponse asyncResponse, Student student){
        Server.pool.submit(
                ()->{
                    asyncResponse.resume(createStudent(student));
                }
        );
    }
    private Student createStudent(Student student){
        fakeLongTimeOperation();
        student.setId(idCounter.incrementAndGet());
        if (StuRepository.insertStudent(student)){
            return student;
        }
        return null;
    }


    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void modifyStudent_Asyn(@Suspended final AsyncResponse asyncResponse, Student student){
        Server.pool.submit(
                ()->{
                    asyncResponse.resume(modifyStudent(student));
                }
        );
    }
    private Response modifyStudent(Student student){
        fakeLongTimeOperation();
        Student stu = StuRepository.updateStudent(student);
        if (stu!=null){
            return Response
                    .status(Response.Status.OK)
                    .type(randomMediaType())
                    .entity(stu)
                    .build();
        }else{
            return Response
                    .status(Response.Status.NOT_MODIFIED)
                    .build();
        }
    }

    @DELETE
    @Path("{id}")
    public void eraseStudent_Asyn(@Suspended final AsyncResponse asyncResponse, @PathParam("id") Integer id) {
        Server.pool.submit(
                ()->{
                    asyncResponse.resume(eraseStudent(id));
                }
        );
    }
    private Response eraseStudent(Integer id){
        fakeLongTimeOperation();
        if (StuRepository.deleteStudent(id)){
            return Response.ok(id.toString()).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(id.toString())
                .type(randomMediaType())
                .build();
    }

}