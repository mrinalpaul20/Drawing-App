package com.mrinal.zersey.customviews;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.mrinal.zersey.R;
import com.mrinal.zersey.helpers.InputValidator;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.mrinal.zersey.helpers.Dialogs.showErrorDialog;

public class SaveDrawingDialog extends Dialog {
    private EditText titleEt;
    private SaveDrawingListener listener;
    private Bitmap bitmap;

    public SaveDrawingDialog(@NonNull Context context, Bitmap bitmap, SaveDrawingListener listener) {
        super(context);
        this.bitmap = bitmap;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_save_drawing);
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        if (window != null) {
            getWindow().setGravity(Gravity.CENTER);
            getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);
        }
        titleEt = findViewById(R.id.title_et);
        ImageView drawingIv = findViewById(R.id.drawing_iv);
        Button cancelBtn = findViewById(R.id.cancel_btn);
        Button saveBtn = findViewById(R.id.save_btn);
        drawingIv.setImageBitmap(bitmap);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (InputValidator.validateName(titleEt) && connectedToInternet()) {
                    String title = titleEt.getText().toString().trim();
                    listener.uploadDrawing(bitmap, title);
                }
            }
        });
    }

    private boolean connectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected = false;
        NetworkInfo activeNetwork;
        if (connectivityManager != null) {
            activeNetwork = connectivityManager.getActiveNetworkInfo();
            isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        if (!isConnected)
            showErrorDialog(getContext(), null, "No Internet!", "Please check your network and try again.");
        return isConnected;
    }

    public interface SaveDrawingListener {
        void uploadDrawing(Bitmap bitmap, String title);
    }
}