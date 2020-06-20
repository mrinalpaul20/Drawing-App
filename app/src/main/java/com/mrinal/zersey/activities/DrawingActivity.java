package com.mrinal.zersey.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mrinal.zersey.R;
import com.mrinal.zersey.customviews.CanvasView;
import com.mrinal.zersey.customviews.ColorPicker;
import com.mrinal.zersey.customviews.SaveDrawingDialog;
import com.mrinal.zersey.pojo.Drawing;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static com.mrinal.zersey.helpers.Dialogs.showErrorDialog;
import static com.mrinal.zersey.helpers.Dialogs.showProgressDialog;

@SuppressWarnings({"ConstantConditions"})
public class DrawingActivity extends AppCompatActivity implements SaveDrawingDialog.SaveDrawingListener, CanvasView.CanvasListener {
    LinearLayout brush;
    LinearLayout eraser;
    LinearLayout undoBtn;
    LinearLayout redoBtn;
    private ProgressDialog progressDialog;
    private CanvasView canvasView;
    private ColorPicker colorPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        brush = findViewById(R.id.brush);
        eraser = findViewById(R.id.eraser);
        undoBtn = findViewById(R.id.undo);
        redoBtn = findViewById(R.id.redo);
        canvasView = findViewById(R.id.canvas_view);
        colorPicker = findViewById(R.id.color_seek_bar);
        colorPicker.init(canvasView);
        canvasView.setListener(this);
    }

    public void undo(View view) {
        canvasView.undo();
    }

    public void redo(View view) {
        canvasView.redo();
    }

    public void clearDrawing(MenuItem item) {
        canvasView.clear();
        colorPicker.setProgress(0);
        brush(item.getActionView());
    }

    public void erase(View view) {
        colorPicker.setVisibility(View.GONE);
        canvasView.startEraser();
        eraser.setBackground(getResources().getDrawable(R.drawable.background_selected));
        brush.setBackground(getResources().getDrawable(R.drawable.background_normal));
    }

    public void brush(View view) {
        colorPicker.setVisibility(View.VISIBLE);
        canvasView.startBrush();
        brush.setBackground(getResources().getDrawable(R.drawable.background_selected));
        eraser.setBackground(getResources().getDrawable(R.drawable.background_normal));
    }

    public void showSaveDrawingDialog(MenuItem item) {
        Bitmap bitmap = canvasView.getBitmap();
        if (bitmap == null)
            Snackbar.make(canvasView, "Draw Something First!", BaseTransientBottomBar.LENGTH_SHORT).show();
        else {
            SaveDrawingDialog saveDrawingDialog = new SaveDrawingDialog(this, bitmap, this);
            saveDrawingDialog.show();
        }
    }

    @Override
    public void uploadDrawing(Bitmap bitmap, final String title) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        String imageName = UUID.randomUUID().toString();
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("drawings").child(imageName);
        final UploadTask uploadTask = storageReference.putBytes(bytes);
        final long expiryTime = System.currentTimeMillis() + 15000;
        startRetryLimiter(uploadTask);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                if (System.currentTimeMillis() < expiryTime)
                    showProgress("Uploading your drawing, " + (int) ((taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()) * 100) + "% complete...");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showError(e.getMessage());
            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful())
                    throw task.getException();
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    saveDrawing(title, downloadUri.toString());
                }
            }
        });
    }

    private void startRetryLimiter(final UploadTask uploadTask) {
        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!uploadTask.isComplete()) {
                    uploadTask.cancel();
                    showError("Seems like network problem. Please check your network and try again");
                }
            }
        }, 15000);
    }

    private void saveDrawing(String title, String uri) {
        showProgress("Saving drawing...");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Drawing drawing = new Drawing(title, uri, currentUser);
        FirebaseFirestore.getInstance().collection("uploads").add(drawing)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        showSuccessDialog();
                        canvasView.clear();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showError(e.getMessage());
                    }
                });
    }

    private void showProgress(String message) {
        progressDialog = showProgressDialog(this, progressDialog, message);
    }

    private void showError(String message) {
        showErrorDialog(this, progressDialog, "OOPS!!", message);
    }

    private void showSuccessDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Drawing Saved")
                .setMessage("Your drawing has been saved to our Dashboard.")
                .setPositiveButton("Cool!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                }).show();
    }

    @Override
    public void changeColorPickerVisibility(int visibility) {
        colorPicker.setVisibility(visibility);
    }

    @Override
    public void onActionPerformed(boolean isUndoAvailable, boolean isRedoAvailable) {
        setButtonState(undoBtn, isUndoAvailable);
        setButtonState(redoBtn, isRedoAvailable);
    }

    private void setButtonState(LinearLayout button, boolean isAvailable) {
        if (button.isClickable() != isAvailable) {
            button.setClickable(isAvailable);
            if (isAvailable)
                button.setBackground(getResources().getDrawable(R.drawable.background_normal));
            else
                button.setBackground(getResources().getDrawable(R.drawable.background_unavailable));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawing, menu);
        return super.onCreateOptionsMenu(menu);
    }
}