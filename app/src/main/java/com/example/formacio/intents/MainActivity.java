package com.example.formacio.intents;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private Button takePictureButton;
    private Button listPictureButton;
    private Button goURLButton;
    private Button makeCallButton;
    private ImageView imageView;
    private Uri photoURI;
    EditText edWeb;
    EditText edCall;
    private static final int SELECT_FILE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        takePictureButton = (Button) findViewById(R.id.takePicture);
        listPictureButton = (Button) findViewById(R.id.listPicture);
        goURLButton = (Button) findViewById(R.id.goURL);
        makeCallButton = (Button) findViewById(R.id.call);
        edWeb = findViewById(R.id.editWeb);
        edCall = findViewById(R.id.editCall);

        imageView = (ImageView) findViewById(R.id.imageView);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            takePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

    }

    public void takePicture(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

       //photoURI = FileProvider.getUriForFile(view.getContext(), view.getContext().getPackageName() + ".my.package.name.provider", getOutputMediaFile());//esto funciona
       photoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".my.package.name.provider", getOutputMediaFile());//tb funciona
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

        /*file = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);*/

        startActivityForResult(intent, 100);//exception ??
    }

    public void listPicture(View view) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
         startActivityForResult(
                Intent.createChooser(intent, "Seleccione una imagen"),
                SELECT_FILE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePictureButton.setEnabled(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                imageView.setImageURI(photoURI);
            }
        }

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                super.onActivityResult(requestCode, resultCode, data);
                Uri selectedImageUri = null;
                Uri selectedImage;

                String filePath = null;
                switch (requestCode) {
                    case SELECT_FILE:
                        if (resultCode == RESULT_OK) {
                            selectedImage = data.getData();
                            String selectedPath = selectedImage.getPath();
                            if (requestCode == SELECT_FILE) {

                                if (selectedPath != null) {
                                    InputStream imageStream = null;
                                    try {
                                        imageStream = getContentResolver().openInputStream(selectedImage);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }

                                    // Transformamos la URI de la imagen a inputStream y este a un Bitmap
                                    Bitmap bmp = BitmapFactory.decodeStream(imageStream);

                                    // Ponemos nuestro bitmap en un ImageView que tengamos en la vista
                                    imageView.setImageBitmap(bmp);

                                }
                            }
                        }
                        break;
                }
            }
        }
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("CameraDemo", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
    }

    public void goURL(View view) {

        /*Uri uri = Uri.parse("http://www.google.com");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);*/

        String name = (String) edWeb.getText().toString();
        if (name != null) {
            Uri uri = Uri.parse("http://"+ name);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        } else {
            Toast.makeText(this, "error url", Toast.LENGTH_SHORT).show();


        }
    }

    public void goCall(View view) {
        String phone = (String) edCall.getText().toString();
       if (phone != "") {
            Intent i = new Intent(Intent.ACTION_DIAL);
            i.setData(Uri.parse("tel:" + phone));
            startActivity(i);
        } else {
            Toast.makeText(this, "error number", Toast.LENGTH_SHORT).show();


        }
    }

}
