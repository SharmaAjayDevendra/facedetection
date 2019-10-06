public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener{
    TextureView textureView;
    Camera camera;
    TextView output;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textureView=findViewById(R.id.textureview);
        textureView.setSurfaceTextureListener(this);
        output=findViewById(R.id.output);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        camera=Camera.open();
//        camera=Camera.open(1);
        camera.setDisplayOrientation(90);
        Camera.Parameters parameters;
        parameters= camera.getParameters();
        parameters.setPreviewSize(352, 288);
        camera.setParameters(parameters);
        try {
            camera.setPreviewTexture(surface);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        camera.stopPreview();
        camera.release();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        Bitmap bitmap=textureView.getBitmap();
        if(bitmap!=null){
            FirebaseVisionImage firebaseVisionImage=FirebaseVisionImage.fromBitmap(bitmap);
            FirebaseVisionLabelDetector recognizer= FirebaseVision.getInstance().getVisionLabelDetector();
            recognizer.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionLabel>>() {
                @Override
                public void onSuccess(List<FirebaseVisionLabel> firebaseVisionLabels) {
                    output.setText(firebaseVisionLabels.get(0).getLabel());
                }
            });
        }
        camera.stopPreview();
        try {
            camera.setPreviewTexture(surface);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }
}
