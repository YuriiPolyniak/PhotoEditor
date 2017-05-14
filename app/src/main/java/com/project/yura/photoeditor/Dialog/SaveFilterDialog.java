package com.project.yura.photoeditor.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.project.yura.photoeditor.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SaveFilterDialog extends DialogFragment {
    @BindView(R.id.name_of_filter) EditText filterEditText;

    private ISaveFilter context;
   // float[] filterMatrix;
    //DialogInterface.OnClickListener positiveListener;

    public static SaveFilterDialog getDialog(ISaveFilter context){
    //public static SaveFilterDialog getDialog(DialogInterface.OnClickListener positiveListener){
        SaveFilterDialog dialog = new SaveFilterDialog();
       // dialog.filterMatrix = filterMatrix;
        //dialog.positiveListener = positiveListener;
        dialog.context = context;

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_save_filter, null);
        ButterKnife.bind(this, view);

        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Save filter")
                .setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (context != null) {
                            context.saveFilter(filterEditText.getText().toString());
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg) {
                final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

                filterEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() == 0) {
                            positiveButton.setEnabled(false);
                            positiveButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        } else {
                            positiveButton.setEnabled(true);
                            positiveButton.setTextColor(getResources().getColor(R.color.darkOrange));
                        }

                        if (s.length() > 8) {
                            filterEditText.setText(s.subSequence(0, 8));
                            filterEditText.setSelection(
                                    filterEditText.getText().length());
                        }
                    }

                    //region Stub
                    @Override
                    public void afterTextChanged(Editable s) {

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }
                    //endregion
                });
                filterEditText.setText("");
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(getResources().getColor(R.color.colorText));
            }
        });

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    public interface ISaveFilter {
        void saveFilter (String name);
    }
}
