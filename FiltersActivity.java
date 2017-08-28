package com.example.jacob.myindependentstudy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;

import static java.security.AccessController.getContext;


public class FiltersActivity extends AppCompatActivity {



    //initalize variables and containers
    ImageView imageView;
    Bitmap BmB,BmA= null;
    Button Binarize, Colorize, Bilateral, Watermark, weight, Inverse, MagicWand;
    ImageButton UndoButton;

    Canvas canvas;
    Paint paint;

    int topx, topy = 0;




    //button to take you back to main menu
    public void GoToMainMenu (View view){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BmA.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();


        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.putExtra("image", byteArray);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        byte[] byteArray = getIntent().getByteArrayExtra("image");
        BmA = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imageView = (ImageView)findViewById(R.id.ImagePreview);

        imageView.setImageBitmap(BmA);

        //initialize buttons
        Binarize = (Button)findViewById(R.id.BwBtn);
        weight = (Button)findViewById(R.id.weightBtn);
        Inverse = (Button)findViewById(R.id.inverseBtn);
        Colorize = (Button)findViewById(R.id.ColorizeBtn);
        Bilateral = (Button)findViewById(R.id.BilateralBtn);
        Watermark = (Button)findViewById(R.id.ColorspaceBtn);
        MagicWand = (Button)findViewById(R.id.MagicWandBtn);

        UndoButton = (ImageButton)findViewById(R.id.UndoBtn);

        Binarize.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                BmB = BmA.copy(BmA.getConfig(), true);
                Mat sourceImage = new Mat(BmB.getHeight(), BmB.getWidth(), CvType.CV_8UC4);
                Utils.bitmapToMat(BmB, sourceImage);
                Imgproc.cvtColor(sourceImage, sourceImage, Imgproc.COLOR_BGRA2GRAY );
                Utils.matToBitmap(sourceImage, BmA);
                imageView.setImageBitmap(BmA);
            }
        });

        Bilateral.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                BmB = BmA.copy(BmA.getConfig(), true);
                Mat sourceImage = new Mat(BmB.getHeight(), BmB.getWidth(), CvType.CV_8UC4);
                Utils.bitmapToMat(BmB, sourceImage);
                Imgproc.bilateralFilter(sourceImage, sourceImage,9,9,7);
                Utils.matToBitmap(sourceImage, BmA);
                imageView.setImageBitmap(BmA);

            }
        });

        Colorize.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                BmB = BmA.copy(BmA.getConfig(), true);
                Mat sourceImage = new Mat(BmB.getHeight(), BmB.getWidth(), CvType.CV_8UC4);
                Utils.bitmapToMat(BmB, sourceImage);
                Imgproc.cvtColor(sourceImage, sourceImage, Imgproc.COLOR_BGRA2GRAY);
                Imgproc.equalizeHist(sourceImage,sourceImage);
                Utils.matToBitmap(sourceImage, BmA);
                imageView.setImageBitmap(BmA);

            }
        });

        UndoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(BmA != null) {
                    BmA = BmB.copy(BmB.getConfig(), true);
                    imageView.setImageBitmap(BmA);
                }
            }
        });

        Watermark.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String Copyright= "Jacob Schoen";
                BmB = BmA.copy(BmA.getConfig(), true);

                canvas = new Canvas(BmB);
                paint = new Paint();
                paint.setColor(Color.BLUE);
                paint.setTextSize(90);

                canvas.drawText(Copyright, 50, 90, paint);
                BmA = BmB.copy(BmB.getConfig(), true);
                imageView.setImageBitmap(BmA);
            }
        });

        weight.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int zoomingFactor = 2;
                BmB = BmA.copy(BmA.getConfig(), true);
                Mat sourceImage = new Mat(BmB.getHeight(), BmB.getWidth(), CvType.CV_8UC4);
                Mat destination = new Mat(sourceImage.rows() * zoomingFactor, sourceImage.cols()*  zoomingFactor, sourceImage.type());

                Utils.bitmapToMat(BmB, sourceImage);
                Imgproc.GaussianBlur(sourceImage, destination, new Size(0,0), 10);
                Core.addWeighted(sourceImage, 1.5, destination, -0.5, 0, destination);
                Utils.matToBitmap(destination, BmA);
                imageView.setImageBitmap(BmA);
            }
        });

        Inverse.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                BmB = BmA.copy(BmA.getConfig(), true);
                Mat sourceImage = new Mat(BmB.getHeight(), BmB.getWidth(), CvType.CV_8UC4);

                Utils.bitmapToMat(BmB, sourceImage);
                //THRESH_BINARY, THRESH_BINARY_INV, THRESH_TOZERO,THRESH_TRUNC
                Imgproc.threshold(sourceImage,sourceImage,127,255,Imgproc.THRESH_BINARY);

                Utils.matToBitmap(sourceImage, BmA);
                imageView.setImageBitmap(BmA);
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
                        //textSource.setText("ACTION_DOWN- " + x + " : " + y);
                        double downx = event.getX();
                        double downy = event.getY();
                        topx = (int) downx;
                        topy = (int) downy;
                        Log.d("Touch", "Action was Down");
                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:


                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                    default:
                        break;
                }
                return true;
            }

        });
        MagicWand.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){

                BmB = BmA.copy(BmA.getConfig(), true);
                Mat sourceImage = new Mat(BmB.getHeight(), BmB.getWidth(), CvType.CV_8SC1);
                Mat mask = new Mat(BmB.getHeight() +2, BmB.getWidth() +2, CvType.CV_8SC1);
                Utils.bitmapToMat(BmB,sourceImage);

                Imgproc.floodFill(sourceImage,mask, new Point(topx,topy),new Scalar(255));
                Utils.matToBitmap(sourceImage, BmA);
                imageView.setImageBitmap(BmA);

                /*
                Mat mask = new Mat();
                Imgproc.Canny(sourceImage, mask, 100, 200);
                Core.copyMakeBorder(mask, mask, 1, 1, 1, 1, Core.BORDER_REPLICATE);

                Point seed = new Point(4,4);

                //fill mask with value 128
                //u fillValue = 128;
                //Imgproc.floodFill(sourceImage, mask, seed, )
                */
            }
        });


    }

}
