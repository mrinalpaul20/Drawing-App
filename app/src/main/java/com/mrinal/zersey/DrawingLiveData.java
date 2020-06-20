package com.mrinal.zersey;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mrinal.zersey.pojo.Drawing;
import com.mrinal.zersey.pojo.DrawingChange;

public class DrawingLiveData extends LiveData<DrawingChange> implements EventListener<QuerySnapshot> {
    private Query query;
    private ListenerRegistration listenerRegistration;

    DrawingLiveData(Query query) {
        this.query = query;
    }

    @Override
    protected void onActive() {
        listenerRegistration = query.addSnapshotListener(this);
    }

    @Override
    protected void onInactive() {
        listenerRegistration.remove();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        if (e != null) return;

        for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
            QueryDocumentSnapshot document = documentChange.getDocument();
            Drawing drawing = document.toObject(Drawing.class);
            drawing.setId(document.getId());
            DrawingChange drawingChange = new DrawingChange(documentChange.getType(), drawing);
            setValue(drawingChange);
        }

    }

}