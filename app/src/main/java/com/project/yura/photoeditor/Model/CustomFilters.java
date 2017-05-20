package com.project.yura.photoeditor.Model;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.Matrix;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.ScriptIntrinsicConvolve3x3;
import android.renderscript.ScriptIntrinsicConvolve5x5;
import android.util.Log;

import com.project.yura.photoeditor.R;

import java.util.Map;

public class CustomFilters extends BaseFilter{
    private Context context = null;

    public CustomFilters (Context context) {
        this.context = context;
    }

    public IFilter[] getFilters() {
        return new IFilter[]{
                new GrayScaleFilter(),
                new PolaroidFilter(),
                new InverseFilter(),
                new SepiaFilter(),
                new BlackAndWhiteFilter(),
                new BlurFilter(),
                new EmbossFilter(),
                new SharpenFilter(),
                new EdgeEnhanceFilter(),
                new GaussianBlurFilter(),
                //new MyGrayScaleFilter(),
                new My2GrayScaleFilter(),
                new MyBlackAndWhiteFilter(),
                //new MyAdjustFilter(),
                //new BrightnessFilter(),
                //new ContrastFilter(),
                new SaturationFilter(),
                //new AltSaturationFilter(),
                //new BalanceFilter(),
                //new Balance2Filter(),
                new OverlayFilter(),
                //new Sepia1Filter()
        };
    }

    private IFilter getFilter(final String name, final float[] array) {
        if (array == null || array.length != 20)
            return null;

        return new IFilter() {
            final String filterName = name;
            final float[] colorMatrix = array;

            @Override
            public String getName() {
                return filterName;
            }

            @Override
            public Bitmap applyFilter(Bitmap image, int weight) {
                ColorMatrix matrix = new ColorMatrix();

                matrix.set(colorMatrix);

                return applyColorMatrix(image, matrix);
            }

            @Override
            public boolean hasWeight() {
                return false;
            }
        };
    }

    public IFilter[] getCustomFilters() {
        Map<String, float[]> arrays = new PreferencesHelper(context).getCustomFiltersMatrix();
        Object[] keys = arrays.keySet().toArray();

        IFilter[] customFilters = new IFilter[keys.length];

        for (int i = 0; i < keys.length; i++) {
            customFilters[i] = getFilter((String) keys[i], arrays.get(keys[i]));
        }

        return customFilters;
    }

    //region Filters

    public class GrayScaleFilter implements IFilter {
        private final String name = "GrayScale";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {
            float fWeight = weight / 100.0f;

            //ColorMatrix colorMatrix = new ColorMatrix();
            //colorMatrix.setSaturation(1 - fWeight);
            float m = 0.5f * fWeight;
            float c = 1f - 0.5f * (fWeight);
            ColorMatrix colorMatrix = new ColorMatrix(new float[] {
                    c, m, m, 0, 0,
                    m, c, m, 0, 0,
                    m, m, c, 0, 0,
                    0, 0, 0, 1, 0,
            });
            return applyColorMatrix(image, colorMatrix);
        }

        @Override
        public boolean hasWeight() {
            return true;
        }
    }

    public class PolaroidFilter implements IFilter {
        private final String name = "Polaroid";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {
            float fWeight = weight / 20.0f;
            ColorMatrix matrix = new ColorMatrix();

            float[] coefficients = new float[]{
                    1 + fWeight * 0.438f, -0.062f * fWeight, -0.062f * fWeight, 0, 0,
                    -0.122f * fWeight, 1 + 0.378f * fWeight, -0.122f * fWeight, 0, 0,
                    -0.016f * fWeight, -0.016f * fWeight, 1 + 0.483f * fWeight, 0, 0,
                    0,       0,       0, 1, 0,
            };

            matrix.set(coefficients);

//            matrix.set(new float[]{
//                    1.438f, -0.062f, -0.062f, 0, 0,
//                    -0.122f,  1.378f, -0.122f, 0, 0,
//                    -0.016f, -0.016f,  1.483f, 0, 0,
//                    0,       0,       0, 1, 0,
//            });

            return applyColorMatrix(image, matrix);
        }

        @Override
        public boolean hasWeight() {
            return true;
        }
    }

    public class InverseFilter implements IFilter {
        private final String name = "Inverse";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {

            ColorMatrix matrix = new ColorMatrix();

            matrix.set(new float[]{
                    -1,  0,  0, 0, 255,
                    0, -1,  0, 0, 255,
                    0,  0, -1, 0, 255,
                    0,  0,  0, 1,   0
            });

            return applyColorMatrix(image, matrix);
        }

        @Override
        public boolean hasWeight() {
            return false;
        }
    }

    public class SepiaFilter implements IFilter {
        private final String name = "Sepia";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {
            float fW = 1 - weight / 100.0f;

            ColorMatrix colorMatrix = new ColorMatrix();
            //colorMatrix.setSaturation(1 - fWeight);
            float invSat = 1 - fW;
            float R = 0.213f * invSat;
            float G = 0.715f * invSat;
            float B = 0.072f * invSat;

            float[] coefficients = new float[]{
                    R + fW, G,      B,      0, 0,
                    R,      G + fW, B,      0, 0,
                    R,      G,      B + fW, 0, 0,
                    0,      0,      0,      1, 0,
            };

            colorMatrix.set(coefficients);
            ///////
            ColorMatrix colorScale = new ColorMatrix();
            colorScale.setScale(1, 1, 0.8f, 1);
            colorMatrix.postConcat(colorScale);

            return applyColorMatrix(image, colorMatrix);
        }

        @Override
        public boolean hasWeight() {
            return true;
        }
    }

    public class Sepia1Filter implements IFilter {
        private final String name = "Sepia1";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {
            float fWeight = weight / 100.0f;

            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(1 - fWeight);

            ColorMatrix colorScale = new ColorMatrix();
            colorScale.setScale(1, 1, 0.8f, 1);
            colorMatrix.postConcat(colorScale);

            return applyColorMatrix(image, colorMatrix);
        }

        @Override
        public boolean hasWeight() {
            return true;
        }
    }

    public class BlackAndWhiteFilter implements IFilter {
        private final String name = "B & W";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {
            float fWeight = weight / 100.0f;

            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(1 - fWeight);
            float m = 255f;
            float t = -255 * 128f;
            ColorMatrix grayFilter = new ColorMatrix(new float[] {
                    m, 0, 0, 1, t,
                    0, m, 0, 1, t,
                    0, 0, m, 1, t,
                    0, 0, 0, 1, 0,
            });
            colorMatrix.postConcat(grayFilter);

            return applyColorMatrix(image, colorMatrix);
        }

        @Override
        public boolean hasWeight() {
            return true;
        }
    }

    public class BlurFilter implements IFilter {
        private final String name = "Blur";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {
            long startTime = System.currentTimeMillis();

            float radius = weight / 4.0f;
            radius = Math.max(radius, 0.0001f);

            Bitmap outputBitmap = Bitmap.createBitmap(
                    image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);

            final RenderScript renderScript = RenderScript.create(context);
            Allocation tmpIn = Allocation.createFromBitmap(renderScript, image);

            Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);

            ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript,
                    Element.U8_4(renderScript));
            theIntrinsic.setRadius(radius);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);

            float finishTime = (System.currentTimeMillis() - startTime) / 1000.0f;
            //t.makeText(context, "Time: " + finishTime, Toast.LENGTH_SHORT).show();
            Log.d("FILTER", "Time: " + finishTime);

            return outputBitmap;
        }

        @Override
        public boolean hasWeight() {
            return true;
        }
    }

    public class EmbossFilter implements IFilter {
        private final String name = "Emboss";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {
            long startTime = System.currentTimeMillis();

            Bitmap bitmap = Bitmap.createBitmap(
                    image.getWidth(), image.getHeight(),
                    Bitmap.Config.ARGB_8888);

            float[] coefficients = new float[] {
                    -2, -1,  0,
                    -1,  1,  1,
                    0,  1,  2
            };

            RenderScript rs = RenderScript.create(context);

            Allocation allocIn = Allocation.createFromBitmap(rs, image);
            Allocation allocOut = Allocation.createFromBitmap(rs, bitmap);

            ScriptIntrinsicConvolve3x3 convolution
                    = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
            convolution.setInput(allocIn);
            convolution.setCoefficients(coefficients);
            convolution.forEach(allocOut);

            allocOut.copyTo(bitmap);
            rs.destroy();

            float finishTime = (System.currentTimeMillis() - startTime) / 1000.0f;
            //Toast.makeText(context, "Time: " + finishTime, Toast.LENGTH_SHORT).show();
            Log.d("FILTER", "Time: " + finishTime);

            return bitmap;
        }

        @Override
        public boolean hasWeight() {
            return false;
        }
    }

    public class SharpenFilter implements IFilter {
        private final String name = "Sharpen";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {
            long startTime = System.currentTimeMillis();

            Bitmap bitmap = Bitmap.createBitmap(
                    image.getWidth(), image.getHeight(),
                    Bitmap.Config.ARGB_8888);

            float[] coefficients = new float[] {
                    0,  -1,  0,
                    -1,  5,  -1,
                    0,  -1,  0,
            };

            RenderScript rs = RenderScript.create(context);

            Allocation allocIn = Allocation.createFromBitmap(rs, image);
            Allocation allocOut = Allocation.createFromBitmap(rs, bitmap);

            ScriptIntrinsicConvolve3x3 convolution
                    = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
            convolution.setInput(allocIn);
            convolution.setCoefficients(coefficients);
            convolution.forEach(allocOut);

            allocOut.copyTo(bitmap);
            rs.destroy();

            float finishTime = (System.currentTimeMillis() - startTime) / 1000.0f;
            //Toast.makeText(context, "Time: " + finishTime, Toast.LENGTH_SHORT).show();
            Log.d("FILTER", "Time: " + finishTime);

            return bitmap;
        }

        @Override
        public boolean hasWeight() {
            return false;
        }
    }

    public class EdgeEnhanceFilter implements IFilter {
        private final String name = "Edge";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {
            long startTime = System.currentTimeMillis();

            Bitmap bitmap = Bitmap.createBitmap(
                    image.getWidth(), image.getHeight(),
                    Bitmap.Config.ARGB_8888);

            float[] coefficients = new float[] {
                    0, 0, 0,
                    -1, 2, 0,
                    0, 0, 0,
            };

            RenderScript rs = RenderScript.create(context);

            Allocation allocIn = Allocation.createFromBitmap(rs, image);
            Allocation allocOut = Allocation.createFromBitmap(rs, bitmap);

            ScriptIntrinsicConvolve3x3 convolution
                    = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
            convolution.setInput(allocIn);
            convolution.setCoefficients(coefficients);
            convolution.forEach(allocOut);

            allocOut.copyTo(bitmap);
            rs.destroy();

            float finishTime = (System.currentTimeMillis() - startTime) / 1000.0f;
            //Toast.makeText(context, "Time: " + finishTime, Toast.LENGTH_SHORT).show();
            Log.d("FILTER", "Time: " + finishTime);

            return bitmap;
        }

        @Override
        public boolean hasWeight() {
            return false;
        }
    }

    public class GaussianBlurFilter implements IFilter {
        private final String name = "Gauss";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {
            float fW = weight / 100.0f;

            long startTime = System.currentTimeMillis();

            Bitmap bitmap = Bitmap.createBitmap(
                    image.getWidth(), image.getHeight(),
                    Bitmap.Config.ARGB_8888);

//            float[] coefficients = new float[] {
//                    0.1f,   0.25f,  0.5f,   0.25f,  0.1f,
//                    0.25f,  0.5f,   0.85f,  0.5f,   0.25f,
//                    0.5f,   0.85f,  1.0f,   0.85f,  0.5f,
//                    0.25f,  0.5f,   0.85f,  0.5f,   0.25f,
//                    0.1f,   0.25f,  0.5f,   0.25f,  0.1f,
//            };
                float[] coefficients = new float[] {
                    fW *0.1f,   fW *0.25f,  fW *0.5f,   fW *0.25f,  fW *0.1f,
                    fW *0.25f,  fW *0.5f,   fW *0.85f,  fW *0.5f,   fW *0.25f,
                    fW *0.5f,   fW *0.85f,  1.0f,       fW *0.85f,  fW *0.5f,
                    fW *0.25f,  fW *0.5f,   fW *0.85f,  fW *0.5f,   fW *0.25f,
                    fW *0.1f,   fW *0.25f,  fW *0.5f,   fW *0.25f,  fW *0.1f,
            };
            float sum = 0;
            for (float element : coefficients) {
                sum += element;
            }
            for (int i = 0; i < coefficients.length; i++) {
                coefficients[i] /= sum;
            }

            RenderScript rs = RenderScript.create(context);

            Allocation allocIn = Allocation.createFromBitmap(rs, image);
            Allocation allocOut = Allocation.createFromBitmap(rs, bitmap);

            ScriptIntrinsicConvolve5x5 convolution
                    = ScriptIntrinsicConvolve5x5.create(rs, Element.U8_4(rs));
            convolution.setInput(allocIn);
            convolution.setCoefficients(coefficients);
            convolution.forEach(allocOut);

            allocOut.copyTo(bitmap);
            rs.destroy();

            float finishTime = (System.currentTimeMillis() - startTime) / 1000.0f;
            //Toast.makeText(context, "Time: " + finishTime, Toast.LENGTH_SHORT).show();
            Log.d("FILTER", "Time: " + finishTime);

            return bitmap;
        }

        @Override
        public boolean hasWeight() {
            return true;
        }
    }


    public class MyGrayScaleFilter implements IFilter {
        private final String name = "GrayScale1";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {
            long startTime = System.currentTimeMillis();

            double fWeight = weight / 100.0;
            int height = image.getHeight();
            int width = image.getWidth();
            int[] pixels = new int[height * width];
            image.getPixels(pixels, 0, width, 0, 0, width, height);

            CustomArray arr = new CustomArray(pixels, width, height);

            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {
                    int pix = arr.IJ(j, i);
                    // Color.argb(255, 255, 0, 0)
                    int R = (pix >> 16) & 0xff;
                    int G = (pix >> 8) & 0xff;
                    int B = pix & 0xff;
                    int newPix = (R + G + B) / 3;

                    int newR = R < newPix ? R + (int)((newPix - R) * fWeight) :
                            R - (int)((R - newPix) * fWeight);
                    int newG = G < newPix ? G + (int)((newPix - G) * fWeight) :
                            G - (int)((G - newPix) * fWeight);
                    int newB = B < newPix ? B + (int)((newPix - B) * fWeight) :
                            B - (int)((B - newPix) * fWeight);
                    newPix = newB + (newG << 8) + (newR << 16) + (0xff << 24);
                    arr.IJ(j, i, newPix);
                }
            }

            float finishTime = (System.currentTimeMillis() - startTime) / 1000.0f;
            //Toast.makeText(context, "Time: " + finishTime, Toast.LENGTH_SHORT).show();
            Log.d("FILTER", "Time: " + finishTime);

            return Bitmap.createBitmap(arr.array, width, height, Bitmap.Config.ARGB_8888);
        }

        @Override
        public boolean hasWeight() {
            return true;
        }
    }

    public class My2GrayScaleFilter implements IFilter {
        private final String name = "GrayScale2";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {
            long startTime = System.currentTimeMillis();

            final double fWeight = weight / 100.0;
            final int height = image.getHeight();
            final int width = image.getWidth();
            int[] pixels = new int[height * width];
            image.getPixels(pixels, 0, width, 0, 0, width, height);

            final CustomArray arr = new CustomArray(pixels, width, height);

            final int length = 3;
            final Thread[] threads = new Thread[length];

            for (int i = 0; i < length; i++) {
                int a, b;
                a = i * height/length;
                //b = i != length - 1 ? (i + 1)* height/length : height;
                b = (i + 1)* height/length;


                threads[i] = new Thread() {
                    private int startRow, endRow;

                    public Thread init(int startRow, int endRow) {
                        this.startRow = startRow;
                        this.endRow = endRow;
                        return this;
                    }

                    @Override
                    public void run() {
                        for (int j = startRow; j < endRow; j++) {
                            for (int i = 0; i < width; i++) {
                                int pix = arr.IJ(j, i);
                                // Color.argb(255, 255, 0, 0)
                                int R = (pix >> 16) & 0xff;
                                int G = (pix >> 8) & 0xff;
                                int B = pix & 0xff;
                                int newPix = (R + G + B) / 3;

                                int newR = R < newPix ? R + (int)((newPix - R) * fWeight) :
                                        R - (int)((R - newPix) * fWeight);
                                int newG = G < newPix ? G + (int)((newPix - G) * fWeight) :
                                        G - (int)((G - newPix) * fWeight);
                                int newB = B < newPix ? B + (int)((newPix - B) * fWeight) :
                                        B - (int)((B - newPix) * fWeight);
                                newPix = newB + (newG << 8) + (newR << 16) + (0xff << 24);
                                arr.IJ(j, i, newPix);
                            }
                        }
                    }
                }.init(a, b);

                threads[i].start();
            }

            try {
                for (Thread thread :threads) {
                    thread.join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            float finishTime = (System.currentTimeMillis() - startTime) / 1000.0f;
            //Toast.makeText(context, "Time: " + finishTime, Toast.LENGTH_SHORT).show();
            Log.d("FILTER", "Time: " + finishTime);

            return Bitmap.createBitmap(arr.array, width, height, Bitmap.Config.ARGB_8888);
        }

        @Override
        public boolean hasWeight() {
            return true;
        }
    }

    public class MyBlackAndWhiteFilter implements IFilter {
        private final String name = "B & W 2";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {
            long startTime = System.currentTimeMillis();

            double fWeight = weight / 100.0;
            int height = image.getHeight();
            int width = image.getWidth();
            int[] pixels = new int[height * width];
            image.getPixels(pixels, 0, width, 0, 0, width, height);

            CustomArray arr = new CustomArray(pixels, width, height);

            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {
                    int pix = arr.IJ(j, i);

                    int R = (pix >> 16) & 0xff;
                    int G = (pix >> 8) & 0xff;
                    int B = pix & 0xff;
                    int newPix = (R + G + B) / 3;
                    newPix = newPix > 255 / 2 ? newPix + (int)((255 - newPix)*fWeight) :
                            (int)(newPix * (1 - fWeight));

                    newPix = newPix + (newPix << 8) + (newPix << 16) + (0xff << 24);
                    arr.IJ(j, i, newPix);
                }
            }

            float finishTime = (System.currentTimeMillis() - startTime) / 1000.0f;
            //Toast.makeText(context, "Time: " + finishTime, Toast.LENGTH_SHORT).show();
            Log.d("FILTER", "Time: " + finishTime);

            return Bitmap.createBitmap(arr.array, width, height, Bitmap.Config.ARGB_8888);
        }

        @Override
        public boolean hasWeight() {
            return true;
        }
    }


    public class BrightnessFilter implements IFilter {
        private final String name = "Brightness";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {
            float fWeight = weight * 3 - 150;

            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.set(new float[]{
                    1, 0, 0, 0, fWeight,
                    0, 1, 0, 0, fWeight,
                    0, 0, 1, 0, fWeight,
                    0, 0, 0, 1, 0
            });

            return applyColorMatrix(image, colorMatrix);
        }

        @Override
        public boolean hasWeight() {
            return true;
        }
    }

    public class ContrastFilter implements IFilter {
        private final String name = "CONTRAST";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {
            float c = weight / 100.0f * 1.6f + 0.2f;
            float t = (1.0f - c) / 2.0f;
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.set(new float[]{
                    c, 0, 0, 0, t,
                    0, c, 0, 0, t,
                    0, 0, c, 0, t,
                    0, 0, 0, 1, 0
            });

            return applyColorMatrix(image, colorMatrix);
        }

        @Override
        public boolean hasWeight() {
            return true;
        }
    }

    public class SaturationFilter implements IFilter {
        private final String name = "SATURATION(not)";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {
            float s = weight / 50.0f;

            //float lumR = 0.3086f;
            //float lumG = 0.6094f;
            //float lumB = 0.0820f;

            float lumR = 0.2125f;
            float lumG = 0.7154f;
            float lumB = 0.0721f;

            float sr = (1 - s) * lumR;
            float sg = (1 - s) * lumG;
            float sb = (1 - s) * lumB;

            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.set(new float[]{
                    sr+s,   sr,     sr,     0,   0,
                    sg,     sg+s,   sg,     0,   0,
                    sb,     sb,     sb+s,   0,   0,
                    0,      0,      0,      1,   0
            });

            return applyColorMatrix(image, colorMatrix);
        }

        @Override
        public boolean hasWeight() {
            return true;
        }
    }

    public class AltSaturationFilter implements IFilter {
        private final String name = "SATURATION";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {
            float fWeight = weight / 50.0f ;

            if (fWeight > 1) {
                fWeight = fWeight + 3 * (fWeight - 1);
            }
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(fWeight);

            return applyColorMatrix(image, colorMatrix);
        }

        @Override
        public boolean hasWeight() {
            return true;
        }
    }

    public class BalanceFilter implements IFilter {
        private final String name = "Color";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {
            long startTime = System.currentTimeMillis();

            float fWeight = weight * 3.6f - 180;
            if (fWeight < 0) {
                fWeight = 360 + fWeight;
            }

            int height = image.getHeight();
            int width = image.getWidth();
            int[] pixels = new int[height * width];
            float[] hsv = new float[3];
            image.getPixels(pixels, 0, width, 0, 0, width, height);

            CustomArray arr = new CustomArray(pixels, width, height);

            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {
                    int pix = arr.IJ(j, i);
                    int A = Color.alpha(pix);

                    Color.colorToHSV(pix, hsv);
                    hsv[0] = (hsv[0] + fWeight) % 360;
                    int newPix = Color.HSVToColor(A, hsv);
                    arr.IJ(j, i, newPix);
                }
            }

            float finishTime = (System.currentTimeMillis() - startTime) / 1000.0f;
            //Toast.makeText(context, "Time: " + finishTime, Toast.LENGTH_SHORT).show();
            Log.d("FILTER", "Time: " + finishTime);

            return Bitmap.createBitmap(arr.array, width, height, Bitmap.Config.ARGB_8888);
        }

        @Override
        public boolean hasWeight() {
            return true;
        }
    }

    public class Balance2Filter implements IFilter {
        private final String name = "Color";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {
            long startTime = System.currentTimeMillis();

            float fWeight = weight * 3.6f - 180;
            if (fWeight < 0) {
                fWeight = 360 + fWeight;
            }

            final int height = image.getHeight();
            final int width = image.getWidth();
            int[] pixels = new int[height * width];

            image.getPixels(pixels, 0, width, 0, 0, width, height);

            final CustomArray arr = new CustomArray(pixels, width, height);

            //////////////////////////////////////////
            final int length = 6;
            final Thread[] threads = new Thread[length];

            for (int i = 0; i < length; i++) {
                int a, b;
                a = i * height/length;
                //b = i != length - 1 ? (i + 1)* height/length : height;
                b = (i + 1)* height/length;


                final float finalFWeight = fWeight;
                threads[i] = new Thread() {
                    private int startRow, endRow;

                    public Thread init(int startRow, int endRow) {
                        this.startRow = startRow;
                        this.endRow = endRow;
                        return this;
                    }

                    @Override
                    public void run() {
                        float[] hsv = new float[3];
                        for (int j = startRow; j < endRow; j++) {
                            for (int i = 0; i < width; i++) {
                                int pix = arr.IJ(j, i);
                                int A = Color.alpha(pix);

                                Color.colorToHSV(pix, hsv);
                                hsv[0] = (hsv[0] + finalFWeight) % 360;
                                int newPix = Color.HSVToColor(A, hsv);
                                arr.IJ(j, i, newPix);
                            }
                        }
                    }
                }.init(a, b);

                threads[i].start();
            }

            try {
                for (Thread thread :threads) {
                    thread.join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /////


            float finishTime = (System.currentTimeMillis() - startTime) / 1000.0f;
            //Toast.makeText(context, "Time: " + finishTime, Toast.LENGTH_SHORT).show();
            Log.d("FILTER", "Time: " + finishTime);

            return Bitmap.createBitmap(arr.array, width, height, Bitmap.Config.ARGB_8888);
        }

        @Override
        public boolean hasWeight() {
            return true;
        }
    }

    public class OverlayFilter implements IFilter {
        private final String name = "Old";
        private final Bitmap pattern;

        public OverlayFilter(){
            pattern = BitmapFactory.decodeResource(context.getResources(), R.drawable.background1);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {
            float fWeight = weight / 100.0f;

            ColorMatrix colorMatrix = new ColorMatrix();
            //colorMatrix.setSaturation(0.3f); // 1- fWeight
            Bitmap editedImage = applyColorMatrix(image, colorMatrix);

//            ColorMatrix colorScale = new ColorMatrix();
//            colorScale.setScale(2, 2, 2, 0.5f);
//            colorMatrix.postConcat(colorScale);

            Bitmap overlay = Bitmap.createScaledBitmap(pattern, image.getWidth(), image.getHeight(), true);
            float c = 0.3f * 1.6f + 0.2f;
            float t = (1.0f - c) / 2.0f;
            ColorMatrix overlayColorMatrix = new ColorMatrix();
            colorMatrix.set(new float[]{
                    c, 0, 0, 0, t,
                    0, c, 0, 0, t,
                    0, 0, c, 0, t,
                    0, 0, 0, 1, 0
            });
            ColorMatrix colorScale = new ColorMatrix();
            colorScale.setScale(1, 1, 1, 0.6f);
            overlayColorMatrix.postConcat(colorScale);
            Bitmap editedOverlay = applyColorMatrix(overlay, overlayColorMatrix);

            Bitmap bmOverlay = Bitmap.createBitmap(image.getWidth(), image.getHeight(), image.getConfig());
            Canvas canvas = new Canvas(bmOverlay);
            canvas.drawBitmap(editedImage, new Matrix(), null);
            canvas.drawBitmap(editedOverlay, 0, 0, null);
//                    set(new float[]{
//                    0.25f,  0.25f,  0.25f,  0,  0.2f,
//                    0.5f,   0.5f,   0.5f,   0,  0.2f,
//                    0.125f, 0.125f, 0.125f, 0,  0.2f,
//                    0,   0,   0,   1,  0
//            });



            //return pattern;
            return bmOverlay;
        }

        @Override
        public boolean hasWeight() {
            return false;
        }
    }
    //endregion

}
