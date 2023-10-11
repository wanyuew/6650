import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import java.util.stream.Collectors;

@MultipartConfig
@WebServlet(name="JavaServlet", value = "/albums/*")
public class AlbumServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().write("789");
        //set the content type to indicate JSON Response
//        response.setContentType("application/json");
//        Gson gson = new Gson();
//        String artist = "The Bollocks";
//        String name = "Sex Pistols";
//        String year = "1971";
//
//        try {
//            //check if there is a URL
//            String urlPath = request.getPathInfo();
//            if (urlPath == null || urlPath.isEmpty()) {
//                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//                response.getWriter().write("missing paramterers!");
//                return;
//            }
//            //check if url is valid
//            String[] urlPaths = urlPath.split("/");
//            if (!isValidUrl(urlPaths)) {
//                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//                response.getWriter().write("The url is not valid!");
//            } else {
//                String id = urlPaths[urlPath.length() - 1];
//                Album album = new Album(id, artist, year, name);
//                response.setStatus(HttpServletResponse.SC_OK);
//                response.getOutputStream().print(gson.toJson(album));
//                response.getOutputStream().flush();
//            }
//        }catch(Exception ex) {
//            ex.printStackTrace();
//            String errorMsg = "The URL cannot be proccessed";
//            response.getOutputStream().print(gson.toJson(errorMsg));
//            response.getOutputStream().flush();
//            return;
//        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().write("789");
        return;
//        String jsonPayload = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
//
//        // Parse the JSON payload
//        JsonObject jsonAlbum;
//        try (JsonReader reader = Json.createReader(new StringReader(jsonPayload))) {
//            jsonAlbum = reader.readObject();
//        } catch (Exception e) {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            response.getWriter().write("Invalid JSON payload");
//            return;
//        }
//
//        // Extract album information from JSON
//        JsonObject albumInfo = jsonAlbum.getJsonObject("albumInfo");
//        if (albumInfo == null) {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            response.getWriter().write("Missing 'albumInfo' in JSON payload");
//            return;
//        }
//
//        String title = albumInfo.get("title").toString();
//        String artist = albumInfo.get("artist").toString();
//        String year = albumInfo.get("year").toString();
//
//
//        // Validate input
//        if (title == null || artist == null || year == null || title.isEmpty() || artist.isEmpty() || year.isEmpty()) {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            response.getWriter().write("Invalid albumInfo parameters");
//            return;
//        }
//
//        // Create a new album
//        Album newAlbum = new Album(nextAlbumId++, title, artist, year);
//        // Set other attributes...
//
//        // Add the new album to your collection
//        albums.add(newAlbum);
//
//        // Return a success response with the album key
//        response.setStatus(HttpServletResponse.SC_CREATED);
//        response.getWriter().write("Album created successfully. Album Key: " + newAlbum.getId());
    }

    private Boolean isValidUrl(String[] urlPath) {
        // TODO: validate the request url path according to the API spec
        return true;
    }
}
