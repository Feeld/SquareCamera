package com.desmond.squarecamera;

import android.content.Intent;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class CameraActivity extends AppCompatActivity {

    public static final String TAG = CameraActivity.class.getSimpleName();
    public static final String CUSTOM_PREVIEW_CLASS = "CUSTOM_PREVIEW_CLASS";
    public static final String DISABLE_PREVIEW = "DISABLE_PREVIEW";
    public static final int RESULT_ERROR = -2;
    public static final String CAMERA_ERROR = "CAMERA_ERROR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.squarecamera__CameraFullScreenTheme);
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.squarecamera__activity_camera);

        if (savedInstanceState == null) {
            Fragment cameraFragment = CameraFragment.newInstance();

            Boolean disablePreview = getIntent().getBooleanExtra(DISABLE_PREVIEW, false);

            if(!disablePreview) {
                setCustomPreviewIfAvailable((CameraFragment) cameraFragment);
            } else {
                ((CameraFragment)cameraFragment).setPreviewDisabled(true);
            }


            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, cameraFragment , CameraFragment.TAG)
                    .commit();
        }
    }

    private void setCustomPreviewIfAvailable(CameraFragment fragment) {

        try {
            Class<? extends Fragment> customPreviewClass = (Class<? extends Fragment>)
                    getIntent().getExtras().getSerializable(CUSTOM_PREVIEW_CLASS);
            fragment.setCustomPreviewFragmentClass(customPreviewClass);
        }catch(Throwable e) {
        }
    }

    public void returnPhotoUri(Uri uri) {
        Intent data = new Intent();
        data.setData(uri);
        setResultAndFinish(RESULT_OK, data);
    }
    
    public void returnError(Throwable t) {
        Intent data = new Intent();
        data.putExtra(CAMERA_ERROR, t);
        setResultAndFinish(RESULT_ERROR, data);
    }

    private void setResultAndFinish(int resultCode, Intent intent){


        if (getParent() == null) {
            setResult(resultCode, intent);
        } else {
            getParent().setResult(resultCode, intent);
        }

        finish();
    }

    public void onCancel(View view) {
        getSupportFragmentManager().popBackStack();
    }
}
