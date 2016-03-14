package com.sirius.madness.ui.activities;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sirius.madness.R;
import com.sirius.madness.beans.ParticipantBean;
import com.sirius.madness.receiver.models.BluemixSessionParticipants;
import com.google.gson.Gson;
import com.ibm.mobile.services.data.IBMQuery;

import java.util.List;

import bolts.Continuation;
import bolts.Task;
import me.dm7.barcodescanner.core.DisplayUtils;
        /* Import ZBar Class files */

public class QRScannerActivity extends Activity
{
    private String CLASS_NAME = "QRScannerActivity";
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private TextView count;

    ImageScanner scanner;

    private boolean barcodeScanned = false;
    private boolean previewing = true;

    Boolean switcher = true;

    Boolean RegisterParticipant;
    String sessionId;
    String sessionName;

    static {
        System.loadLibrary("iconv");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        RegisterParticipant = getIntent().getBooleanExtra("RegisterParticipant", true);
        sessionId = getIntent().getStringExtra("sessionId");

        setContentView(R.layout.activity_qr_scanner);

        count = (TextView) findViewById(R.id.Count);

        String toast = getIntent().getStringExtra("toast");
        if(toast!=null) {
            Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
        }

        if(RegisterParticipant){
            sessionName = getIntent().getStringExtra("sessionName");
            count.setVisibility(View.VISIBLE);
            getCount(sessionId);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        autoFocusHandler = new Handler();
        if(RegisterParticipant){
            mCamera = getFrontCameraInstance();
        }else{
            mCamera = getBackCameraInstance();
        }

                /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 1);
        scanner.setConfig(0, Config.Y_DENSITY, 1);
        scanner.setConfig(0, Config.ENABLE, 0); //Disable all the Symbols
        scanner.setConfig(Symbol.QRCODE, Config.ENABLE, 1); //Only QRCODE is enable

        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
        preview.addView(mPreview);
    }

    private void getCount(String sessionId) {
        IBMQuery<BluemixSessionParticipants> query = IBMQuery.queryForClass("SessionParticipants");
        // Query all the BluemixParticipant objects from the server
        query.whereKeyEqualsTo("sessionId", sessionId);
        query.find().onSuccess(new Continuation<List<BluemixSessionParticipants>, Void>() {
            @Override
            public Void then(Task<List<BluemixSessionParticipants>> task) throws Exception {
                final List<BluemixSessionParticipants> objects = task.getResult();

                if (task.isCancelled()) {
                    Log.e(CLASS_NAME, "Exception in BluemixSessionParticipants : Task " + task.toString() + " was cancelled.");
                } else if (task.isFaulted()) {
                    Log.e(CLASS_NAME, "Exception in BluemixSessionParticipants : " + task.getError().getMessage());
                } else {
                    Log.d(CLASS_NAME, String.valueOf(objects.size()));

                    QRScannerActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            count.setText(String.valueOf(objects.size()));
                        }
                    });
                }
                return null;
            }
        });
    }

    public void onPause() {
        super.onPause();
        releaseCamera();
        FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
        preview.removeView(mPreview);
    }

    public void onResume(){
        super.onResume();

        try {
            if(mCamera==null){

                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                autoFocusHandler = new Handler();
                if(RegisterParticipant){
                    mCamera = getFrontCameraInstance();
                }else{
                    mCamera = getBackCameraInstance();
                }
                this.getWindowManager().getDefaultDisplay().getRotation();

                scanner = new ImageScanner();
                scanner.setConfig(0, Config.X_DENSITY, 3);
                scanner.setConfig(0, Config.Y_DENSITY, 3);

                mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
                FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
                preview.addView(mPreview);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block

        }
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getBackCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e){
        }
        return c;
    }
    public static Camera getFrontCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } catch (Exception e){
        }
        return c;
    }

    private void releaseCamera() {
        //Toast.makeText(QRScannerActivity.this, "Paused State", Toast.LENGTH_SHORT).show();
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.release();
            mCamera = null;
            mPreview= null;
        }

    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    PreviewCallback previewCb = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Size size = parameters.getPreviewSize();
            int width = size.width;
            int height = size.height;

            switcher = !switcher;

            if(DisplayUtils.getScreenOrientation(QRScannerActivity.this) == Configuration.ORIENTATION_PORTRAIT) {
                byte[] rotatedData = new byte[data.length];
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++)
                        rotatedData[x * height + height - y - 1] = data[x + y * width];
                }
                int tmp = width;
                width = height;
                height = tmp;
                data = rotatedData;
            }

            Image barcode = new Image(width, height, "Y800");

            if (switcher) {
                int[] pixels = applyGrayScale(data,width,height);
                Bitmap bm = Bitmap.createBitmap(pixels,width,height, Bitmap.Config.ARGB_8888);
                bm = createInvertedBitmap(bm, width, height);

                pixels = new int[width*height];
                bm.getPixels(pixels, 0, bm.getWidth(), 0, 0, bm.getWidth(), bm.getHeight());

                encodeYUV420SP(data, pixels, bm.getWidth(), bm.getHeight());
            }

            barcode.setData(data);

            int result = scanner.scanImage(barcode);
            String QRScannerResult;

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
                    QRScannerResult = sym.getData();
                    showResultAction(QRScannerResult);
                    barcodeScanned = true;
                }
            }
        }
    };

    private void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
        final int frameSize = width * height;

        int yIndex = 0;
        int uvIndex = frameSize;

        int a, R, G, B, Y, U, V;
        int index = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

                a = (argb[index] & 0xff000000) >> 24; // a is not used obviously
                R = (argb[index] & 0xff0000) >> 16;
                G = (argb[index] & 0xff00) >> 8;
                B = (argb[index] & 0xff) >> 0;

                // well known RGB to YUV algorithm
                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;

                // NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
                //    meaning for every 4 Y pixels there are 1 V and 1 U.  Note the sampling is every other
                //    pixel AND every other scanline.
                yuv420sp[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
                if (j % 2 == 0 && index % 2 == 0) {
                    yuv420sp[uvIndex++] = (byte) ((V < 0) ? 0 : ((V > 255) ? 255 : V));
                    yuv420sp[uvIndex++] = (byte) ((U < 0) ? 0 : ((U > 255) ? 255 : U));
                }

                index++;
            }
        }
    }


    private Bitmap createInvertedBitmap(Bitmap src, Integer width, Integer height) {

        Integer sizeWidth = width;
        Integer sizeHeight = height;

        ColorMatrix colorMatrix_Inverted =
                new ColorMatrix(new float[] {
                        -1,  0,  0,  0, 255,
                        0, -1,  0,  0, 255,
                        0,  0, -1,  0, 255,
                        0,  0,  0,  1,   0});

        ColorFilter ColorFilter_Sepia = new ColorMatrixColorFilter(
                colorMatrix_Inverted);

        Bitmap bitmap = Bitmap.createBitmap(sizeWidth, sizeHeight,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();

        paint.setColorFilter(ColorFilter_Sepia);
        canvas.drawBitmap(src, 0, 0, paint);

        return bitmap;
    }


    private int[] applyGrayScale(byte [] data, int width, int height) {
        int p;
        int size = width*height;
        int[] pixels = new int[size];
        for(int i = 0; i < size; i++) {
            p = data[i] & 0xFF;
            pixels[i] = 0xff000000 | p<<16 | p<<8 | p;
        }
        return pixels;
    }

    // Mimic continuous auto-focusing
    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    private void showResultAction(String QRScannerResult){
        Log.v("Scanner:", QRScannerResult); // Prints scan results

        try{
            Gson object = new Gson();
            //JsonObject obj = new JsonParser().parse().getAsJsonObject();
            final ParticipantBean bean = object.fromJson(QRScannerResult, ParticipantBean.class);
            if("registered".equals(bean.getType())) {
                //GET data passedback
                Intent returnIntent = new Intent();
                returnIntent.putExtra("ScanResult", QRScannerResult.toString());
                if(sessionId!=null){
                    returnIntent.putExtra("sessionId", sessionId);
                    if(RegisterParticipant){
                        returnIntent.putExtra("sessionName", sessionName);
                    }
                }
                setResult(RESULT_OK, returnIntent);
                finish();
            }else{
                Log.e(CLASS_NAME,bean.getType());
                Intent returnIntent = new Intent();
                if(sessionId!=null){
                    returnIntent.putExtra("sessionId", sessionId);
                    if(RegisterParticipant){
                        returnIntent.putExtra("sessionName", sessionName);
                    }
                }
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        } catch (Exception e) {
            Log.e("JsonParsingException : ", e.getMessage());
            Intent returnIntent = new Intent();
            if(sessionId!=null){
                returnIntent.putExtra("sessionId", sessionId);
                if(RegisterParticipant){
                    returnIntent.putExtra("sessionName", sessionName);
                }
            }
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }

    }
}