package com.astinx.cameramanager;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by agustin on 01/12/14.
 */
public class NewPublicationActivity extends Activity {

    public static final int REQUEST_CODE_CAMERA = 0;
    public static final int REQUEST_CODE_GALLERY = 1;
    public static final String PUBLICATION = "publication";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        Bundle b = getIntent().getExtras();
        if (b!= null && b.containsKey(PUBLICATION)) {
            if (savedInstanceState == null) {
                Fragment f = new NewPublicationFragment();
                f.setArguments(getIntent().getExtras());
                getFragmentManager().beginTransaction().replace(R.id.container, f).commit();
            }
        }else{
            if (savedInstanceState == null) {
                Fragment f = new NewPublicationFragment();
                getFragmentManager().beginTransaction().replace(R.id.container, f).commit();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getFragmentManager().findFragmentById(R.id.container).onActivityResult(requestCode,resultCode,data);
    }


    @Override
    public void onBackPressed() {
        try {
            NewPublicationFragment f = (NewPublicationFragment) getFragmentManager().findFragmentById(R.id.container);
            if (!f.hasAnythingChange()) {
                super.onBackPressed();
            } else {
                f.showDialogConfirmation(new DialogInterface.OnClickListener() {
                                             @Override
                                             public void onClick(DialogInterface dialog, int which) {
                                                 NewPublicationActivity.super.onBackPressed();
                                             }
                                         }, new DialogInterface.OnClickListener() {
                                             @Override
                                             public void onClick(DialogInterface dialog, int which) {
                                                 dialog.dismiss();
                                             }
                                         }
                );
            }
        } catch (NullPointerException e) {
            throw new RuntimeException("NewPublicationActivity is supposed to contain NewPublicationFragment");
        }



    }
}
