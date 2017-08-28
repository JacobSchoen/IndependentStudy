package com.example.jacob.myindependentstudy;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.solver.widgets.Rectangle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.R.attr.bitmap;
import static java.security.AccessController.getContext;
import static org.opencv.core.Core.flip;
import static org.opencv.core.Core.transpose;

public class MainMenuActivity extends AppCompatActivity {

    //initalize variables and containers
    ImageView imageView, Rectview;
    Button Imgbutton, BlurButton, ErodeButton, DialateButton, FilterPageButton, RoiButton;
    ImageButton OGButton, SaveButton, UndoButton, RotateButton;
    SeekBar SharpBar, SoftBar, BlurBar;
    TextView text_view, textSource;
    private static final int PICK_IMAGE = 100;
    File file;
    FileOutputStream fileOutputStream;

    Uri imageUri;
    Bitmap BmOG, BmB, BmA, RoiBm, temp = null;
    int progress_valueBlur , progress_valueSoft, progress_valueSharp;

    float downx = 0,downy = 0,upx = 0,upy = 0;
    int topx, topy, botx, boty = 0;
    Canvas canvas, CanR;
    Paint paint,panR;


    public void GoToFilters(View view){
        if(BmA != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            BmA.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Intent in1 = new Intent(this, FiltersActivity.class);
            in1.putExtra("image", byteArray);
            startActivity(in1);
        }
    }

    public void GoToRoi(View view){
        if(RoiBm != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            RoiBm.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Intent in1 = new Intent(this, RoiActivity.class);
            in1.putExtra("image", byteArray);
            startActivity(in1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //ImageViews initialized
        imageView = (ImageView)findViewById(R.id.ImagePreview);
        Rectview = (ImageView)findViewById(R.id.RectView);

        //buttons initialized
        Imgbutton = (Button)findViewById(R.id.SelectImageBtn);
        DialateButton = (Button)findViewById(R.id.DialateBtn);
        BlurButton = (Button)findViewById(R.id.BlurBtn);
        ErodeButton = (Button)findViewById(R.id.ErodeBtn);
        OGButton = (ImageButton)findViewById(R.id.OgBtn);
        RoiButton = (Button)findViewById(R.id.RoiBtn);
        SaveButton = (ImageButton) findViewById(R.id.SaveBtn);
        UndoButton = (ImageButton) findViewById(R.id.RevertBtn);
        FilterPageButton = (Button)findViewById(R.id.FiltersBtn);
        RotateButton = (ImageButton)findViewById(R.id.RotateBtn);


        BlurBar = (SeekBar)findViewById(R.id.BlurBar);
        SharpBar = (SeekBar)findViewById(R.id.SharpBar);
        SoftBar = (SeekBar)findViewById(R.id.SoftenBar);

        text_view =(TextView)findViewById(R.id.textView);
        textSource = (TextView)findViewById(R.id.textSourceview);

        text_view.setText("Intensity : " + BlurBar.getProgress() + " / " + BlurBar.getMax());

        //initalization for seekbars
        BlurBar();
        SoftBar();
        SharpBar();



        Intent refresh = getIntent();
        if(refresh.hasExtra("image")) {
            byte[] byteArray = getIntent().getByteArrayExtra("image");
            BmA = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imageView.setImageBitmap(BmA);
        }
        if(refresh.hasExtra("roi")){
            byte[] byteArray = getIntent().getByteArrayExtra("roi");
            RoiBm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

            /*
            CanR = new Canvas(BmA);
            panR = new Paint();
            panR.setAntiAlias(true);
            panR.setFilterBitmap(true);
            panR.setDither(true);

            CanR.drawBitmap(RoiBm,50,50,panR);
            Rectview.setImageResource(0);
            */
            imageView.setImageBitmap(RoiBm);
        }



        //On click function calls
        Imgbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        BlurButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(progress_valueBlur != 0 && RoiBm != null){
                    BmB = BmA.copy(BmA.getConfig(), true);
                    Mat sourceImage = new Mat(RoiBm.getHeight(), RoiBm.getWidth(), CvType.CV_8UC4);
                    Utils.bitmapToMat(RoiBm, sourceImage);
                    Imgproc.GaussianBlur(sourceImage, sourceImage, new Size(progress_valueBlur, progress_valueBlur), 0);
                    Utils.matToBitmap(sourceImage, RoiBm);
                    imageView.setImageBitmap(RoiBm);

                }
                else if(progress_valueBlur != 0 && BmA != null) {
                    BmB = BmA.copy(BmA.getConfig(), true);
                    Mat sourceImage = new Mat(BmB.getHeight(), BmB.getWidth(), CvType.CV_8UC4);
                    Utils.bitmapToMat(BmB, sourceImage);
                    Imgproc.GaussianBlur(sourceImage, sourceImage, new Size(progress_valueBlur, progress_valueBlur), 0);
                    Utils.matToBitmap(sourceImage, BmA);
                    imageView.setImageBitmap(BmA);
                }

            }
        });

        ErodeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(progress_valueSharp != 0 && BmA != null) {
                    BmB = BmA.copy(BmA.getConfig(), true);
                    Mat sourceImage = new Mat(BmB.getHeight(), BmB.getWidth(), CvType.CV_8UC4);
                    Utils.bitmapToMat(BmB, sourceImage);
                    Mat kernel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_CROSS, new Size(progress_valueSharp, progress_valueSharp));
                    Imgproc.erode(sourceImage, sourceImage, kernel);
                    Utils.matToBitmap(sourceImage, BmA);
                    imageView.setImageBitmap(BmA);
                }
            }
        });

        DialateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(progress_valueSoft != 0 && BmA != null) {
                    BmB = BmA.copy(BmA.getConfig(), true);
                    Mat sourceImage = new Mat(BmB.getHeight(), BmB.getWidth(), CvType.CV_8UC4);
                    Utils.bitmapToMat(BmB, sourceImage);
                    Mat kernel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_CROSS, new Size(progress_valueSoft, progress_valueSoft));
                    Imgproc.dilate(sourceImage, sourceImage, kernel);
                    Utils.matToBitmap(sourceImage, BmA);
                    imageView.setImageBitmap(BmA);
                }
            }
        });

        OGButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(BmOG != null) {

                    BmA = BmOG.copy(BmOG.getConfig(), true);
                    imageView.setImageBitmap(BmA);
                    Rectview.setImageResource(0);
                }
            }
        });

        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File dir = new File("/sdcard/tempfolder/");
                if (!dir.exists()){
                    dir.mkdirs();
                }

                File output = new File(dir, "tempfile.jpg");
                OutputStream os = null;

                try{
                    os = new FileOutputStream(output);
                    BmA.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.flush();
                    os.close();

                    MediaScannerConnection.scanFile(MainMenuActivity.this, new String[] { output.toString() }
                            , null, new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                        Log.d("appname", "image is saved in gallery and gallery is refreshed");
                    }
                    });
                }catch (Exception e) {
                }

                Toast.makeText(MainMenuActivity.this, "Image was saved successfully", Toast.LENGTH_SHORT).show();

            }

        });

        UndoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(BmB != null) {

                    BmA = BmB.copy(BmB.getConfig(), true);
                    imageView.setImageBitmap(BmA);
                }
            }
        });

        RotateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(BmA != null) {
                    BmB = BmA.copy(BmA.getConfig(), true);
                    Mat sourceImage = new Mat(BmB.getHeight(), BmB.getWidth(), CvType.CV_8UC4);
                    Utils.bitmapToMat(BmB, sourceImage);
                    Point center = new Point();
                    center.x = BmB.getWidth() / 2;
                    center.y = BmB.getHeight() / 2;
                    Mat r = Imgproc.getRotationMatrix2D(center, 90, 1);
                    Imgproc.warpAffine(sourceImage, sourceImage, r, sourceImage.size());
                    Utils.matToBitmap(sourceImage, BmA);
                    imageView.setImageBitmap(BmA);
                }
            }
        });


        imageView.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch (View v, MotionEvent event){

                int action = event.getAction();
                int x = (int) event.getX();
                int y = (int) event.getY();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        textSource.setText("ACTION_DOWN- " + x + " : " + y);
                        downx = event.getX();
                        downy = event.getY();
                        topx = (int) downx;
                        topy = (int) downy;
                        Log.d("Touch", "Action was Down");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        textSource.setText("ACTION_MOVE- " + x + " : " + y);
                        upx = event.getX();
                        upy = event.getY();
                        Log.d("Touch", "Action was Move");

                        downx = upx;
                        downy = upy ;
                        break;
                    case MotionEvent.ACTION_UP:
                        textSource.setText("ACTION_UP- " + x + " : " + y);
                        upx = event.getX();
                        upy = event.getY();

                        botx = (int)upx;
                        boty = (int)upy;

                        int width = botx - topx;
                        int height = boty - topy;

                        temp = BmA.copy(BmA.getConfig(), true);
                        RoiBm = Bitmap.createBitmap(BmA,topx, topy, width, height);

                        canvas = new Canvas(temp);
                        paint = new Paint();
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setColor(Color.BLUE);
                        paint.setStrokeWidth(3);

                        //RoiBm = Bitmap.createBitmap(BmA, topx, topy, botx, boty);
                        canvas.drawRect(topx, topy, botx, boty, paint);
                        Rectview.setImageBitmap(temp);
                        Log.d("Touch", " Action was UP");

                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                    default:
                        break;
                }
                return true;
            }

        });


    }

    private String saveImage(Context context, Bitmap b, String name, String extension){
        name=name+"."+extension;
        FileOutputStream out;
        try{
            out = context.openFileOutput(name, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return name;
    }

    public boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        return false;
    }

    public File getAlbumStorageDir(String albumName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
        if(!file.mkdirs()){
            Log.e("Error","Directory not Created");
        }
        return  file;
    }





    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = getIntent();
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            //imageView.setImageURI(imageUri);

            try {
                // convert uri to stream
                InputStream picturein = getContentResolver().openInputStream(imageUri);
                //open image as a bitmap
                BmOG = BitmapFactory.decodeStream(picturein);
                BmOG = JPGtoRGB888(BmOG);
                //NOTE make copies not directly reference a bitmap
                BmB = BmOG.copy(BmOG.getConfig(), true);
                BmA = BmOG.copy(BmOG.getConfig(), true);



                //show bitmap in image view
                imageView.setImageBitmap(BmOG);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    private Bitmap JPGtoRGB888(Bitmap img){
        Bitmap result = null;

        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //        get jpeg pixels, each int is the color value of one pixel
        img.getPixels(pixels,0,img.getWidth(),0,0,img.getWidth(),img.getHeight());

        //        create bitmap in appropriate format
        result = Bitmap.createBitmap(img.getWidth(),img.getHeight(), Bitmap.Config.ARGB_8888);

        //        Set RGB pixels
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());

        return result;
    }

    private void createDirectoryAndSaveFile(Bitmap imageTOsave, String filename){

        File direct = new File(Environment.getExternalStorageDirectory() + "/DirName");

        if(!direct.exists()){
            File WallpaperDirectory = new File("/sdcard/DirName/");
            WallpaperDirectory.mkdirs();
        }

        File file = new File(new File(Environment.DIRECTORY_PICTURES), filename);
        if (file.exists()){
            file.delete();
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            imageTOsave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void BlurBar( ){
        BlurBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progress_valueBlur = progress;
                        text_view.setText("Intensity : " + progress + " / " + BlurBar.getMax());
                        //Toast.makeText(MainMenuActivity.this,"SeekBar in progress",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //Toast.makeText(MainMenuActivity.this,"SeekBar in StartTracking",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        text_view.setText("Intensity : " + progress_valueBlur + " / " + BlurBar.getMax());
                        //Toast.makeText(MainMenuActivity.this,"SeekBar in StopTracking",Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }

    public void SharpBar( ){
        SharpBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progress_valueSharp = progress;
                        text_view.setText("Intensity : " + progress + " / " + SharpBar.getMax());
                        //Toast.makeText(MainMenuActivity.this,"SeekBar in progress",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //Toast.makeText(MainMenuActivity.this,"SeekBar in StartTracking",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        text_view.setText("Intensity : " + progress_valueSharp + " / " + SharpBar.getMax());
                        //Toast.makeText(MainMenuActivity.this,"SeekBar in StopTracking",Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }

    public void SoftBar( ){
        SoftBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progress_valueSoft = progress;
                        text_view.setText("Intensity : " + progress + " / " + SoftBar.getMax());
                        //Toast.makeText(MainMenuActivity.this,"SeekBar in progress",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //Toast.makeText(MainMenuActivity.this,"SeekBar in StartTracking",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        text_view.setText("Intensity : " + progress_valueSoft + " / " + SoftBar.getMax());
                        //Toast.makeText(MainMenuActivity.this,"SeekBar in StopTracking",Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }
}
