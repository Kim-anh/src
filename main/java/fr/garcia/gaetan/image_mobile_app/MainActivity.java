package fr.garcia.gaetan.image_mobile_app;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/*
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;*/

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    //Declaration des elements de l application :
    private Button mCaptureButton;
    private Button mLibraryButton;
    private Button mSearchButton;
    private ImageView mDisplayImageView;
    public static final int PICK_IMAGE = 2;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int CTE_CAMERA = 0;
    static final int CTE_GALLERY = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCaptureButton = (Button) findViewById(R.id.activity_main_capture_button);
        mLibraryButton = (Button) findViewById(R.id.activity_main_library_button);
        mSearchButton = (Button) findViewById(R.id.activity_main_search_button);
        mDisplayImageView = (ImageView) findViewById(R.id.activity_main_image_clothes);

        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                //   Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                //        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                //    }
                if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,CTE_CAMERA);
                }else{
                    requestPermissions(new String[]{android.Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        mLibraryButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, CTE_GALLERY);

                }else{
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE);
                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE);
                }

            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                Bitmap myImage = ((BitmapDrawable) mDisplayImageView.getDrawable()).getBitmap();
                new MyTask().execute(myImage);

            }
        });

    }
    public String getFileDataFromDrawable(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private class MyTask extends AsyncTask<Bitmap, Void, String> {
        String result;


        @Override
        protected String doInBackground(Bitmap... bitmaps) {
        Bitmap img = bitmaps.clone()[0];
        postimage(img);


            return "hello";
        }
    }
    protected void postimage(Bitmap bitmap){
        String y = getFileDataFromDrawable(bitmap);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");




        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("image",y)
                .build();
        Request request = new Request.Builder()
                .url("http://78.221.250.17:8000/apiImage/post")
                .method("POST", body)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.w("failure Response", mMessage);
                //call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String mMessage = response.body().string();
                Log.e("text", mMessage);
            }

        });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //rajouter resultCode
        if(requestCode == 0 && resultCode== Activity.RESULT_OK) {
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            mDisplayImageView.setImageBitmap(bitmap);


        } else if (requestCode == 1 && resultCode== Activity.RESULT_OK){

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

            mDisplayImageView.setImageBitmap(bitmap);

        }
    }
}