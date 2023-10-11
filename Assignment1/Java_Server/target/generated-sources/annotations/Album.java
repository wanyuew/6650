public class Album {
    protected String id;
    protected String artist;
    protected String year;
    protected String title;
    protected ImageMetaData  imgData;

    public Album(String id, String artist, String year, String title){
        this.id = id;
        this.artist = artist;
        this.year = year;
        this.title = title;
    }

    public Album(String id, String artist, String year, String title, ImageMetaData imgData){
        this.id = id;
        this.artist = artist;
        this.year = year;
        this.title = title;
        this.imgData = imgData;
    }

}

