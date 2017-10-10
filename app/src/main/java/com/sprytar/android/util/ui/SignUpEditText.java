package com.sprytar.android.util.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

public class SignUpEditText extends AppCompatEditText {

    public SignUpEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setErrorWithoutText(CharSequence error, Drawable icon) {
        setCompoundDrawables(null, null, icon, null);
    }
}
