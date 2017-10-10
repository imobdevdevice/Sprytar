package com.sprytar.android.util;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sprytar.android.R;

import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import me.toptas.fancyshowcase.OnViewInflateListener;

public class Showcase {
    private static final String STEP_1 = "This is the question bar. Here you'll find the question number and hint button.";
    private static final String STEP_2 = "This is the quiz question.";
    private static final String STEP_3 = "These view tabs allow you to navigate in two different ways. Click the tab to toggle between 2D map or 3D augmented reality.";
    private static final String STEP_4 = "In the map view your location is shown by the blue marker. The place where you should find the answer is shown by the Spryte.";
    private static final String STEP_5 = "This is the help button. You can press this to return to these screens.";

    private static int step = 1;

    public static int[] measureViewPosition(View view) {
        Rect myViewRect = new Rect();
        view.getGlobalVisibleRect(myViewRect);
        int[] measurement = new int[4];

        measurement[0] = myViewRect.left;
        measurement[1] = myViewRect.top;
        //   measurement[2] = (int) (myViewRect.width() * 2.5);
        //   measurement[3] = myViewRect.height() * 2;
        measurement[2] = myViewRect.width();
        measurement[3] = myViewRect.height();

        return measurement;
    }

    public static int getStep() {
        return step;
    }

    public static void increaseStep() {
        step++;
    }

    public static void decreaseStepBy(int decreaseFor) {
        if(step > 1){
            step = step -decreaseFor;
        }
    }

    public static void resetStep() {
        step = 1;
    }

    public static String getStepText() {
        switch (step) {
            case 1:
                return STEP_1;
            case 2:
                return STEP_2;
            case 3:
                return STEP_3;
            case 4:
                return STEP_4;
            case 5:
                return STEP_5;
            default:
                return "";
        }
    }

    public static FancyShowCaseView prepareShowCaseFor(View view, boolean reMeasureHeight, View.OnClickListener onClickListener, FocusShape shape, Activity activity, int layout) {
        if (reMeasureHeight) {
            return getReMeasuredShowcase(view, onClickListener, shape, activity, layout);
        }

        return getShowCase(view, onClickListener, shape, activity, layout);
    }

    private static FancyShowCaseView getReMeasuredShowcase(View view, final View.OnClickListener onClickListener, FocusShape shape, Activity activity, int layout) {
        int[] measure = measureViewPosition(view);
        measure[3] = 180;
        measure[2] = (int) (measure[2] * 2.5);

        FancyShowCaseView showCaseView = new FancyShowCaseView.Builder(activity)
                .focusRectAtPosition(measure[0], measure[1], measure[2], measure[3])
                .focusShape(shape)
                .roundRectRadius(90)
                .customView(layout, new OnViewInflateListener() {
                    @Override
                    public void onViewInflated(View view) {
                        ((TextView) view.findViewById(R.id.showcaseText)).setText(Showcase.getStepText());

                        if(getStep() == 1){
                            view.findViewById(R.id.backArrowShowcase).setVisibility(View.INVISIBLE);
                        }else {
                            view.findViewById(R.id.backArrowShowcase).setVisibility(View.VISIBLE);
                        }
                        //   view.findViewById(R.id.nextShowcase)
                        //           .setOnClickListener(onClickListener);
                        view.findViewById(R.id.backArrowShowcase).setOnClickListener(onClickListener);
                        view.findViewById(R.id.forwardArrowShowcase).setOnClickListener(onClickListener);

                        view.findViewById(R.id.skipExplainer).setOnClickListener(onClickListener);
                    }
                })
                .closeOnTouch(false)
                .build();

        return showCaseView;
    }

    private static FancyShowCaseView getShowCase(View view, final View.OnClickListener onClickListener, FocusShape shape, Activity activity, int layout) {
        FancyShowCaseView showCaseView = new FancyShowCaseView.Builder(activity)
                .focusOn(view)
                .focusShape(shape)
                .roundRectRadius(90)
                .customView(layout, new OnViewInflateListener() {
                    @Override
                    public void onViewInflated(View view) {

                        if (getStep() == 5) {
                            Button done = (Button)view.findViewById(R.id.nextShowcase);
                            done.setVisibility(View.VISIBLE);
                            done.setOnClickListener(onClickListener);
                            view.findViewById(R.id.forwardArrowShowcase).setVisibility(View.INVISIBLE);
                        }

                        if(getStep() == 1){
                            view.findViewById(R.id.backArrowShowcase).setVisibility(View.INVISIBLE);
                        }else {
                            view.findViewById(R.id.backArrowShowcase).setVisibility(View.VISIBLE);
                        }

                        ((TextView) view.findViewById(R.id.showcaseText)).setText(Showcase.getStepText());
                        //    view.findViewById(R.id.nextShowcase)
                        //           .setOnClickListener(onClickListener);
                        view.findViewById(R.id.backArrowShowcase).setOnClickListener(onClickListener);
                        view.findViewById(R.id.forwardArrowShowcase).setOnClickListener(onClickListener);

                        view.findViewById(R.id.skipExplainer).setOnClickListener(onClickListener);
                    }
                })
                .closeOnTouch(false)
                .build();

        return showCaseView;
    }

    public static boolean isReadyForRemove(FancyShowCaseView showcase) {
        if (showcase != null) {
            if (showcase.isAttachedToWindow()) {
                return true;
            }
        }

        return false;
    }
}
