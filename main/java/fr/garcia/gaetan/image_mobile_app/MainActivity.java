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
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
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
                }

            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Bitmap myImage = ((BitmapDrawable) mDisplayImageView.getDrawable()).getBitmap();
                try {
                    getHttpResponse();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

    }
    public String getFileDataFromDrawable(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    public Object getHttpResponse() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("http://192.168.1.15:8080/world/api")
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        return response.message();
        /*Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            Log.e("TAG", "error in getting response get request okhttp");
        }*/
    }

    /*protected void sendTheImage(Bitmap bmp) throws IOException {
        final String image = getFileDataFromDrawable(bmp);
        //String urlPost = "http://78.221.250.17:8000/apiImage/post";
        String urlPost = "192.168.1.15:8080/world/api/";

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,urlPost,null,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response",response.toString());
                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", "Error de post");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                long imageName = System.currentTimeMillis();
                params.put("image", "Hello");
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);


    }*/




    /*protected void receivetheimage() throws IOException {
        //String urlpost = "http://78.221.250.17:8000/";
        String urlget = "192.168.1.15:8080/world/api/";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, urlget, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response: ", response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Response: ", "ERROR GET");

                    }
                });

// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);


    }*/

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