package com.focuspoint.translator.utils;

import android.content.Context;
import android.graphics.Rect;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by v_banko on 23.05.2016.
 * Предназначен для корректной работы виртуальной клавиатуры в
 * полноэкранном режиме
 * Размещать в корне разметки XML
 */
public class KeyboardLayout extends ConstraintLayout {


    Rect r = new Rect();
    int maxSize;
    OnResizeListener listener;
    OnKeyboardOpen keyboardOpen;
    OnKeyboardClose keyboardClose;
    Runnable touchListener;
    private boolean keyboardOpened = false;

    public interface OnResizeListener {
        void onResize();
    }

    public interface OnKeyboardOpen {
        void onKeyboardOpen();
    }

    public interface OnKeyboardClose {
        void onKeyboardClose();
    }


    public KeyboardLayout(Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        getWindowVisibleDisplayFrame(r);
        if (r.height() > maxSize) {
            maxSize = r.height();
        }


        //if keyboard more than 1/5 of screen height;
        if (r.height() < maxSize * 0.8f) {
            if (listener != null) {
                listener.onResize();
            }
            if (!keyboardOpened) {
                if (keyboardOpen != null){
                    keyboardOpen.onKeyboardOpen();
                }
                keyboardOpened = true;
            }

        } else {
            if (keyboardOpened) {
                if (keyboardClose != null ){
                    keyboardClose.onKeyboardClose();
                }

                keyboardOpened = false;
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setTouchListener(Runnable listener) {
        this.touchListener = listener;
    }

    public void setOnResizeListener(OnResizeListener listener) {
        this.listener = listener;
    }

    public void setKeyboardOpen(OnKeyboardOpen listener) {
        this.keyboardOpen = listener;
    }

    public void setKeyboardClose(OnKeyboardClose listener) {
        this.keyboardClose = listener;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (touchListener != null) {
            touchListener.run();
        }
        return super.onInterceptTouchEvent(ev);
    }
}
