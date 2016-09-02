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

            try {
                Class<? extends Fragment> customPreviewClass = (Class<? extends Fragment>)
                        getIntent().getExtras().getSerializable(CUSTOM_PREVIEW_CLASS);
                ((CameraFragment)cameraFragment).setCustomPreviewFragmentClass(customPreviewClass);
            }catch(Throwable e) {
            }


            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, cameraFragment , CameraFragment.TAG)
                    .commit();
        }
    }

    public void returnPhotoUri(Uri uri) {
        Intent data = new Intent();
        data.setData(uri);

        if (getParent() == null) {
            setResult(RESULT_OK, data);
        } else {
            getParent().setResult(RESULT_OK, data);
        }

        finish();
    }

    public void onCancel(View view) {
        getSupportFragmentManager().popBackStack();
    }
}
