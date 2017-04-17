package com.focuspoint.translator.utils;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

/**
 *
 */

public class KeyboardLayout extends ConstraintLayout{

    private int lastHeight;
    private int maxHeight;

    private Runnable onOpenKeyboardListener;
    private Runnable onCloseKeyboardListener;

    public KeyboardLayout(Context context) {
        super(context);
    }

    public KeyboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        System.out.println(lastHeight + " / " + getHeight() + " / " + maxHeight);


        if (lastHeight != 0 && lastHeight != getHeight()){


            if (getHeight() == maxHeight){
                if (onOpenKeyboardListener != null){
                    onOpenKeyboardListener.run();
                }
            }

            if (maxHeight - getHeight() > 100){
                if (onCloseKeyboardListener != null){
                    onCloseKeyboardListener.run();
                }
            }
        }
        maxHeight = Math.max(getHeight(), maxHeight);
        lastHeight = getHeight();
    }



    public void setOnCloseKeyboardListener(Runnable onCloseKeyboardListener) {
        this.onCloseKeyboardListener = onCloseKeyboardListener;
    }

    public void setOnOpenKeyboardListener(Runnable onOpenKeyboardListener) {
        this.onOpenKeyboardListener = onOpenKeyboardListener;
    }
}
