package org.dimamir999.testapp.services;

import android.graphics.Bitmap;

/**
 * Created by dimamir999 on 20.01.16.
 */
public class PhotoScaler {

    public Bitmap scaleForList(Bitmap src, int expectedHeight, int expectedWidth){
        int height = src.getHeight(), width = src.getWidth();
        if(height != expectedHeight || width != expectedWidth) {
            if (height > width) {
                // scale
                double coef = (double) expectedWidth / width;
                src = Bitmap.createScaledBitmap(src, (int) (width * coef), (int) (height * coef), true);
                //get center of image according expected height
                int startY = (int) ((src.getHeight() - expectedHeight) / 2);
                src = Bitmap.createBitmap(src, 0, startY, expectedWidth, expectedHeight);
            } else {
                //scale
                double coef = (double) expectedHeight / height;
                src = Bitmap.createScaledBitmap(src, (int) (width * coef), (int) (height * coef), true);
                //get center of image according expected width
                int startX = (int) ((src.getWidth() - expectedWidth) / 2);
                src = Bitmap.createBitmap(src, startX, 0, expectedWidth, expectedHeight);
            }
        }
        return src;
    }

    public Bitmap scaleForBigSize(Bitmap src, int maxPixelLength) {
        int height = src.getHeight(), width = src.getWidth();
        int max = Math.max(height, width);
        if(max > maxPixelLength){
            double coef = (double)maxPixelLength / max;
            return Bitmap.createScaledBitmap(src, (int)(width * coef), (int)(height * coef), true);
        }else {
            return src;
        }
    }
}
