package olmo.eduardo.galeriapublica;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GalleryRepository {

    Context context;

    // construtor que recebe um objeto do tipo Context, pelo qual o app vai ter acesso ao conteudo da galeria do celular
    public GalleryRepository (Context context) {
        this.context = context;
    }

    // limit -> o número de elementos que devem ser carregados;
    // offset -> o índice a partir do qual os elementos devem ser carregados.
    // O método loadImageData retorna uma lista de objetos ImageData, a qual contém somente a quantidade de itens referentes a uma página.
    public List<ImageData> loadImageData (Integer limit, Integer offSet) throws FileNotFoundException {

        // lista que vai ser preenchida com os itens de uma página
        List<ImageData> imageDataList = new ArrayList<>();

        // pegando as dimensoes da miniatura de cada foto
        int w = (int) context.getResources().getDimension(R.dimen.im_width);
        int h = (int) context.getResources().getDimension(R.dimen.im_height);

        // MediaStore.Images.Media = tabela que guarda as imagens
        //● _ID -> o id do arquivo de fotos, usado para construir o endereço uri;
        //● DISPLAY_NAME -> o nome do arquivo de foto;
        //● DATE_ADDED -> a data em que a foto foi criada;
        //● SIZE -> tamanho do arquivo em bytes;
        String[] projection = new String[] {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.SIZE
        };

        // definindo qual subconjunto de dados iremos obter, setamos esse parametro como nulo, pois queremos todas as fotos
        String selection = null;
        // definindo os argumentos para o selection, como selection e nulo, selectionArgs tambem e nulo
        String selectionArgs[] = null;
        // definindo qual coluna sera usada para ordenar os dados, nesse caso, eles serao ordenados de acordo com a data de criacao
        String sort = MediaStore.Images.Media.DATE_ADDED;

        Cursor cursor = null;
        // conferindo a versao do Android, para executar uma query que funcione
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            // Um Bundle é uma estrutura que guarda tuplas do tipo chave=valor
            Bundle queryArgs = new Bundle();

            // definindo os parâmetros de selection e selectionArgs
            queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection);
            queryArgs.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs);

            // sort
            // definem quais colunas devem ser usadas para a ordenação do resultado da consulta
            queryArgs.putString(ContentResolver.QUERY_ARG_SORT_COLUMNS, sort);
            // indicando qual será a direção da ordenação
            queryArgs.putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION, ContentResolver.QUERY_SORT_DIRECTION_ASCENDING);

            // limit, offset
            // indicando os parâmetros de LIMIT e OFFSET
            queryArgs.putInt(ContentResolver.QUERY_ARG_LIMIT, limit);
            queryArgs.putInt(ContentResolver.QUERY_ARG_OFFSET, offSet);

            //Uma vez definidos os parâmetros da consulta, ela é realizada através do ContentResolver,
            //o qual é obtido do contexto da aplicação. O resultado é retornado em um objeto do tipo Cursor.
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, queryArgs, null);

        }
        else {
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sort + " ASC + LIMIT " + String.valueOf(limit) + " OFFSET " + String.valueOf(offSet));
        }

        // obtendo os dados das fotos
        // obtendo o ID que e utilizado para contruir o endereco URI da foto
        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        // obtendo o nome da foto
        int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
        // obtendo a data da foto
        int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
        // obtendo o tamanho da foto
        int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);

        while (cursor.moveToNext()) {
            // get values of columns for a given image
            // obtendo o ID que e utilizado para contruir o endereco URI da foto
            long id = cursor.getLong(idColumn);
            Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            // obtendo o nome da foto
            String name = cursor.getString(nameColumn);
            // obtendo a data da foto
            int dateAdded = cursor.getInt(dateAddedColumn);
            // obtendo o tamanho da foto
            int size = cursor.getInt(sizeColumn);
            Bitmap thumb = Util.getBitmap(context, contentUri, w, h);

            // stores column values and the contentUri in a local object that represents the media file
            // adicionando um objeto do tipo ImageData na lista
            imageDataList.add(new ImageData(contentUri, thumb, name, new Date(dateAdded*1000L), size));

        }
        //retornando a lista que contem os itens referentes a uma pagina de dados
        return imageDataList;

    }


}
