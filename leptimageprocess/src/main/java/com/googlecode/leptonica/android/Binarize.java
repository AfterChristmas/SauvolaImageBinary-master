/*
 * Copyright (C) 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.googlecode.leptonica.android;

import androidx.annotation.FloatRange;

/**
 * Image binarization methods.
 * 
 * @author alanv@google.com (Alan Viverette)
 */
@SuppressWarnings("WeakerAccess")
public class Binarize {
    static {
        System.loadLibrary("lept");
    }

    // Sauvola thresholding constants

    public final static int SAUVOLA_DEFAULT_WINDOW_HALFWIDTH = 8;

    public final static float SAUVOLA_DEFAULT_REDUCTION_FACTOR = 0.35f;

    public final static int SAUVOLA_DEFAULT_NUM_TILES_X = 1;

    public final static int SAUVOLA_DEFAULT_NUM_TILES_Y = 1;


    /**
     * Performs Sauvola binarization using default values.
     * 
     * @see #sauvolaBinarizeTiled(Pix, int, float, int, int)
     * 
     * @param pixs An 8 bpp PIX source image.
     * @return A 1 bpp thresholded PIX image.
     */
    public static Pix sauvolaBinarizeTiled(Pix pixs) {
        return sauvolaBinarizeTiled(pixs, SAUVOLA_DEFAULT_WINDOW_HALFWIDTH, 
                SAUVOLA_DEFAULT_REDUCTION_FACTOR, SAUVOLA_DEFAULT_NUM_TILES_X, 
                SAUVOLA_DEFAULT_NUM_TILES_Y);
    }

    /**
     * Performs Sauvola binarization.
     * <p>
     * Notes:
     * <ol>
     * <li> The window width and height are 2 * whsize + 1.  The minimum
     * value for whsize is 2; typically it is &gt;= 7.
     * <li> For nx == ny == 1, this defaults to pixSauvolaBinarize().
     * <li> Why a tiled version?
     * (a) Because the mean value accumulator is a uint32, overflow
     * can occur for an image with more than 16M pixels.
     * (b) The mean value accumulator array for 16M pixels is 64 MB.
     * The mean square accumulator array for 16M pixels is 128 MB.
     * Using tiles reduces the size of these arrays.
     * (c) Each tile can be processed independently, in parallel,
     * on a multicore processor.
     * <li> The Sauvola threshold is determined from the formula:
     *   t = m * (1 - k * (1 - s / 128))
     * where:
     *   t = local threshold
     *   m = local mean
     *   k = @factor (&gt;= 0)   [ typ. 0.35 ]
     *   s = local standard deviation, which is maximized at
     *       127.5 when half the samples are 0 and half are 255.
     * <li> The basic idea of Niblack and Sauvola binarization is that
     * the local threshold should be less than the median value, and the larger
     * the variance, the closer to the median it should be chosen.  Typical 
     * values for k are between 0.2 and 0.5.
     * </ol>
     *   
     * @param pixs An 8 bpp PIX source image.
     * @param whsize Window half-width for measuring local statistics
     * @param factor Factor for reducing threshold due to variance; &gt;= 0
     * @param nx Subdivision into tiles; &gt;= 1
     * @param ny Subdivision into tiles; &gt;= 1
     * @return A 1 bpp thresholded PIX image.
     */
    public static Pix sauvolaBinarizeTiled(Pix pixs, int whsize, @FloatRange(from=0.0) float factor,
                                           int nx, int ny) {
        if (pixs == null)
            throw new IllegalArgumentException("Source pix must be non-null");
        if (pixs.getDepth() != 8)
            throw new IllegalArgumentException("Source pix depth must be 8bpp");

        long nativePix = nativeSauvolaBinarizeTiled(pixs.getNativePix(), 
                whsize, factor, nx, ny);

        if (nativePix == 0)
            throw new RuntimeException("Failed to perform Sauvola binarization on image");

        return new Pix(nativePix);        
    }

    // ***************
    // * NATIVE CODE *
    // ***************
    private static native long nativeSauvolaBinarizeTiled(
            long nativePix, int whsize, float factor, int nx, int ny);
}
