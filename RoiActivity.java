package com.example.jacob.myindependentstudy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class RoiActivity extends AppCompatActivity {

    //initalize variables and containers
    ImageView imageView;
    Bitmap BmB,BmA= null;
    Button Binarize, Equalize, Bilateral, Watermark, weight, Inverse, MagicWand;
    ImageButton DoneBtn;

    Canvas canvas;
    Paint paint;

    //button to take you back to main menu
    public void goFinish(View view){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BmA.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();


        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.putExtra("roi", byteArray);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roi);

        byte[] byteArray = getIntent().getByteArrayExtra("image");
        BmA = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imageView = (ImageView)findViewById(R.id.ImagePreview);

        imageView.setImageBitmap(BmA);

        //initialize buttons
        Binarize = (Button)findViewById(R.id.BnwBtn);
        weight = (Button)findViewById(R.id.WeightBtn);
        Inverse = (Button)findViewById(R.id.InverseBtn);
        Equalize = (Button)findViewById(R.id.EqualizeBtn);
        Bilateral = (Button)findViewById(R.id.BilateralBtn);
        Watermark = (Button)findViewById(R.id.watermark);
        MagicWand = (Button)findViewById(R.id.MagicBtn);




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


        Equalize.setOnClickListener(new View.OnClickListener(){
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

        Watermark.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String Copyright= "Jacob Schoen";
                BmB = BmA.copy(BmA.getConfig(), true);

                canvas = new Canvas(BmB);
                paint = new Paint();
                paint.setColor(Color.BLUE);
                paint.setTextSize(17);

                canvas.drawText(Copyright, 50, 90, paint);
                BmA = BmB.copy(BmB.getConfig(), true);
                imageView.setImageBitmap(BmA);
            }
        });



    }
}
