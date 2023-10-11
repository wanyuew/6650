package main.java;

public class ImageMetaData {
    protected String albumId;
    protected String imageSize;

    public ImageMetaData(){}

    public ImageMetaData(String albumId, String imageSize) {
        this.albumId = albumId;
        this.imageSize = imageSize;
    }

    //getters
    public String getAlbumId() {
        return albumId;
    }

    public String getImageSize(){
        return imageSize;
    }

    //setters
    public void setAlbumId(String albumId){
        this.albumId = albumId;
    }

    public void setImageSize(String imageSize){
        this.imageSize = imageSize;
    }
}
