package com.project.yura.photoeditor.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;

import com.project.yura.photoeditor.ui.dialog.SaveFilterDialog;
import com.project.yura.photoeditor.manager.CurrentSession;
import com.project.yura.photoeditor.manager.PreferencesHelper;
import com.project.yura.photoeditor.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewFilterActivity extends AppCompatActivity
    implements SaveFilterDialog.ISaveFilter {
    @BindView(R.id.imageToEdit) ImageView imageToEdit;
    @BindView(R.id.preview_button) ImageView previewButton;
    @BindView(R.id.resize_button) ImageView resizeButton;
    @BindView(R.id.layout_to_hide) ViewGroup layoutToHide;
    //@BindView(R.id.array_input_grid) GridView inputGrid;
    @BindView(R.id.input_numbers) TableLayout inputNumbers;

    private Bitmap editedBitmap = null;
    private CurrentSession currentSession;

    private boolean displayOriginal = false;
    //Float[] inputElements;// = new Float[4*5];
    private double[] inputElements;
    private float[] floatInputElements;
    private final EditText[] inputEditTexts = new EditText[20];
    private int currentIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_filter);

        ButterKnife.bind(this);
        currentSession = CurrentSession.GetInstance();
        editedBitmap = currentSession.currentBitmap;

        imageToEdit.setImageBitmap(editedBitmap);

        inputElements = new double[]{
                1f, 0f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f, 0f,
                0f, 0f, 1f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f,
        };

        bindViews();

        //resetClick();


    }

    @Override
    public void onBackPressed() {
        if (layoutToHide.getVisibility() != View.VISIBLE) {
            resizeClick(resizeButton);
            return;
        }

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Exit?")
                .setMessage("Exit creating new filter?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NewFilterActivity.super.onBackPressed();
                    }
                })
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(getResources().getColor(R.color.darkOrange));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(getResources().getColor(R.color.colorText));
            }
        });
        dialog.show();
    }

    @OnClick(R.id.cancel_button)
    void cancelClick(View view) {
        onBackPressed();
    }

    @OnClick(R.id.ok_button)
    void okClick(View view) {
        /*SaveFilterDialog dialog = SaveFilterDialog.getDialog(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NewFilterActivity.super.onBackPressed();
            }
        });*/
        SaveFilterDialog dialog = SaveFilterDialog.getDialog(this);
        dialog.show(getFragmentManager(), "");

    }

    @Override
    public void saveFilter(String name) {
        floatInputElements = new float[inputElements.length];
        for (int i = 0; i < inputElements.length; i++) {
            floatInputElements[i] = (float)inputElements[i];
        }
        PreferencesHelper.getInstance().addCustomFilter(name, floatInputElements);

        NewFilterActivity.super.onBackPressed();
    }

    // show image without change (and hide)
    @OnClick(R.id.preview_button)
    void previewClick(View view) {
        if (editedBitmap != null) {
            if (displayOriginal) {
                imageToEdit.setImageBitmap(editedBitmap);
                previewButton.setImageResource(R.drawable.preview_button_dark);
                displayOriginal = false;
            } else {
                imageToEdit.setImageBitmap(currentSession.currentBitmap);
                previewButton.setImageResource(R.drawable.preview_button_light);
                displayOriginal = true;
            }
        }
    }

    // hide action bar
    @OnClick(R.id.resize_button)
    void resizeClick(View view) {
        if (layoutToHide.getVisibility() == View.VISIBLE) {
            layoutToHide.setVisibility(View.GONE);
            resizeButton.setImageResource(R.drawable.resize_big);
        } else {
            layoutToHide.setVisibility(View.VISIBLE);
            resizeButton.setImageResource(R.drawable.resize_small);
        }
    }

    @OnClick(R.id.reset_button)
    public void resetClick( ) {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Reset?")
                .setMessage("Reset color correction matrix?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        inputElements = new double[]{
                                1f, 0f, 0f, 0f, 0f,
                                0f, 1f, 0f, 0f, 0f,
                                0f, 0f, 1f, 0f, 0f,
                                0f, 0f, 0f, 1f, 0f,
                        };

                        int saveCurrentIndex = currentIndex;
                        for (int i = 0; i < inputElements.length; i++) {
                            currentIndex = i;
                            inputEditTexts[i].setText(String.valueOf(inputElements[i]));
                        }
                        applyMatrix();
                        currentIndex = saveCurrentIndex;
                        if (currentIndex >= 0) {
                            inputEditTexts[currentIndex].setSelection(
                                    inputEditTexts[currentIndex].getText().length());
                        }
                    }
                })
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(getResources().getColor(R.color.darkOrange));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(getResources().getColor(R.color.colorText));
            }
        });
        dialog.show();

    }

    @OnClick(R.id.help_button)
    void onHelpClick() {
        new AlertDialog.Builder(this)
                .setTitle("New Filter")
                .setMessage("Input the coefficients for new filter matrix. Each row will form the color component of new image, " +
                        "each column determines how much of each old component was used:\n" +
                        "1 - Red\n" +
                        "2 - Green\n" +
                        "3 - Blue\n" +
                        "4 - Alpha")
                .setPositiveButton("Ok", null)
                .show();
    }

    private void bindViews(){
        int rowCount = inputNumbers.getChildCount();
        int columnCount = ((ViewGroup) inputNumbers.getChildAt(0)).getChildCount();
        for (int i = 0; i < rowCount; i++) {
            ViewGroup tableRow = (ViewGroup) inputNumbers.getChildAt(i);
            for (int j = 0; j < tableRow.getChildCount(); j++) {
                EditText editText = (EditText) tableRow.getChildAt(j);
                editText.setTag(i * tableRow.getChildCount() + j);
                inputEditTexts[i * tableRow.getChildCount() + j] = editText;
            }
        }

        View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //Log.d("FOCUS", String.valueOf((int)v.getTag()) + " " + String.valueOf(hasFocus));
                if (!hasFocus) {
                    EditText editText = (EditText) v;
                    String text = editText.getText().toString();
                    if (text.equals("") ||
                            text.equals("-") ||
                            text.equals(".") ||
                            text.equals(",")
                            ) {
                        editText.setText("0.0");
                    }
                }
            }
        };

        //updateInputEdit();
        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                currentIndex = (int) v.getTag();
                return false;
            }
        };

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (currentIndex < 0) {
                    currentIndex = 0;
                    Log.d("Error", "index lower than 0");
                }
                double originalNumber = 0;
                try {
                    originalNumber = Double.parseDouble(s.toString());
                } catch (Exception e) {
                    if (!s.toString().equals("-") &&
                            !s.toString().equals("") &&
                            !s.toString().equals(",") &&
                            !s.toString().equals(".")
                            ){
                        inputEditTexts[currentIndex].setText("");
                        //applyMatrix();
                    }
                }
                double number = Math.max(Math.min(originalNumber, 99f), -99f);
                number = ((int)(number * 100) / 100d);

                //  || (originalNumber != 0 && !String.valueOf(originalNumber).equals(s.toString()))
                if (Double.compare(number, originalNumber) != 0) {
                    inputEditTexts[currentIndex].setText(String.valueOf(number));
                    //applyMatrix();
                    inputEditTexts[currentIndex].setSelection(
                            inputEditTexts[currentIndex].getText().length());
                }

                if (inputElements[currentIndex] != number) {
                    inputElements[currentIndex] = number;
                    applyMatrix();
                }
            }
        };

        for (int i = 0; i < inputEditTexts.length; i++) {
            inputEditTexts[i].setText(String.valueOf(inputElements[i]));
            inputEditTexts[i].setOnFocusChangeListener(focusListener);
            inputEditTexts[i].setOnTouchListener(listener);
            inputEditTexts[i].addTextChangedListener(watcher);
        }
    }

    private void applyMatrix(){
        Bitmap bitmap = Bitmap.createBitmap(currentSession.currentBitmap.getWidth(),
                currentSession.currentBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        floatInputElements = new float[inputElements.length];
        for (int i = 0; i < inputElements.length; i++) {
            floatInputElements[i] = (float)inputElements[i];
        }

        ColorMatrix colorMatrix = new ColorMatrix(floatInputElements);
        paint.setColorFilter(new ColorMatrixColorFilter(
                colorMatrix));
        canvas.drawBitmap(currentSession.currentBitmap, 0, 0, paint);
        editedBitmap = bitmap;
        imageToEdit.setImageBitmap(editedBitmap);

        if (displayOriginal) {
            previewClick(previewButton);
        }
    }

}
