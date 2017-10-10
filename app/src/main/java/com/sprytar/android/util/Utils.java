package com.sprytar.android.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.google.common.base.CharMatcher;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sprytar.android.BuildConfig;
import com.sprytar.android.R;

import org.joda.time.DateTime;
import org.joda.time.Years;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static final CharMatcher ALNUM =
            CharMatcher.inRange('a', 'z').or(CharMatcher.inRange('A', 'Z'))
                    .or(CharMatcher.inRange('0', '9')).precomputed();

    public static <T> List<T> getQuestionsFromAssets(Context context, String filename) {
        List<T> result = new ArrayList<>();

        Type type = new TypeToken<List<T>>() {}.getType();

        Gson gson = new Gson();
        result = gson.fromJson(loadJSONFromAsset(context, filename), type);

        return result;
    }

    public static boolean hasCompass(Context context){
        PackageManager manager = context.getPackageManager();
        return manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);
    }

    private static String loadJSONFromAsset(Context context, String filename) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean checkNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        boolean result = networkInfo != null && networkInfo.isConnected();
        if (!result && context != null) {
            Toast.makeText(context, R.string.no_network, Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    public static String doubleToString(double value) {
        return  Double.toString(value);
    }

    public static int getAge(long birthday) {
        DateTime currentDate = new DateTime();
        DateTime birthdayDate = new DateTime(birthday * 1000);
        return Years.yearsBetween(birthdayDate, currentDate).getYears();
    }

    public static Uri getSvgUri(Context context, String imageFileName, String svgRawText) throws
            IOException {

        imageFileName = ALNUM.retainFrom(imageFileName);

        String svgIcon = svgRawText.replaceAll("\\\\", "");

        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Uri uri = null;
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File file = new File(storageDir, imageFileName + ".svg");
        if (!file.exists()) {
            Files.write(svgIcon, file, Charset.forName("UTF-8"));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + "" +
                    ".provider", file);
        } else {
            uri = Uri.fromFile(file);
        }

        return uri;
    }

    public static int screenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }




}
