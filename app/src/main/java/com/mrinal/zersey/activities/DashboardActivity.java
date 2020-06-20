package com.mrinal.zersey.activities;

import android.os.Bundle;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.mrinal.zersey.DrawingLiveData;
import com.mrinal.zersey.DrawingsAdapter;
import com.mrinal.zersey.DrawingsViewModel;
import com.mrinal.zersey.R;
import com.mrinal.zersey.pojo.Drawing;
import com.mrinal.zersey.pojo.DrawingChange;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    RecyclerView drawingsRv;
    private List<Drawing> drawings = new ArrayList<>();
    private DrawingsAdapter drawingsAdapter;
    private boolean isScrolling;
    private DrawingsViewModel drawingsViewModel;
    private final RecyclerView.OnScrollListener SCROLL_LISTENER = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                isScrolling = true;
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            if (layoutManager != null) {
                int firstVisibleDrawingPosition = layoutManager.findFirstVisibleItemPosition();
                int visibleDrawingCount = layoutManager.getChildCount();
                int totalDrawingCount = layoutManager.getItemCount();

                if (isScrolling && (firstVisibleDrawingPosition + visibleDrawingCount == totalDrawingCount)) {
                    isScrolling = false;
                    getDrawings();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        init();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        drawingsRv = findViewById(R.id.drawings_rv);
        drawingsAdapter = new DrawingsAdapter(this, drawings);
        drawingsRv.setItemAnimator(new DefaultItemAnimator());
        drawingsRv.setAdapter(drawingsAdapter);
        drawingsViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new DrawingsViewModel();
            }
        }).get(DrawingsViewModel.class);
        drawingsRv.addOnScrollListener(SCROLL_LISTENER);
        getDrawings();
        setLiveDrawingObserver();
    }

    private void setLiveDrawingObserver() {
        DrawingLiveData drawingLiveData = drawingsViewModel.getNewDrawingsObserver();
        if (drawingLiveData != null) {
            drawingLiveData.observe(this, new Observer<DrawingChange>() {
                @Override
                public void onChanged(DrawingChange drawingChange) {
                    //no other operation will be happening other than addition
                    if (drawingChange.type == DocumentChange.Type.ADDED) {
                        if (drawings.size() > 0) {
                            Drawing currentFirstDrawing = drawings.get(0);
                            if (!currentFirstDrawing.getId().equals(drawingChange.drawing.getId())) {
                                if (drawings.size() > 1) {
                                    Drawing currentSecondDrawing = drawings.get(1);
                                    if (!currentSecondDrawing.getId().equals(drawingChange.drawing.getId())) {
                                        //need refresh as some drawings may have been skipped
                                        refresh();
                                        return;
                                    }
                                }
                                drawings.add(0, drawingChange.drawing);
                                drawingsAdapter.notifyItemInserted(0);
                            }
                        }
                    }
                }
            });
        }
    }

    private void refresh() {
        final MutableLiveData<List<Drawing>> drawingsLiveData = drawingsViewModel.getSkippedDrawings(drawings.get(0).getId());
        if (drawingsLiveData != null)
            drawingsLiveData.observe(this, new Observer<List<Drawing>>() {
                @Override
                public void onChanged(List<Drawing> drawingList) {
                    drawingsLiveData.removeObservers(DashboardActivity.this);
                    drawings.addAll(0, drawingList);
                    drawingsAdapter.notifyDataSetChanged();
                }
            });
    }

    private void getDrawings() {
        final MutableLiveData<List<Drawing>> drawingsLiveData = drawingsViewModel.getDrawings();
        if (drawingsLiveData != null)
            drawingsLiveData.observe(this, new Observer<List<Drawing>>() {
                @Override
                public void onChanged(List<Drawing> drawingList) {
                    drawingsLiveData.removeObservers(DashboardActivity.this);
                    drawings.addAll(drawingList);
                    drawingsAdapter.notifyDataSetChanged();
                }
            });
    }
}