package com.mrinal.zersey;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mrinal.zersey.pojo.Drawing;

import java.util.ArrayList;
import java.util.List;

import static com.google.firebase.firestore.Query.Direction.DESCENDING;

@SuppressWarnings("ConstantConditions")
class DrawingsRepository {
    private final Query allDrawingsQuery = FirebaseFirestore.getInstance().collection("uploads").orderBy("uploadedAt", DESCENDING);
    private Query paginateDrawingsQuery = allDrawingsQuery.limit(20);
    private DocumentSnapshot lastVisibleDrawing;
    private boolean lastDrawingReached;

    MutableLiveData<List<Drawing>> getDrawings() {
        final MutableLiveData<List<Drawing>> drawings = new MutableLiveData<>();
        if (lastDrawingReached)
            return null;
        if (lastVisibleDrawing != null)
            allDrawingsQuery.startAfter(lastVisibleDrawing);
        paginateDrawingsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Drawing> drawingList = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                        Drawing drawing = document.toObject(Drawing.class);
                        drawing.setId(document.getId());
                        drawingList.add(drawing);
                    }
                    drawings.setValue(drawingList);
                    int querySnapshotSize = task.getResult().size();
                    if (querySnapshotSize < 20)
                        lastDrawingReached = true;
                    else {
                        lastVisibleDrawing = task.getResult().getDocuments().get(querySnapshotSize - 1);
                    }
                }
            }
        });
        return drawings;
    }

    DrawingLiveData getNewDrawingsObserver() {
        return new DrawingLiveData(allDrawingsQuery.limit(1));
    }

    MutableLiveData<List<Drawing>> getSkippedDrawings(String drawingId) {
        final MutableLiveData<List<Drawing>> skippedDrawings = new MutableLiveData<>();
        FirebaseFirestore.getInstance().collection("uploads").document(drawingId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                    paginateDrawingsQuery.endBefore(task.getResult()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<Drawing> drawingList = new ArrayList<>();
                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                    Drawing drawing = document.toObject(Drawing.class);
                                    drawing.setId(document.getId());
                                    drawingList.add(drawing);
                                }
                                skippedDrawings.setValue(drawingList);
                            }
                        }
                    });
            }
        });
        return skippedDrawings;
    }

}