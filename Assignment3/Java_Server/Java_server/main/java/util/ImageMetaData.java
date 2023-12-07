package main.java.util;

public class ImageMetaData {
    protected int albumId;
    protected String imageSize;

    public ImageMetaData(){}

    public ImageMetaData(int albumId, String imageSize) {
        this.albumId = albumId;
        this.imageSize = imageSize;
    }

    //getters
    public int getAlbumId() {
        return albumId;
    }

    public String getImageSize(){
        return imageSize;
    }


    //setters
    public void setAlbumId(int albumId){
        this.albumId = albumId;
    }

    public void setImageSize(String imageSize){
        this.imageSize = imageSize;
    }

}
