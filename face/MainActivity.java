package in.autodice.facedetection;
import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;

import java.io.IOException;
import java.net.URI;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    ImageView imageView;
    TextView output;
    Camera camera;
    Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=findViewById(R.id.image);
        output=findViewById(R.id.output);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1001);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
        }
        ContentValues values=new ContentValues(2);
        values.put(MediaStore.Images.Media.TITLE, "New Image");
        values.put(MediaStore.Images.Media.DESCRIPTION, "For Face Recognition");
        ContentResolver resolver=getContentResolver();
        uri=resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==Activity.RESULT_OK){
            imageView.setImageURI(uri);
            BitmapDrawable drawable= (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap=drawable.getBitmap();
            FirebaseVisionImage image=FirebaseVisionImage.fromBitmap(bitmap);
            FirebaseVisionFaceDetectorOptions.Builder builder = new FirebaseVisionFaceDetectorOptions.Builder();
            builder.setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS);
            FirebaseVisionFaceDetectorOptions options=builder.build();
            FirebaseVisionFaceDetector detector=FirebaseVision.getInstance().getVisionFaceDetector(options);
            detector.detectInImage(image).addOnSuccessListener(
                    new OnSuccessListener<List<FirebaseVisionFace>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                            FirebaseVisionFace face;
                            for(int i=0; i<firebaseVisionFaces.size(); i++){
                                face=firebaseVisionFaces.get(i);
                                if(face.getSmilingProbability()<0.5){
                                    output.setText("No Smiling");
                                }else{
                                    output.setText("Smiling");
                                }
                            }
                        }
                    }
            );
        }
    }
}
