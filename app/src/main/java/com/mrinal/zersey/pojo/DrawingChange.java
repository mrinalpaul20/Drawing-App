package com.mrinal.zersey.pojo;

import com.google.firebase.firestore.DocumentChange;

public class DrawingChange {
    public DocumentChange.Type type;
    public Drawing drawing;

    public DrawingChange(DocumentChange.Type type, Drawing drawing) {
        this.type = type;
        this.drawing = drawing;
    }

}