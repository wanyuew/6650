package main.java.util;

public class Album {
    protected int id;
    protected String artist;
    protected String year;
    protected String title;
    protected byte[] imgData;
    protected String imageSize;

    public Album() {}
    public Album(int id, String artist, String year, String title){
        this.id = id;
        this.artist = artist;
        this.year = year;
        this.title = title;
    }


    public Album(int id, String artist, String year, String title, byte[] imgData, String imageSize){
        this.id = id;
        this.artist = artist;
        this.year = year;
        this.title = title;
        this.imgData = imgData;
        this.imageSize = imageSize;
    }

    //getters
    public int getId() {
        return this.id;
    }

    public String getArtist() {return this.artist;}

    public String getYear() {return this.year;}

    public String getTitle() {return this.title;}

    public byte[] getImgData() {return this.imgData;}
    
    public String getImageSize() {return this.imageSize;}

    //setters
    public void setId(int id) {this.id = id;}

    public void setArtist(String artist) {this.artist = artist;}
    public void setYear(String year) {this.year = year;}
    public void setTitle(String title) {this.title = title;}

    public void setImgData(byte[] imgData) {this.imgData = imgData;}
    
    public void setImageSize(String imageSize) {this.imageSize = imageSize;}
}

