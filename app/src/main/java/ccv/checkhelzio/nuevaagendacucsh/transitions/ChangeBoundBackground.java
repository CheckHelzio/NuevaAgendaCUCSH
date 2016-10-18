/*
 * Copyright 2015 Google Inc.
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

package ccv.checkhelzio.nuevaagendacucsh.transitions;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.transition.ChangeBounds;
import android.transition.TransitionValues;
import android.view.View;
import android.view.ViewGroup;

import ccv.checkhelzio.nuevaagendacucsh.util.AnimUtils;


/**
 * Una extension de {@link ChangeBounds} que toma una captura de la vista compartida y se transforma.
 */
public class ChangeBoundBackground extends ChangeBounds {

    private static final String EXTRA_SHARED_ELEMENT_START_BITMAP = "EXTRA_SHARED_ELEMENT_START_BITMAP";
    private static final String EXTRA_SHARED_ELEMENT_START_BOUNDS = "EXTRA_SHARED_ELEMENT_START_BOUNDS";
    private static boolean fromFab;
    private final Bitmap startBitmap;
    private final Bitmap endBitmap;
    private final Rect startBounds;
    private final Rect endBounds;
    private final long duration = 250L;

    private ChangeBoundBackground(Bitmap mStartBitmap, Rect mStartBounds, Bitmap mEndBitmap, Rect mEndBounds) {
        setPathMotion(new GravityArcMotion());
        startBitmap = mStartBitmap;
        endBitmap = mEndBitmap;
        startBounds = mStartBounds;
        endBounds = mEndBounds;
    }

    /**
     * Configure {@code intent} with the extras needed to initialize this transition.
     */
    public static void addExtras(@NonNull Intent intent, Bitmap startBitmap, Rect startBounds) {
        intent.putExtra(EXTRA_SHARED_ELEMENT_START_BITMAP, startBitmap);
        intent.putExtra(EXTRA_SHARED_ELEMENT_START_BOUNDS, startBounds);
    }

    public static void setup(@NonNull Activity activity, @Nullable View target, boolean b, Rect endBounds, Bitmap viewBitmap) {
        final Intent intent = activity.getIntent();
        if (intent == null || !intent.hasExtra(EXTRA_SHARED_ELEMENT_START_BITMAP)) return;

        final Bitmap startBitmap = activity.getIntent().getParcelableExtra(EXTRA_SHARED_ELEMENT_START_BITMAP);
        final Rect startBounds = activity.getIntent().getParcelableExtra(EXTRA_SHARED_ELEMENT_START_BOUNDS);
        fromFab = b;
        final ChangeBoundBackground sharedEnter = new ChangeBoundBackground(startBitmap, startBounds, viewBitmap, endBounds);
        final ChangeBoundBackground sharedReturn = new ChangeBoundBackground(startBitmap, startBounds, viewBitmap, endBounds);
        if (target != null) {
            sharedEnter.addTarget(target);
            sharedReturn.addTarget(target);
        }
        activity.getWindow().setSharedElementEnterTransition(sharedEnter);
        activity.getWindow().setSharedElementReturnTransition(sharedReturn);
    }

    @Override
    public Animator createAnimator(final ViewGroup sceneRoot,
                                   final TransitionValues startValues,
                                   final TransitionValues endValues) {
        final Animator changeBounds = super.createAnimator(sceneRoot, startValues, endValues);
        if (changeBounds == null) return null;

        TimeInterpolator interpolator = getInterpolator();
        if (interpolator == null) {
            interpolator = AnimUtils.getFastOutSlowInInterpolator(sceneRoot.getContext());
        }

        BitmapDrawable d = new BitmapDrawable(startBitmap);
        BitmapDrawable e = new BitmapDrawable(endBitmap);
        ColorDrawable f = new ColorDrawable(Color.WHITE);
        d.setBounds(0, 0, startBounds.width(), startBounds.height());
        e.setBounds(0, 0, endBounds.width(), endBounds.height());
        f.setBounds(0, 0, endBounds.width(), endBounds.height());
        if (!fromFab) {
            ((ViewGroup) endValues.view).removeAllViews();
            endValues.view.getOverlay().clear();
            d.setAlpha(0);
            endValues.view.getOverlay().add(d);
            endValues.view.getOverlay().add(e);
        } else {
            endValues.view.getOverlay().add(f);
            endValues.view.getOverlay().add(d);
        }

        final Animator colorFade = ObjectAnimator.ofInt(d, "alpha", fromFab ? 0 : 255);
        colorFade.setStartDelay(fromFab ? 0 : duration/2);
        colorFade.setDuration(fromFab ? duration/3 : duration/2);
        changeBounds.setDuration(duration);

        final AnimatorSet transition = new AnimatorSet();
        transition.playTogether(changeBounds);
        transition.playTogether(colorFade);
        if (!fromFab) {
            Animator colorFade2 = ObjectAnimator.ofInt(e, "alpha", 0);
            colorFade2.setDuration(duration);
            transition.playTogether(colorFade2);
        } else {
            Animator colorFade2 = ObjectAnimator.ofInt(f, "alpha", 0);
            colorFade2.setDuration((duration/3) * 2);
            colorFade2.setStartDelay(duration/3);
            transition.playTogether(colorFade2);
        }
        transition.setInterpolator(interpolator);

        transition.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                fromFab = !fromFab;
                if (!fromFab){
                    endValues.view.getOverlay().clear();
                    startValues.view.getOverlay().clear();
                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        return transition;
    }

}
