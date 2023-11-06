package main.java;

import com.google.gson.Gson;
import main.java.dal.AlbumsDao;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.output.ByteArrayOutputStream;


@WebServlet(name="JavaServlet", value = "/albums/*")
@MultipartConfig(fileSizeThreshold = 1024*1024*10,
                maxFileSize = 1024*1024*50,
                maxRequestSize = 1024*1024*100)
public class AlbumServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //set the content type to indicate JSON Response
        response.setContentType("application/json");
        Gson gson = new Gson();


        try {
            //check if there is a URL
            String urlPath = request.getPathInfo();
            if (urlPath == null || urlPath.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("missing paramterers!");
                return;
            }
            //check if url is valid
            String[] urlPaths = urlPath.split("/");
            if (!isValidUrl(urlPaths)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("The url is not valid!");
            } else {
                int id = Integer.parseInt(urlPaths[urlPaths.length - 1]);
                Album album = AlbumsDao.getAlbumByAlbumId(id);

            //check if the album exist
                if (album != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getOutputStream().print(gson.toJson(album));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("The album does not exist!");
                }

            }
        }catch(Exception ex) {
            ex.printStackTrace();
            String errorMsg = "The URL cannot be proccessed, " + ex.getMessage();
            response.getOutputStream().print(gson.toJson(errorMsg));
            response.getOutputStream().flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        // Check if the request is multipart
        if (!ServletFileUpload.isMultipartContent(request)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Content type is not multipart/form-data");
            return;
        }

        //check if url is valid
        String urlPath = request.getPathInfo();
        if (urlPath == null || urlPath.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("the URL is invalid");
            return;
        }

        try {
            //extract image info from input
            String size = "0";
            byte[] imgData = null;


            Part filePart = request.getPart("image");
            if (filePart != null) {
                InputStream fileContent = filePart.getInputStream();
                long fileSize = filePart.getSize();
                size = String.valueOf(fileSize);

                // Convert InputStream to byte[]
                imgData = readFully(fileContent);
            }

            //other parameters
            String artist = request.getParameter("artist");
            String year = request.getParameter("year");
            String title = request.getParameter("title");

            //call post to db
            Album newAlbum = AlbumsDao.post(artist, year, title, imgData, size);

            // Return a success response with the album key
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("Album created successfully. Album Key: " + newAlbum.getId());
            response.getWriter().write("The size: " + newAlbum.getImageSize() +" bytes");
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
   }

    private Boolean isValidUrl(String[] urlPath) {
        // TODO: validate the request url path according to the API spec
        return true;
    }

    private byte[] readFully(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
    }
}