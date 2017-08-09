package com.novp.sprytar.main;

import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;

import com.novp.sprytar.R;
import com.novp.sprytar.databinding.ActivityTcBinding;
import com.novp.sprytar.presentation.BaseActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TcActivity extends BaseActivity {

    ActivityTcBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_tc);

        String tc = "";
        try {
            tc = loadTc("tc");
        } catch (IOException e) {}

        binding.tcTextView.loadData(tc,"text/html; charset=utf-8", "utf-8");

        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.terms_and_conditions));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public String loadTc(String fileName) throws IOException
    {
        //Create a InputStream to read the file into
        InputStream iS;

        Resources resources = getResources();

        //get the resource id from the file name
        int rID = resources.getIdentifier(fileName, "raw", getPackageName());
        //get the file as a stream
        iS = resources.openRawResource(rID);

        //create a buffer that has the same size as the InputStream
        byte[] buffer = new byte[iS.available()];
        //read the text file as a stream, into the buffer
        iS.read(buffer);
        //create a output stream to write the buffer into
        ByteArrayOutputStream oS = new ByteArrayOutputStream();
        //write this buffer to the output stream
        oS.write(buffer);
        //Close the Input and Output streams
        oS.close();
        iS.close();

        //return the output stream as a String
        return oS.toString();
    }
}
