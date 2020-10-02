package cxiao.sh.cn.resource;

import cxiao.sh.cn.Server;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
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
    public void download_Asyn(@Suspended final AsyncResponse asyncResponse, @PathParam("name") String fileName){
        Server.pool.submit(
                ()->{
                    try {
                        asyncResponse.resume(download(fileName));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
        );
    }
    private Response download(String fileName) throws IOException {
        StudentResource.fakeLongTimeOperation();
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
    public void upload_Asyn(@Suspended final AsyncResponse asyncResponse, @FormDataParam("file") FormDataBodyPart bp) {
        Server.pool.submit(
                ()->{
                    asyncResponse.resume(upload(bp));
                }
        );
    }
    private Response upload(FormDataBodyPart bp) {
        StudentResource.fakeLongTimeOperation();
        FormDataContentDisposition disposition = bp.getFormDataContentDisposition();
        String fileName = disposition.getFileName();
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