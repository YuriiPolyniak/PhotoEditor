package com.project.yura.photoeditor.processing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.Matrix;
import android.util.Log;

import com.project.yura.photoeditor.R;
import com.project.yura.photoeditor.processing.model.CustomArray;

public class CustomFrameFilters extends BaseFilter {
    private Context context = null;
    private final Bitmap pattern1;
    private final Bitmap pattern2;
    private final Bitmap pattern3;

    public CustomFrameFilters(Context context) {
        this.context = context;
        pattern1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.grunge_frame_1);
        pattern2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.grunge_frame_2);
        pattern3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.grunge_frame_3);
    }

    public IFilter[] getFilters() {
        IFilter[] frameFilters = new IFilter[]{
            new FrameFilter1(),
            new FrameFilter2(),
            new FrameFilter3(),
            new FrameFilter4(),
            new FrameFilter5(),
            new FrameFilter6(),
            //new CustomFilter(),
        };

        return frameFilters;
    }

    public class FrameFilter1 implements IFilter {
        private final String name = "Black 1";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {
            Matrix m = new Matrix();

            Bitmap overlay;
            if (image.getHeight() > image.getWidth()) {
                m.postRotate(90);
                overlay = Bitmap.createScaledBitmap(pattern1, image.getHeight(), image.getWidth(), true);
            } else {
                overlay = Bitmap.createScaledBitmap(pattern1, image.getWidth(), image.getHeight(), true);
            }

            overlay = Bitmap.createBitmap(overlay, 0, 0,
                    overlay.getWidth(),
                    overlay.getHeight(), m, true);

            Bitmap bmOverlay = Bitmap.createBitmap(image.getWidth(), image.getHeight(), image.getConfig());


            Canvas canvas = new Canvas(bmOverlay);
            canvas.drawBitmap(image, new Matrix(), null);
            canvas.drawBitmap(overlay, 0,0, null);

            return bmOverlay;
        }

        @Override
        public boolean hasWeight() {
            return false;
        }
    }

    public class FrameFilter2 implements IFilter {
        private final String name = "White 1";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {
            Matrix m = new Matrix();

            Bitmap overlay;
            if (image.getHeight() > image.getWidth()) {
                m.postRotate(90);
                overlay = Bitmap.createScaledBitmap(pattern1, image.getHeight(), image.getWidth(), true);
            } else {
                overlay = Bitmap.createScaledBitmap(pattern1, image.getWidth(), image.getHeight(), true);
            }

            overlay = Bitmap.createBitmap(overlay, 0, 0,
                    overlay.getWidth(),
                    overlay.getHeight(), m, true);

            ColorMatrix overlayColorMatrix = new ColorMatrix();
            overlayColorMatrix.set(new float[]{
                    1, 0, 0, 0, 255,
                    0, 1, 0, 0, 255,
                    0, 0, 1, 0, 255,
                    0, 0, 0, 1, 0
            });
            Bitmap overlay1 = applyColorMatrix(overlay, overlayColorMatrix);
            Bitmap bmOverlay = Bitmap.createBitmap(image.getWidth(), image.getHeight(), image.getConfig());


            Canvas canvas = new Canvas(bmOverlay);
            canvas.drawBitmap(image, new Matrix(), null);
            canvas.drawBitmap(overlay1, 0,0, null);

            return bmOverlay;
        }

        @Override
        public boolean hasWeight() {
            return false;
        }
    }

    public class FrameFilter3 implements IFilter {
        private final String name = "Black 2";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {
            Matrix m = new Matrix();

            Bitmap overlay;
            if (image.getHeight() > image.getWidth()) {
                m.postRotate(90);
                overlay = Bitmap.createScaledBitmap(pattern2, image.getHeight(), image.getWidth(), true);
            } else {
                overlay = Bitmap.createScaledBitmap(pattern2, image.getWidth(), image.getHeight(), true);
            }

            overlay = Bitmap.createBitmap(overlay, 0, 0,
                    overlay.getWidth(),
                    overlay.getHeight(), m, true);

            Bitmap bmOverlay = Bitmap.createBitmap(image.getWidth(), image.getHeight(), image.getConfig());

            Canvas canvas = new Canvas(bmOverlay);
            canvas.drawBitmap(image, new Matrix(), null);
            canvas.drawBitmap(overlay, 0,0, null);

            return bmOverlay;
        }

        @Override
        public boolean hasWeight() {
            return false;
        }
    }

    public class FrameFilter4 implements IFilter {
        private final String name = "White 2";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {
            Matrix m = new Matrix();

            Bitmap overlay;
            if (image.getHeight() > image.getWidth()) {
                m.postRotate(90);
                overlay = Bitmap.createScaledBitmap(pattern2, image.getHeight(), image.getWidth(), true);
            } else {
                overlay = Bitmap.createScaledBitmap(pattern2, image.getWidth(), image.getHeight(), true);
            }

            overlay = Bitmap.createBitmap(overlay, 0, 0,
                    overlay.getWidth(),
                    overlay.getHeight(), m, true);

            ColorMatrix overlayColorMatrix = new ColorMatrix();
            overlayColorMatrix.set(new float[]{
                    1, 0, 0, 0, 255,
                    0, 1, 0, 0, 255,
                    0, 0, 1, 0, 255,
                    0, 0, 0, 1, 0
            });

            Bitmap overlay1 = applyColorMatrix(overlay, overlayColorMatrix);
            Bitmap bmOverlay = Bitmap.createBitmap(image.getWidth(), image.getHeight(), image.getConfig());


            Canvas canvas = new Canvas(bmOverlay);
            canvas.drawBitmap(image, new Matrix(), null);
            canvas.drawBitmap(overlay1, 0,0, null);

            return bmOverlay;
        }

        @Override
        public boolean hasWeight() {
            return false;
        }
    }

    public class FrameFilter5 implements IFilter {
        private final String name = "Black 3";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {
            Matrix m = new Matrix();

            Bitmap overlay;
            if (image.getHeight() > image.getWidth()) {
                m.postRotate(90);
                overlay = Bitmap.createScaledBitmap(pattern3, image.getHeight(), image.getWidth(), true);
            } else {
                overlay = Bitmap.createScaledBitmap(pattern3, image.getWidth(), image.getHeight(), true);
            }

            overlay = Bitmap.createBitmap(overlay, 0, 0,
                    overlay.getWidth(),
                    overlay.getHeight(), m, true);

            Bitmap bmOverlay = Bitmap.createBitmap(image.getWidth(), image.getHeight(), image.getConfig());

            Canvas canvas = new Canvas(bmOverlay);
            canvas.drawBitmap(image, new Matrix(), null);
            canvas.drawBitmap(overlay, 0,0, null);

            return bmOverlay;
        }

        @Override
        public boolean hasWeight() {
            return false;
        }
    }

    public class FrameFilter6 implements IFilter {
        private final String name = "White 3";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, int weight) {
            Matrix m = new Matrix();

            Bitmap overlay;
            if (image.getHeight() > image.getWidth()) {
                m.postRotate(90);
                overlay = Bitmap.createScaledBitmap(pattern3, image.getHeight(), image.getWidth(), true);
            } else {
                overlay = Bitmap.createScaledBitmap(pattern3, image.getWidth(), image.getHeight(), true);
            }

            overlay = Bitmap.createBitmap(overlay, 0, 0,
                    overlay.getWidth(),
                    overlay.getHeight(), m, true);

            ColorMatrix overlayColorMatrix = new ColorMatrix();
            overlayColorMatrix.set(new float[]{
                    1, 0, 0, 0, 255,
                    0, 1, 0, 0, 255,
                    0, 0, 1, 0, 255,
                    0, 0, 0, 1, 0
            });

            Bitmap overlay1 = applyColorMatrix(overlay, overlayColorMatrix);
            Bitmap bmOverlay = Bitmap.createBitmap(image.getWidth(), image.getHeight(), image.getConfig());


            Canvas canvas = new Canvas(bmOverlay);
            canvas.drawBitmap(image, new Matrix(), null);
            canvas.drawBitmap(overlay1, 0,0, null);

            return bmOverlay;
        }

        @Override
        public boolean hasWeight() {
            return false;
        }
    }

    public class CustomFilter implements IFilter {
        private final String name = "Circle";

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Bitmap applyFilter(Bitmap image, final int weight) {
            long startTime = System.currentTimeMillis();

            final double fWeight = weight / 100.0;
            final int height = image.getHeight();
            final int width = image.getWidth();
            final float circleWidth = width / 6.0f;
            final float circleHeight = height / 6.0f;
            int[] pixels = new int[height * width];
            image.getPixels(pixels, 0, width, 0, 0, width, height);

            final CustomArray arr = new CustomArray(pixels, width, height);

            final int length = 3;
            final Thread[] threads = new Thread[length];

            for (int i = 0; i < length; i++) {
                int a, b;
                a = i * height/length;
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
                                if (i < circleWidth || i > width - circleWidth
                                        || j < circleHeight || j > height - circleHeight) {
                                    int pix = arr.IJ(j, i);
                                    int newPix = -1;
                                    arr.IJ(j, i, newPix);
                                }
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

            Log.d("FILTER", "Time: " + finishTime);

            return Bitmap.createBitmap(arr.array, width, height, Bitmap.Config.ARGB_8888);
        }

        @Override
        public boolean hasWeight() {
            return false;
        }
    }
}
