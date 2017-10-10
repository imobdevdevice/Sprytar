package com.sprytar.android.presentation;

import android.app.Dialog;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sprytar.android.R;
import com.sprytar.android.SprytarApplication;
import com.sprytar.android.injection.component.ApplicationComponent;
import com.sprytar.android.injection.component.SessionComponent;
import com.sprytar.android.network.NetworkProblemDialog;
import com.sprytar.android.network.refreshListener;

public abstract class BaseActivity extends AppCompatActivity implements refreshListener {


    private NetworkProblemDialog networkProblemDialog;
    private Dialog progressDialog;
    protected ApplicationComponent getApplicationComponent() {
        return ((SprytarApplication) getApplication()).getComponent();
    }

    protected SessionComponent getSessionComponent() {
        return  SprytarApplication.get(this).getSessionComponent();
    }

    protected void showErrorDialog(String dialogTitle, String dialogMessage, String dialogDescription){

        if (networkProblemDialog == null) {
            networkProblemDialog = new NetworkProblemDialog(dialogTitle,dialogMessage,dialogDescription);
        } else {
            return;
        }
        networkProblemDialog.setRefreshListener(this);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(networkProblemDialog, null);
        ft.commitAllowingStateLoss();
    }

    //Task 6.4 Create a throbber

    protected void showThrobber(){

        try {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            if (progressDialog == null) {
                progressDialog = new Dialog(this);
            } else {
                return;
            }
            View view = LayoutInflater.from(this).inflate(R.layout.throbber_layout, null, false);

            Uri loadingGif = Uri.parse("res:///" + R.drawable.spryer_preloader256);
            SimpleDraweeView draweeView = (SimpleDraweeView) view.findViewById(R.id.loading_imageView);
            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
            GenericDraweeHierarchy hierarchy = builder.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE).build();
            DraweeController controller = Fresco.newDraweeControllerBuilder().setUri(loadingGif).setAutoPlayAnimations(true).build();
            draweeView.setImageURI(loadingGif);
            draweeView.setHierarchy(hierarchy);
            draweeView.setController(controller);

            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(view);
            progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            Window window = progressDialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(ContextCompat.getDrawable(this, android.R.color.transparent));
            }
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void hideThrobber(){
        if(progressDialog !=null){
            progressDialog.dismiss();
            progressDialog = null;
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void onRefreshClick() {
        if(networkProblemDialog !=null){
            networkProblemDialog.dismiss();
            networkProblemDialog = null;
        }
    }

    @Override
    public void onCloseClick() {
        if(networkProblemDialog !=null){
            networkProblemDialog.dismiss();
            networkProblemDialog = null;
        }
    }
}
