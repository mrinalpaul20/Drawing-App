package com.mrinal.zersey.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.mrinal.zersey.R;
import com.mrinal.zersey.helpers.ImageUtil;
import com.mrinal.zersey.pojo.Drawing;

public class ViewDrawingActivity extends AppCompatActivity {

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_drawing);
        Drawing drawing = getIntent().getParcelableExtra("DRAWING");
        if (drawing == null) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
        ImageView drawingIv = findViewById(R.id.drawing_iv);
        TextView titleTv = findViewById(R.id.title_tv);
        TextView uploaderTv = findViewById(R.id.uploader_tv);
        ImageUtil.loadImage(drawingIv, drawing.getUri());
        titleTv.setText(drawing.getTitle());
        uploaderTv.setText(getUploaderString(drawing));
    }

    private String getUploaderString(Drawing drawing) {
        if (drawing.getUploaderId().equals(FirebaseAuth.getInstance().getUid()))
            return "Made by You";
        return "Artist: " + drawing.getUploaderName();
    }

    public void close(View view) {
        finish();
    }
}