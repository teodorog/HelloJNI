/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.hellojni;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Toast;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class HelloJni extends Activity {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;
    private static File myfile;

    /**
     * Crea un file Uri per salvare un' immagine
     */
    private static Uri getOutputMediaFileUri() {
        myfile = getOutputMediaFile();
        return Uri.fromFile(myfile);
    }

    /**
     * Crea un File per salvare un' immagine
     */
    private static File getOutputMediaFile() {

        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return null;
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "hellojni");
        // questa locazione lavora meglio se vuoi creare un' immagine che deve essere condivisa
        // tra applicazioni

        // Crea una directory se non esiste
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("hellojni", "failed to create directory");
                return null;
            }
        }

        // Crea il nome di un file immagine
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + timeStamp + ".jpg");

        return mediaFile;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*  il testo è recuperato chiamando una funzione nativa.
         */
        setContentView(R.layout.main_layout);
        TextView mTextView = (TextView) findViewById(R.id.jniTextView);
        final String myStringa = stringFromJNI();
        mTextView.setText(myStringa);

        // crea un Intent perottenre un' immagine
        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(); // crea un file per salvarel' immagine
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set il nome del file immagine

        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // immagine catturata e salvata nel fileUri specificato dall' Intent
                Toast.makeText(this, "immagine salvata in\n" + myfile.toString(), Toast.LENGTH_LONG).show();
                ImageView mImageView = (ImageView) findViewById(R.id.voltoView);
                Bitmap mBitmap = BitmapFactory.decodeFile(fileUri.getEncodedPath());
                Bitmap rBitmap = rotateBitmap(mBitmap, 270);
                mImageView.setImageBitmap(rBitmap);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "immagine cancellata dall' utente\n", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "acquisizione immagine fallita\n", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void parteCamera(View cameraButton) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(); // crea un file per salvarel' immagine
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set il nome del file immagine

        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

    }


    /* A native method that is implemented by the  'hello-jni' native library, which is packaged
     * with this application.
     */
    public native String stringFromJNI();

    /* This is another native method declaration that is *not* implemented by 'hello-jni'.
     This is simply to show that you can declare as many native methods in your Java code
     * as you want, their implementation is searched in the currently loaded native libraries only the first time
     * you call them.
     *
     * Trying to call this function will result in a java.lang.UnsatisfiedLinkError exception !
     */
    public native String unimplementedStringFromJNI();

    /* this is used to load the 'hello-jni' library on application startup.
       The library has already been unpacked into /data/data/com.example.hellojni/lib/libhello-jni.so at
     * installation time by the package manager.
     */
    static {
        System.loadLibrary("hello-jni");
    }


    public static Bitmap rotateBitmap(Bitmap bmOrg, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        float newHeight = bmOrg.getHeight();
        float newWidth = bmOrg.getWidth() / 100 * (100.0f / bmOrg.getHeight() * newHeight);

        return Bitmap.createBitmap(bmOrg, 0, 0, (int) newWidth, (int) newHeight, matrix, true);
    }
}