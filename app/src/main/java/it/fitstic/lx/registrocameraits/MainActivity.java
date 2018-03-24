package it.fitstic.lx.registrocameraits;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;


import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String PDF_PATH = "./target/test/raw_pdf.pdf";
    public static final String IMAGE_PATH = "./target/test/raw_image.jpg";

    String path_pdf = Environment.getExternalStorageDirectory() + "/photopdf.pdf";
    String path = "";
    private static int CAMERA_PIC_REQUEST = 1;
    private ImageView mImagePreview;
    private Bitmap mImageRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mImagePreview = (ImageView) findViewById(R.id.camera_preview);
        setSupportActionBar(toolbar);

        checkCameraHardware(getApplicationContext());

        //bottone in basso RIGHT
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                path = Environment.getExternalStorageDirectory() + "/photo1.jpg";
                //open camera activity INTENT con pasasggio percorso immagine
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);

            }
        });

        //bottone delete bottom LEFT

        FloatingActionButton fab_delete = (FloatingActionButton) findViewById(R.id.fab2);
        fab_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DELETE local IMAGE
                //TODO delete from local storage!
                mImagePreview.setImageResource(0);
                mImageRegistro = null;
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //menu laterale LEFT
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //disabilitato menu top right (utile per impostare i settings)
        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            //bottone menu SCATTA
            path = Environment.getExternalStorageDirectory() + "/photo1.jpg";
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);

            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_convert) {

            //conversione PDF
            convertToPdf(path_pdf);

        } else if (id == R.id.nav_send) {

            //TODO da testare
            //TODO gestire nome e data foto

            if (mImageRegistro != null) {
                Toast.makeText(MainActivity.this, "percorso DB ancora da definite!!!",
                        Toast.LENGTH_LONG).show();

//                Bitmap thumbnail = mImageRegistro;
//                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
//                File destination = new File(Environment.getExternalStorageDirectory(),"temp.jpg");
//                FileOutputStream fo;
//                try {
//                    fo = new FileOutputStream(destination);
//                    fo.write(bytes.toByteArray());
//                    fo.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                new uploadFileToServerTask().execute(destination.getAbsolutePath());
//

                //test2:
//                String SERVER_POST_URL = ""; //TODO address
//                try {
//                    URL url = new URL(SERVER_POST_URL);
//                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
//                    c.setDoInput(true);
//                    c.setRequestMethod("POST");
//                    c.setDoOutput(true);
//                    c.connect();
//                    OutputStream output = c.getOutputStream();
//                    mImageRegistro.compress(Bitmap.CompressFormat.JPEG, 50, output);
//                    output.close();
//                    Scanner result = new Scanner(c.getInputStream());
//                    String response = result.nextLine();
//                    Log.e("ImageUploader", "Error uploading image: " + response);
//                    result.close();
//                } catch (IOException e) {
//                    Log.e("ImageUploader", "Error uploading image", e);
//                }


                //


                //test 3:
                new ImageUploadTask().execute();


            } else {
                Toast.makeText(MainActivity.this, "Nessuna immagine scattata",
                        Toast.LENGTH_LONG).show();

            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * Check camera attivabile
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // OK
            return true;
        } else {
            // KO
            Toast.makeText(this, "il dispositivo non ha la fotocamera abilitata",
                    Toast.LENGTH_LONG).show();
            return false;
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_PIC_REQUEST) {
            if (resultCode == -1) {
                mImageRegistro = (Bitmap) data.getExtras().get("data");
                //TODO gestire il ritorno RESULT
                mImagePreview.setImageBitmap(mImageRegistro);

                path = saveToInternalStorage(mImageRegistro);
            }
        }
    }


    //---------------------------------------------//

    //TODO - doing - gestire il salvataggio a DB - che sia Firebase o proprietario.
    //GESTIONE SALVATAGGI

    // region HTTP SEND IMAGE
    class ImageUploadTask extends AsyncTask<Void, Void, String> {
        private String webAddressToPost = "http://0.0.0.0:8080/IL PERCORSO QUI"; //TODO ADDRESS di upload

        // private ProgressDialog dialog;
        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Uploading...");
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(webAddressToPost);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setConnectTimeout(5000);

                MultipartEntity entity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                mImageRegistro.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] data = bos.toByteArray();
                ByteArrayBody bab = new ByteArrayBody(data, "test.jpg");
                entity.addPart("file", bab);

                entity.addPart("someOtherStringToSend", new StringBody("your string here"));

                conn.addRequestProperty("Content-length", entity.getContentLength() + "");
                conn.addRequestProperty(entity.getContentType().getName(), entity.getContentType().getValue());

                OutputStream os = conn.getOutputStream();
                entity.writeTo(conn.getOutputStream());
                os.close();
                conn.connect();


                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    return readStream(conn.getInputStream());
                }


            } catch (java.net.SocketTimeoutException e) {
                //ERRORE di TIMEOUT

                dialog.cancel();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "URL non raggiungibile",
                                        Toast.LENGTH_LONG).show();
                            }
                        }, 500);
                    }
                });


                return null;
            } catch (Exception e) {
                e.printStackTrace();
                // something went wrong. connection with the server error
            }
            return null;
        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuilder builder = new StringBuilder();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return builder.toString();
        }


        //conversione per lo stream a base64 (valutare)
        public String convertBitmapToString(Bitmap bmp) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
            byte[] byte_arr = stream.toByteArray();
            String imageStr = Base64.encodeToString(byte_arr, Base64.DEFAULT);
            return imageStr;
        }

    }

    //endregion

    //region jpg to pdf
    public void convertToPdf(String outputPdfPath) {
        try {

            File outputFile = new File(outputPdfPath);
            if (!outputFile.exists()) outputFile.createNewFile();

            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outputPdfPath));


            Document doc = new Document(pdfDoc, new PageSize(PageSize.A4));


            //add dell'image alla conversione
            Image image = new Image(ImageDataFactory.create(path));
            image.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());//resize in A4

            doc.add(image);
//            Image gray = new Image(ImageDataFactory.create(1, 1, 1, 8,
//                    new byte[]{(byte) 0x80}, null));
//            gray.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
//
//            doc.add(gray);
            doc.close();

//            InputStream asset = this.getAssets().open(path_pdf);
//            FileOutputStream output = new FileOutputStream(outputFile);
//            final byte[] buffer = new byte[1024];
//            int size;
//            while ((size = asset.read(buffer)) != -1) {
//                output.write(buffer, 0, size);
//            }
//            asset.close();
//            output.close();

            Toast.makeText(MainActivity.this, "immagine salvata in: " + path_pdf,
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


    }

    //endregion


    //region save in local storage
    private String saveToInternalStorage(Bitmap bitmapImage) {

        bitmapImage = Bitmap.createScaledBitmap(bitmapImage, (int) PageSize.A4.getWidth(), (int) PageSize.A4.getHeight(), true);
        bitmapImage = toGrayscale(bitmapImage);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
        FileOutputStream fo;
        File f = new File(path);
        try {
            f.createNewFile();
            fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


// STAND-BY
//        ContextWrapper cw = new ContextWrapper(getApplicationContext());
//        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/registro/";
//
//        // path to /data/data/yourapp/app_data/nome
//        File directory = cw.getDir("testRegistro", Context.MODE_PRIVATE);
//        // Create imageDir
//        File mypath=new File(directory,"registro.jpg");
//
//
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(mypath);
//            // Use the compress method on the BitMap object to write image to the OutputStream
//            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
//            fos.flush();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                fos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        Toast.makeText(MainActivity.this, "salvato in: " + f.getAbsolutePath(),
                Toast.LENGTH_LONG).show();
        return f.getAbsolutePath();
    }

    //endregion

    //region convert greyscale
    public Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        //TODO testare
        int contrast = -50;
        int brightness = -200;

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });
        cm.setSaturation(0);

        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);

        paint.setColorFilter(f);

        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    //endregion

}
