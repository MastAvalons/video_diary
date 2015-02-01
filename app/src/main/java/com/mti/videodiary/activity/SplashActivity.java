package com.mti.videodiary.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.mti.videodiary.animation.SkewableTextView;
import com.mti.videodiary.utils.UserHelper;

import java.lang.Integer;
import java.util.ArrayList;
import java.util.List;

import mti.com.videodiary.R;

/**
 * Created by Taras  Matolinets on 04.11.14.
 */
public class SplashActivity extends BaseActivity implements ViewTreeObserver.OnPreDrawListener, TextWatcher, View.OnClickListener, ColorPicker.OnColorChangedListener {

    public static final long MEDIUM_DURATION = 1000;
    private static final long SMALL_DURATION = 500;

    private int XColor;
    private int YColor;

    private boolean mChangeGradientSides;

    private static final AccelerateInterpolator sAccelerator = new AccelerateInterpolator();
    private static final LinearInterpolator sLinearInterpolator = new LinearInterpolator();

    private static final TimeInterpolator mOverShooter = new OvershootInterpolator();
    private static final DecelerateInterpolator mDecelerator = new DecelerateInterpolator();

    private ArrayList<Integer> mColorsGradientList = new ArrayList<Integer>();
    private SparseArray<ArrayList<Integer>> mGlobalGradientColor = new SparseArray();

    private ColorPicker mPicker;
    private Button mButtonCustomise;
    private Button mButtonSkip;
    private LinearLayout mLayoutButtonsAction;
    private SkewableTextView mName;
    private SkewableTextView mWelcome;
    private RelativeLayout mContainer;
    private EditText mPersonalName;
    private ImageButton mClickNext;
    private ShapeDrawable mDrawableGradient;
    private CheckBox mLefttColorGradiet;
    private CheckBox mRightColorGradient;
    private CheckBox mTopColorGradient;
    private CheckBox mButtomColorGradient;
    private LinearLayout mLayoutButtonsChangeGradient;
    private GradientDrawable gradientDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        gradientDrawable = new GradientDrawable();
        mDrawableGradient = new ShapeDrawable(new RectShape());
        getSupportActionBar().hide();

        initViews();
        setListeners();
    }

    private void initViews() {
        mContainer = (RelativeLayout) findViewById(R.id.flSplash);
        mName = (SkewableTextView) findViewById(R.id.tvTitle);
        mWelcome = (SkewableTextView) findViewById(R.id.tvWelcome);
        mPersonalName = (EditText) findViewById(R.id.etName);
        mClickNext = (ImageButton) findViewById(R.id.splashBtClickNext);
        mPicker = (ColorPicker) findViewById(R.id.picker);
        mLayoutButtonsAction = (LinearLayout) findViewById(R.id.llButtonsActions);
        mLayoutButtonsChangeGradient = (LinearLayout) findViewById(R.id.llButtonsGradient);

        mButtonCustomise = (Button) findViewById(R.id.btCustomiseApp);
        mButtonSkip = (Button) findViewById(R.id.btSkip);

        mLefttColorGradiet = (CheckBox) findViewById(R.id.cbGradientLeft);
        mRightColorGradient = (CheckBox) findViewById(R.id.cbGradientRight);
        mTopColorGradient = (CheckBox) findViewById(R.id.cbGradientTop);
        mButtomColorGradient = (CheckBox) findViewById(R.id.chGradientBottom);
    }


    private void setListeners() {
        mContainer.getViewTreeObserver().addOnPreDrawListener(this);
        mPicker.setOnColorChangedListener(this);
        mPersonalName.addTextChangedListener(this);

        mClickNext.setOnClickListener(this);
        mButtonCustomise.setOnClickListener(this);
        mButtonSkip.setOnClickListener(this);

        mLefttColorGradiet.setOnClickListener(this);
        mRightColorGradient.setOnClickListener(this);
        mTopColorGradient.setOnClickListener(this);
        mButtomColorGradient.setOnClickListener(this);
    }

    @Override
    public boolean onPreDraw() {
        mContainer.getViewTreeObserver().removeOnPreDrawListener(this);
        mContainer.setScaleX(0);
        mContainer.setScaleY(0);

        ViewPropertyAnimator animationProperty = mContainer.animate();
        animationProperty.scaleX(1).scaleY(1);
        animationProperty.setInterpolator(new OvershootInterpolator());

        YoYo.with(Techniques.ZoomIn).playOn(mWelcome);
        animateView();

        return true;
    }

    private void animateView() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            mContainer.animate().setListener(mAnimViewListener);
        } else
            mContainer.animate().setDuration(SMALL_DURATION).withEndAction(mAnimListenerRunnable);
    }

    private AnimatorListenerAdapter mAnimViewListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            moveView();
        }
    };

    private Runnable mAnimListenerRunnable = new Runnable() {
        @Override
        public void run() {
            moveView();
        }
    };

    private void moveView() {
        PropertyValuesHolder pvhTX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0);
        PropertyValuesHolder pvhTY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0);

        ObjectAnimator moveAnim = ObjectAnimator.ofPropertyValuesHolder(mContainer, pvhTX, pvhTY);
        moveAnim.setDuration(600);
        moveAnim.start();

        moveAnim.addListener(mSlideToNextAnimation);
    }

    private AnimatorListenerAdapter mSlideToNextAnimation = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            slideToNext(mWelcome, mName);
        }
    };

    private void slideToNext(final SkewableTextView currentView, final SkewableTextView nextView) {
        ObjectAnimator currentSkewer = ObjectAnimator.ofFloat(currentView, "skewX", -.5f);
        currentSkewer.setInterpolator(mDecelerator);

        ObjectAnimator currentMover = ObjectAnimator.ofFloat(currentView, View.TRANSLATION_X, -mContainer.getWidth());
        currentMover.setInterpolator(sLinearInterpolator);
        currentMover.setDuration(MEDIUM_DURATION);

        // set next view visible, translate off to right, skew,
        // slide on in parallel, overshoot/wobble, unskew
        nextView.setVisibility(View.VISIBLE);
        nextView.setSkewX(-.5f);
        nextView.setTranslationX(mContainer.getWidth());

        ObjectAnimator nextMover = ObjectAnimator.ofFloat(nextView, View.TRANSLATION_X, 0);
        nextMover.setInterpolator(sAccelerator);
        nextMover.setDuration(MEDIUM_DURATION);

        ObjectAnimator nextSkewer = ObjectAnimator.ofFloat(nextView, "skewX", 0);
        nextSkewer.setInterpolator(mOverShooter);

        AnimatorSet moverSet = new AnimatorSet();
        moverSet.playTogether(currentMover, nextMover);

        AnimatorSet fullSet = new AnimatorSet();
        fullSet.playSequentially(currentSkewer, moverSet, nextSkewer);

        fullSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                UserHelper.sleep(1000);

                moveViewToScreenCenter(nextView, -nextView.getHeight() * 4, 0, true);
            }
        });
        fullSet.start();
    }

    private void moveViewToScreenCenter(View view, int height, int width, boolean isLisenerEnable) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int originalPos[] = new int[2];
        view.getLocationOnScreen(originalPos);

        TranslateAnimation anim = new TranslateAnimation(width, 0, 0, height);
        anim.setDuration(1000);
        anim.setFillAfter(true);

        if (isLisenerEnable && view.getId() == R.id.tvTitle)
            anim.setAnimationListener(mMoveOnScreenListener);

        view.startAnimation(anim);
    }

    private Animation.AnimationListener mMoveOnScreenListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mPersonalName.setVisibility(View.VISIBLE);

            YoYo.AnimationComposer composer = YoYo.with(Techniques.RollIn);
            composer.duration(700);
            composer.playOn(mPersonalName);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (count > 0) {

            if (mClickNext.getVisibility() != View.VISIBLE) {
                mClickNext.setVisibility(View.VISIBLE);
            }
        } else {
            mClickNext.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.splashBtClickNext:
                splashClickNext();
                break;

            case R.id.btCustomiseApp:
                customiseApp();
                break;

            case R.id.btSkip:
                skip();
                break;

            case R.id.cbGradientLeft:
                mLefttColorGradiet.setChecked(true);
                mTopColorGradient.setChecked(false);
                mButtomColorGradient.setChecked(false);
                mRightColorGradient.setChecked(false);
                break;
            case R.id.cbGradientTop:
                mTopColorGradient.setChecked(true);
                mLefttColorGradiet.setChecked(false);
                mButtomColorGradient.setChecked(false);
                mRightColorGradient.setChecked(false);
                break;
            case R.id.cbGradientRight:
                mRightColorGradient.setChecked(true);
                mLefttColorGradiet.setChecked(false);
                mButtomColorGradient.setChecked(false);
                mTopColorGradient.setChecked(false);
                break;
            case R.id.chGradientBottom:
                mButtomColorGradient.setChecked(true);
                mRightColorGradient.setChecked(false);
                mLefttColorGradiet.setChecked(false);
                mTopColorGradient.setChecked(false);
                break;
        }
    }

    private void splashClickNext() {
        mClickNext.setVisibility(View.GONE);
        UserHelper.hideKeyboard(this, mClickNext);

        YoYo.AnimationComposer personalAnim = YoYo.with(Techniques.RollOut);
        personalAnim.duration(700);
        personalAnim.withListener(mPersonalNameAnimation);
        personalAnim.playOn(mPersonalName);
    }


    private void customiseApp() {
        mPicker.setVisibility(View.VISIBLE);
        mLayoutButtonsChangeGradient.setVisibility(View.VISIBLE);

        mButtonCustomise.setVisibility(View.GONE);
        YoYo.AnimationComposer picker = YoYo.with(Techniques.ZoomInUp);
        picker.duration(700);
        picker.playOn(mPicker);

        mButtonSkip.setText(getResources().getText(R.string.splash_set_color));

        YoYo.AnimationComposer buttonSkip = YoYo.with(Techniques.FadeIn);
        buttonSkip.duration(700);
        buttonSkip.playOn(mButtonSkip);

        mPicker.setShowOldCenterColor(false);
        mPicker.setNewCenterColor(Color.TRANSPARENT);
    }

    private void skip() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private com.nineoldandroids.animation.Animator.AnimatorListener mPersonalNameAnimation = new com.nineoldandroids.animation.Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(com.nineoldandroids.animation.Animator animation) {
        }

        @Override
        public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
            mLayoutButtonsAction.setVisibility(View.VISIBLE);

            YoYo.AnimationComposer picker = YoYo.with(Techniques.ZoomIn);
            picker.duration(700);
            picker.playOn(mLayoutButtonsAction);
        }

        @Override
        public void onAnimationCancel(com.nineoldandroids.animation.Animator animation) {
        }

        @Override
        public void onAnimationRepeat(com.nineoldandroids.animation.Animator animation) {
        }
    };

    @Override
    public void onColorChanged(int i) {

        if (mLefttColorGradiet.isChecked())
            addColorGradient(mLefttColorGradiet, i, GradientDrawable.Orientation.LEFT_RIGHT);

        else if (mRightColorGradient.isChecked())
            addColorGradient(mRightColorGradient, i, GradientDrawable.Orientation.RIGHT_LEFT);

        else if (mTopColorGradient.isChecked())
            addColorGradient(mTopColorGradient, i, GradientDrawable.Orientation.TOP_BOTTOM);

        else if (mButtomColorGradient.isChecked())
            addColorGradient(mButtomColorGradient,i, GradientDrawable.Orientation.BOTTOM_TOP);

        mContainer.setBackgroundDrawable(gradientDrawable);
        mButtonSkip.setTextColor(i);
    }

    private void addColorGradient(CompoundButton checkBox, int color, GradientDrawable.Orientation orientation) {
        int viewId = checkBox.getId();
        ArrayList<Integer> arrayListColors = new ArrayList<>();

        if ((mGlobalGradientColor.size() > 0)) {

            ArrayList<Integer> colorList = new ArrayList<>();

            colorList.add(color);
            colorList.add(Color.TRANSPARENT);

            mGlobalGradientColor.append(viewId, colorList);

        } else if ( mGlobalGradientColor.size() == 0) {
            ArrayList<Integer> colorList = new ArrayList<>();

            colorList.add(color);
            colorList.add(Color.TRANSPARENT);

            mGlobalGradientColor.append(viewId, colorList);
        }

        for (int i = 0; i <mGlobalGradientColor.size(); i++) {
            ArrayList<Integer> list = mGlobalGradientColor.get(viewId);

            for (Integer elem : list)
                arrayListColors.add(elem);
        }

        gradientDrawable.setColors(convertIntegers(mGlobalGradientColor.get(viewId)));
        gradientDrawable.setOrientation(orientation);
    }

    public static int[] convertIntegers(List<Integer> integers) {
        int[] ret = new int[integers.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = integers.get(i).intValue();
        }
        return ret;
    }

}
