package cxiao.sh.cn.resource;

import cxiao.sh.cn.entity.StuRepository;
import cxiao.sh.cn.entity.Student;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
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

@Path("stu")
@PermitAll
public class StudentResource {
    private static AtomicInteger idCounter = new AtomicInteger(1000);
    private final static MediaType[] mediaTypes = new MediaType[]{MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_JSON_TYPE};
    private MediaType randomMediaType(){
        return mediaTypes[new Random().nextInt(mediaTypes.length)];
    }

    @GET
    @RolesAllowed("COMMON_USER")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Student> getAll(){
        List<Student> students = StuRepository.getStudents();
        //消息体将使用请求时Accept属性值指定的格式且应答状态码为OK
        return students;
    }

    @GET
    @Path("{id}")
    @RolesAllowed({"SUPER_USER","GUEST"})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Student getOne(@PathParam("id") Integer id) {
        Student student = StuRepository.getStudent(id);
        return student;
    }

    @GET
    @Path("query")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Student getOneByQuery(@QueryParam("uid") Integer id) {
        Student student = StuRepository.getStudent(id);
        return student;
    }

    @POST
    @RolesAllowed("SUPER_USER")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Student createStudent(Student student){
        student.setId(idCounter.incrementAndGet());
        StuRepository.insertStudent(student);
        return student;
    }

    @PUT
    @RolesAllowed({"COMMON_USER","SUPER_USER"})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response modifyStudent(Student student){
        //模拟对学生信息的更新
        Student stu = StuRepository.updateStudent(student);
        if (stu!=null){
            return Response
                    .status(Response.Status.OK)
                    //若不用.type(.)设置MediaType，则自动使用请求时Accept属性值
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
    @RolesAllowed({"SUPER_USER","COMMON_USER"})
    @Path("{id}")
    public Response eraseStudent(@PathParam("id") Integer id){
        if (StuRepository.deleteStudent(id)){
            return Response.ok(id.toString()).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(id.toString())
                .type(randomMediaType())
                .build();
    }
}