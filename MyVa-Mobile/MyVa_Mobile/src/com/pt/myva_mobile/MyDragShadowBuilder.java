package com.pt.myva_mobile;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.DragShadowBuilder;

public class MyDragShadowBuilder extends DragShadowBuilder {
    private Drawable mShadow;

    public MyDragShadowBuilder(View v) {
        super(v);
        mShadow = v.getResources().getDrawable(R.drawable.action_icon_logout);
        mShadow.setCallback(v);
        mShadow.setBounds(0, 0, v.getWidth(), v.getHeight());
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        super.onDrawShadow(canvas);
        mShadow.draw(canvas);
        getView().draw(canvas);
    }
}