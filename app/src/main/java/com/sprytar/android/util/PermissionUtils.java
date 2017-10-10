/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sprytar.android.util;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.sprytar.android.R;

public abstract class PermissionUtils {

    public static boolean checkPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (!checkPermission(context, permission)) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isCameraPermissionGranted(Context context) {
        return checkPermission(context, Manifest.permission.CAMERA);
    }

    public static void requestPermissions(final AppCompatActivity activity, String requestRationale,
                                          final int permissionId, final String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.permission_request_title)
                    .setMessage(requestRationale)
                    .setPositiveButton(R.string.permission_button_accept, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity,
                                    new String[]{permission},
                                    permissionId);
                        }
                    })
                    .setNegativeButton(R.string.permission_button_deny, null)
                    .show();

        } else {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, permissionId);
        }
    }

    public static void requestPermissions(final FragmentActivity activity, String requestRationale,
                                          final int permissionId, final String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.permission_request_title)
                    .setMessage(requestRationale)
                    .setPositiveButton(R.string.permission_button_accept, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity,
                                    new String[]{permission},
                                    permissionId);
                        }
                    })
                    .setNegativeButton(R.string.permission_button_deny, null)
                    .show();

        } else {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, permissionId);
        }
    }

    public static boolean isPermissionGranted(String[] grantPermissions, int[] grantResults,
                                              String permission) {
        for (int i = 0; i < grantPermissions.length; i++) {
            if (permission.equals(grantPermissions[i])) {
                return grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
        }
        return false;
    }


}
