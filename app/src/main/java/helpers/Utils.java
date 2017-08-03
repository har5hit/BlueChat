package helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.justadeveloper96.bluechat.BuildConfig;
import com.justadeveloper96.bluechat.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Harshith on 21/2/17.
 */

public class Utils {


	//put ur application context object here
    public static Context getContext()
    {
        return MyApplication.getINSTANCE();
    }

    //views

    public static String getText(EditText t)
    {
        String temp=t.getText().toString().trim();
        return temp;
    }


    public void hidekeyboard(View view) {
        Context context = view.getContext();
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(((Activity) context)
                .getCurrentFocus().getWindowToken(), 0);
    }


    
    //Image

    public static int getImageOrientation(Context context, Uri contentPhotoUri) {
        Cursor cursor = context.getContentResolver().query(contentPhotoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION},
                null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            } else {
                return -1;
            }
        }catch(Exception e){
            return 0;
        }
    }

    public static int getImageOrientation(Uri contentPhotoUri){
        Context context = getContext().getApplicationContext();
        String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
        Cursor cur = context.getContentResolver().query(contentPhotoUri, orientationColumn, null, null, null);
        int orientation = -1;
        if (cur != null && cur.moveToFirst()) {
            orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
        }else{
            Log.d("EXIF", "File: 0 results from query");
        }
        return orientation;
    }

    public static int getImageOrientation(String file) {

        Context context = getContext();
        Uri uri = getImageContentUri(context,new File(file));
        if(uri != null) {

            Cursor cursor = context.getContentResolver().query(uri,
                    new String[]{MediaStore.Images.ImageColumns.ORIENTATION},
                    null, null, null);

            try {
                if (cursor != null && cursor.moveToFirst()) {
                    Log.d("EXIF","Orientation found");
                    return cursor.getInt(0);
                } else {
                    return -1;
                }
            } catch (Exception e) {
                Log.d("EXIF","Orientation Exception");
                return 0;
            }
        }else{
            Log.d("EXIF","URI is null");
            return 0;
        }
    }

    public static int getExifOrientation(String filepath) {// YOUR MEDIA PATH AS
        // STRING
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }

            }
        }
        return degree;
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            Utils.log("EXIF","URI found");
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        }else {
            if (imageFile.exists()) {
                Utils.log("EXIF","Inserting URI");
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public static String getFilePathFromUri(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.DATA}, null,
                null, null);

        try {
            if (cursor.moveToFirst()) {
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(column_index);
            } else {
                return "";
            }
        }catch (Exception e){
            return photoUri.getPath();
        }
    }

    public static String getDateFromTimestamp(long timestamp,String format) {
        return (String) android.text.format.DateFormat.format(format,
                timestamp);
    }


    public static String saveImageToLocalDirectory(Bitmap bitmap,Context context, String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/"));
        String extStorageDirectory = Environment
                .getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).toString();
        File folder = new File(extStorageDirectory, getFolderName());
        folder.mkdir();

        if (!folder.exists()) {
            Log.d("database",
                    "Sd card does not exist saving in app directory");
            File mypath = new File(folder, fileName);

            ContextWrapper cw = new ContextWrapper(context);
            folder = cw.getDir("imageDir", Context.MODE_PRIVATE);
        }

        File mypath = new File(folder, fileName);

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to
            // the OutputStream
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mypath.getAbsolutePath();
    }

    @NonNull
    private static String getFolderName() {
        return getContext().getResources().getString(R.string.app_name);
    }


    public static void notifyGalleryScanning(String path) {
        Context context=getContext();
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri uri = Uri.fromFile(f);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                getFolderName());

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                mediaStorageDir      /* directory */
        );

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
        intent.setData(uri);
        context.sendBroadcast(intent);

        return image;
    }
    
    public static File getOutputMediaFile(Context context,String filename) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                getFolderName());
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("file", "failed to create directory");
                return null;
            }
        }
        // Create a media file name

        File mediaFile;


        if (filename==null || filename.isEmpty()) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                    .format(new Date());
            filename = "IMG_" + timeStamp;
        }

        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                +filename+".jpg");

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
        intent.setData(uri);
        context.sendBroadcast(intent);
        return mediaFile;
    }


    public static String takeImageFromCameraAndSave(Context ctx,String filename,int requestCode) throws IOException
    {
        File file= getOutputMediaFile(ctx,filename);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getFileProviderUri(file));
        }else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        }

        if (ctx instanceof Activity)
            ((Activity) ctx).startActivityForResult(intent,requestCode);


        return file.getPath();
    }



    private static Uri getFileProviderUri(File file) {
        return FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID +".provider",file);
    }


    public static void deleteFileRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteFileRecursive(child);
        boolean status=fileOrDirectory.delete();
        Utils.log(fileOrDirectory.getName()+" "+status);
    }



    public static Bitmap loadImageFromStorage(String path) {
        try {
            File f = new File(path);
            return BitmapFactory.decodeStream(new FileInputStream(f));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean imageExist(String path) {
        File f = new File(path);
        if (f.exists()) {
            return true;
        } else {
            return false;
        }
    }


    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null,
                    null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }catch(Exception e){
            return contentUri.getPath();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static Bitmap scaleBitmapToHD(Bitmap bitmap) {
        int destX = 1280;
        int destY = 720;
		/*
		 * int srcX = bitmap.getWidth(); int scrY = bitmap.getHeight();
		 *
		 * double scaleX, scaleY; scaleX = destX / srcX; scaleY = destY / scrY;
		 * double scale = Math.min( scaleX, scaleY );
		 */
        return Bitmap.createScaledBitmap(bitmap, destX, destY, false);
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }



    //location

    public static boolean areLocationServicesEnabled(Context context){
        LocationManager lm = null;
        boolean gps_enabled = false,network_enabled = false;
        if(lm==null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try{
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex){}
        try{
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception ex){}

        if(!gps_enabled && !network_enabled){
            return false;
        }else{
            return true;
        }
    }


    //time

    public static String getRelativeTime(long timestamp){
        return DateUtils.getRelativeTimeSpanString(timestamp*1000, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString();
    }


    //video


    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }


    //screen

    public static float convertPixelsToDp(float px){
        Resources resources = getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static float convertDpToPixel(float dp){
        Resources resources = getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }



    public static AlertDialog openSettingAlert(String title, String msg, final AppCompatActivity app)
    {
        return new AlertDialog.Builder(app).setTitle(title).setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + app.getPackageName()));
                        app.startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    //network




    //reporting

    public static void showToast(Context ctx,String msg)
    {
        if(ctx!=null&&!msg.isEmpty())
        {

            Toast.makeText(ctx,msg,Toast.LENGTH_SHORT).show();
        }



    }

    public static void log(String tag,String k)
    {
        if (BuildConfig.DEBUG)
        {
            Log.d(tag,k.toString());
        }
    }

    public static void log(String k)
    {
        log(getContext().getPackageName(),k.toString());
    }

    public static boolean isNetworkAvailable() {
        Context context = getContext();
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


    public static String getNetworkErrorText(Throwable t)
    {
        if (t instanceof SocketException)
        {
            return "";
        }

        if (!isNetworkAvailable())
        {
            Utils.log("no internet");
            return "No Internet Connection!";
        }

        if(t instanceof IOException)
        {
            return "";
        }

        return "Failed";
    }


    //data

    public static String removeLastCommaFromString(String str) {
        str = str.trim();
        str=str.length()>0?str.substring(1):str;
        return str;
    }

}
