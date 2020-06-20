package com.mrinal.zersey;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.mrinal.zersey.activities.ViewDrawingActivity;
import com.mrinal.zersey.pojo.Drawing;

import java.util.List;

import static com.mrinal.zersey.helpers.ImageUtil.loadImage;

public class DrawingsAdapter extends RecyclerView.Adapter<DrawingsAdapter.ViewHolder> {
    private Context context;
    private List<Drawing> drawings;

    public DrawingsAdapter(Context context, List<Drawing> drawings) {
        this.context = context;
        this.drawings = drawings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_drawing_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Drawing drawing = drawings.get(position);
        if (drawing != null)
            holder.setData(drawing);
    }

    @Override
    public int getItemCount() {
        return drawings.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView drawingIv;
        TextView titleTv;
        TextView uploaderNameTv;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            drawingIv = itemView.findViewById(R.id.drawing_iv);
            titleTv = itemView.findViewById(R.id.title_tv);
            uploaderNameTv = itemView.findViewById(R.id.uploader_tv);
        }

        void setData(final Drawing drawing) {
            titleTv.setText(drawing.getTitle());
            uploaderNameTv.setText(getUploaderString(drawing));
            loadImage(drawingIv, drawing.getUri());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ViewDrawingActivity.class);
                    intent.putExtra("DRAWING", drawing);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }

        private String getUploaderString(Drawing drawing) {
            if (drawing.getUploaderId().equals(FirebaseAuth.getInstance().getUid()))
                return "Made by You";
            return "By: " + drawing.getUploaderName();
        }

    }
}