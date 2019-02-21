package com.wowls.boddari.ui.custom.gallery.vo;

/**
 * Created by woong on 2015. 10. 20..
 */
public class PhotoVO {

    private String imgPath;
    private boolean selected;

    public PhotoVO(String imgPath, boolean selected) {
        this.imgPath = imgPath;
        this.selected = selected;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
