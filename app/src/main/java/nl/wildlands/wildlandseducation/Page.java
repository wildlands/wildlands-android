package nl.wildlands.wildlandseducation;

import android.widget.ImageView;

/**
 * Class om een pagina content van een pinpoint te laten zien
 * Deze heeft een paginanummer
 * Een titel
 * Een centrale afbeelding
 * Een beschrijving
 */
public class Page {

    private int pageNumber;
    private ImageView image;
    private String text;
    private String title;

    public Page(int pageNumber,ImageView image, String text, String title)
    {
        this.pageNumber = pageNumber;
        this.image = image;
        this.text = text;
        this.title = title;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
}
