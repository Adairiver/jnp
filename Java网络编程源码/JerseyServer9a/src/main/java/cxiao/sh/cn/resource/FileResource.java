package cxiao.sh.cn.resource;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.UUID;

/**
 * @program: Java网络编程进阶
 * @author:  Xiao Chuan
 * @email:   cxiao@fudan.edu.cn
 * @create:  2020.09
 **/

@Path("file")
@PermitAll
public class FileResource {
    private final String folderOnServer = "D:\\ServerFiles\\";

    @GET
    @RolesAllowed("COMMON_USER")
    @Path("/{name}")
    public Response download(@PathParam("name") String fileName) throws IOException {
        File f = new File(this.folderOnServer + fileName);
        if (!f.exists()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response
                    .ok(f)
                    .header("Content-disposition", "attachment;filename=" + fileName)
                    .header("Cache-Control", "no-cache")
                    .build();
        }
    }

    @POST
    @RolesAllowed("SUPER_USER")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response upload(@FormDataParam("file") FormDataBodyPart bp) {
        FormDataContentDisposition disposition = bp.getFormDataContentDisposition();
        String fileName = disposition.getFileName();
        //上传至服务端时文件名自动增加UUID以避免和已有文件重名
        File file = new File(this.folderOnServer + UUID.randomUUID().toString() + "-" + fileName);
        try {
            InputStream is = bp.getValueAs(InputStream.class);
            OutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024*1024];
            int len = 0;
            while( (len=is.read(buffer)) != -1 ){
                fos.write(buffer, 0, len);
                fos.flush();
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.notModified().build();
        }
        return Response.ok().build();
    }
}