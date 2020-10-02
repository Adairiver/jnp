package cxiao.sh.cn.resource;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.stereotype.Component;

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

@Component
@Path("file")
public class FileResource {
    private final String folderOnServer = "D:\\ServerFiles\\";

    @GET
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

    @GET
    @Path("get")
    // 用来与 downlaod(.) 对比，并不使用
    public void download0(@QueryParam("name") String fileName, @Context HttpServletResponse response) throws IOException {
        response.setHeader("Content-disposition", "attachment;filename="+fileName);
        response.setHeader("Cache-Control", "no-cache");
        File f = new File(folderOnServer + fileName);
        //若文件不存在的情况
        if (!f.exists()) {
            response.setStatus(Response.Status.NOT_FOUND.getStatusCode());
            response.getOutputStream().close();
            return;
        }
        OutputStream os = response.getOutputStream();
        byte[] buffer = new byte[1024*1024];
        int len = 0;
        InputStream is = new FileInputStream(f);
        while( (len=is.read(buffer)) != -1 ){
            os.write(buffer, 0, len);
        }
        is.close();
        os.flush();
        response.setStatus(Response.Status.OK.getStatusCode());
        response.getOutputStream().close();
    }


}