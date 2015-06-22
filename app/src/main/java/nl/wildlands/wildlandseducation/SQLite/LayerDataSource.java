package nl.wildlands.wildlandseducation.SQLite;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import nl.wildlands.wildlandseducation.Layer;
import nl.wildlands.wildlandseducation.LayerImage;

/**
 * Class om de SQLite handeling voor de layers te verzorgen
 */
public class LayerDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] layerColumns = { MySQLiteHelper.COLUMN_LAYER_ID, MySQLiteHelper.COLUMN_THEMA_ID, MySQLiteHelper.COLUMN_LAYER_IMAGE};
    private String[] imageColumns = {MySQLiteHelper.COLUMN_LAYER_IMAGE_ID, MySQLiteHelper.COLUMN_THEMA_ID, MySQLiteHelper.COLUMN_LAYER_IMAGE_PATH, MySQLiteHelper.COLUMN_LAYER_IMAGE_NAME};


    public LayerDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Voeg een nieuwe layer toe
     * @param themaId
     * @param image
     * @return
     */
    public Layer createLayer(int themaId, String image)
    {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_THEMA_ID, themaId);
        values.put(MySQLiteHelper.COLUMN_LAYER_IMAGE, image);
        long insertId = database.insert(MySQLiteHelper.TABLE_LAYERS, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_LAYERS,
                layerColumns, MySQLiteHelper.COLUMN_LAYER_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Layer newLayer = cursorToLayer(cursor);
        cursor.close();
        return newLayer;
    }

    /**
     * Voeg een nieuwe layerimage toe
     * @param themaId
     * @param path
     * @param name
     * @return
     */
    public LayerImage createLayerImage(int themaId, String path, String name)
    {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_LAYER_THEMA_ID, themaId);
        values.put(MySQLiteHelper.COLUMN_LAYER_IMAGE_PATH, path);
        values.put(MySQLiteHelper.COLUMN_LAYER_IMAGE_NAME, name);
        long insertId = database.insert(MySQLiteHelper.TABLE_LAYER_IMAGES, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_LAYER_IMAGES,
                imageColumns, MySQLiteHelper.COLUMN_LAYER_IMAGE_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        LayerImage newLayerImage = cursorToLayerImage(cursor);
        cursor.close();
        return newLayerImage;
    }

    /**
     *
     * @return alle lagen
     */
    public ArrayList<Layer> getAllLayers()
    {
        ArrayList<Layer> layers = new ArrayList<Layer>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_LAYERS, layerColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Layer layer = cursorToLayer(cursor);
            layers.add(layer);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return layers;
    }

    /**
     *
     * @return alle layerimages
     */
    public ArrayList<LayerImage> getAllLayerImages()
    {
        ArrayList<LayerImage> layerImages = new ArrayList<LayerImage>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_LAYER_IMAGES, imageColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            LayerImage layerImage = cursorToLayerImage(cursor);
            layerImages.add(layerImage);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return layerImages;
    }


    /**
     * Cursor voor layer
     * @param cursor
     * @return layer
     */
    private Layer cursorToLayer(Cursor cursor)
    {
        Layer layer = new Layer(cursor.getLong(0), cursor.getInt(1), cursor.getString(2));
        return layer;
    }

    /**
     * Cursor voor layerimage
     * @param cursor
     * @return layerimage
     */
    private LayerImage cursorToLayerImage(Cursor cursor)
    {
        LayerImage layerImage = new LayerImage(cursor.getLong(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3));
        return layerImage;
    }






}