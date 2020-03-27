/* Copyright 2015 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package org.tensorflow.dot;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;

import org.tensorflow.dot.Classifier.Recognition;

public class RecognitionScoreView extends View implements ResultsView {
  private static final float TEXT_SIZE_DIP = 24;
//  private List<Recognition> results;
private Recognition results;
  private final float textSizePx;
  private final Paint fgPaint;
  private final Paint bgPaint;

  public RecognitionScoreView(final Context context, final AttributeSet set) {
    super(context, set);

    textSizePx =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
    fgPaint = new Paint();
    fgPaint.setTextSize(textSizePx);

    bgPaint = new Paint();
    bgPaint.setColor(0xcc4285f4);
  }

  @Override
//  public void setResults(final List<Recognition> results) {
  public void setResults(final Recognition results) {
    this.results = results;
    postInvalidate();
  }

  @Override
  public void onDraw(final Canvas canvas) {
    final int x = 50;
    int y = (int) (fgPaint.getTextSize() * 1.5f);

    canvas.drawPaint(bgPaint);

    if (results != null) {
      if(results.getTitle().equals("positive")){
        canvas.drawText("There is a pothole!"+results.getConfidence(), x, y, fgPaint);
      }else{
        canvas.drawText("There isn't a pothole!", x, y, fgPaint);
      }
    }
  }


}
