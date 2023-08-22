package olmo.eduardo.galeriapublica;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.Date;

public class ImageData {
    public Uri uri; //endereco URI da foto
    public Bitmap thumb; // imagem em miniatura da foto
    public String fileName; // nome do arquivo de foto
    public Date date; // data em que a foto foi tirada
    public int size; // tamanho em bytes do arquivo de foto

    public ImageData (Uri uri, Bitmap thumb, String fileName, Date date, int size){
        this.uri = uri;
        this.thumb = thumb;
        this.fileName = fileName;
        this.date = date;
        this.size = size;
    }

}
