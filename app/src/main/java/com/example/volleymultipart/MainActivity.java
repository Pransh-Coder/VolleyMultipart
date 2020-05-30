package com.example.volleymultipart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button choose, upload;
    ImageView imageView,img;
    RequestQueue queue;
    final int CODE_GALLERY_REQUEST = 777;
    private static final int PICK_IMAGE=1;
    Uri filePath;
    Bitmap bitmap;
    EditText entertext;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        choose = findViewById(R.id.choose);
        upload = findViewById(R.id.upload);
        imageView = findViewById(R.id.img);
        img = findViewById(R.id.img2);
        entertext=findViewById(R.id.entertext);

        queue = Volley.newRequestQueue(this);

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CODE_GALLERY_REQUEST);
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    startActivityForResult(Intent.createChooser(intent, "SelectPicture"), PICK_IMAGE);
                }

            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if the tags edittext is empty
                //we will throw input error
               /* if (entertext.getText().toString().trim().isEmpty()) {
                    entertext.setError("Add text before uploading an image!");
                }
                else{
                    UploadImage(bitmap);
                }*/
                UploadImage(bitmap);
            }
        });
    }

 /*   @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == CODE_GALLERY_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Image"), CODE_GALLERY_REQUEST);
            } else {
                Toast.makeText(getApplicationContext(), "Permission not granted!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK ) {        //&& data != null
            Log.e("Info","Image to be uploaded");
            filePath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                Log.e("Image inside onActivity",bitmap.toString());
                imageView.setImageBitmap(bitmap);
                //UploadImage(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void UploadImage(final Bitmap bitmap) {
        //text=entertext.getText().toString().trim();
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, "http://paytmpay001.dx.am/api/raeces/uploadPdf.php", new Response.Listener<NetworkResponse>() {      //https://dry-chamber-48445.herokuapp.com/post
            @Override
            public void onResponse(NetworkResponse response) {
                Log.e("UploadImage", new String(response.data));
                /*byte[] bitmap1 = response.data;                     // png img converted into byte array png images are in form of 0 and 1
                Bitmap bitmap2 = BitmapFactory.decodeByteArray(bitmap1,0,bitmap1.length);       //decoding bytearray to bitmaps to set it in imageView
                //Bitmap bitmap2 = BitmapFactory.decodeByteArray(response.data,0,response.data.length);
                img.setImageBitmap(bitmap2); //setting the bitmap image in next imageView i.e at the bottom
                Log.d("line 128","After setting recieved image from server into imageview");*/
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"error: "+error,Toast.LENGTH_SHORT).show();
                Log.e("error","error: "+error);
            }
        }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("name",imagename + ".png");
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("pdf", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));      //our key is "file"
                //System.out.println(getFileDataFromDrawable(bitmap).toString());
                //Toast.makeText(MainActivity.this,""+getFileDataFromDrawable(bitmap).toString(),Toast.LENGTH_SHORT).show();
                return params;
            }
        };
        queue.add(multipartRequest);

    }
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
        /*System.out.println(byteArrayOutputStream.toByteArray().toString());
        String s=byteArrayOutputStream.toByteArray().toString();
        Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
        Log.println(1,"Byte[]",s);*/
        return byteArrayOutputStream.toByteArray();
    }
}