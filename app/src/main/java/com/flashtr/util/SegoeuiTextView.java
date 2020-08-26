package com.flashtr.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by hirenkanani on 28/12/15.
 */
public class SegoeuiTextView extends TextView {

    public SegoeuiTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SegoeuiTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SegoeuiTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "SEGOEUI.TTF");
        setTypeface(tf);
    }
}
