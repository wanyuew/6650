package main.java;

        import com.google.gson.Gson;

        import javax.servlet.ServletException;
        import javax.servlet.annotation.MultipartConfig;
        import javax.servlet.annotation.WebServlet;
        import javax.servlet.http.HttpServlet;
        import javax.servlet.http.HttpServletRequest;
        import javax.servlet.http.HttpServletResponse;
        import javax.servlet.http.Part;
        import java.io.IOException;
        import java.io.InputStream;

@MultipartConfig
@WebServlet(name="JavaServlet", value = "/albums/*")
public class AlbumServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //set the content type to indicate JSON Response
        response.setContentType("application/json");
        Gson gson = new Gson();

        String artist = "The Bollocks";
        String name = "Sex Pistols";
        String year = "1971";

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
                String id = urlPaths[urlPath.length() - 1];
                Album album = new Album(id, artist, year, name);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getOutputStream().print(gson.toJson(album));
                response.getOutputStream().flush();
            }
        }catch(Exception ex) {
            ex.printStackTrace();
            String errorMsg = "The URL cannot be proccessed";
            response.getOutputStream().print(gson.toJson(errorMsg));
            response.getOutputStream().flush();
        }
        //TODO: retrieve data from storage location
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        Gson gson = new Gson();

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
            Part filePart = request.getPart("image");
            if (filePart != null) {
                InputStream fileContent = filePart.getInputStream();
                long fileSize = filePart.getSize();
                size = String.valueOf(fileSize);
            }

            //create image
            ImageMetaData newImage = new ImageMetaData();
            String albumId = "100";
            newImage.setAlbumId(albumId);
            newImage.setImageSize(size);
            System.out.print("the size is:" + newImage.getImageSize());

            // Return a success response with the album key
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("Album created successfully. Album Key: " + newImage.getAlbumId());
            response.getWriter().write("The size: " + newImage.getImageSize() +" bytes");

        }
        catch (Exception ex){
            ex.printStackTrace();
            String errorMsg = "Image Upload Failure";
            response.getOutputStream().print(gson.toJson(errorMsg));
            response.getOutputStream().flush();
        }
    }

    private Boolean isValidUrl(String[] urlPath) {
        // TODO: validate the request url path according to the API spec
        return true;
    }
}