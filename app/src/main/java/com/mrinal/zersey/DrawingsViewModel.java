package com.mrinal.zersey;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mrinal.zersey.pojo.Drawing;

import java.util.List;

public class DrawingsViewModel extends ViewModel {
    private DrawingsRepository drawingsRepository;

    public DrawingsViewModel() {
        drawingsRepository = new DrawingsRepository();
    }

    public MutableLiveData<List<Drawing>> getDrawings() {
        return drawingsRepository.getDrawings();
    }

    public DrawingLiveData getNewDrawingsObserver() {
        return drawingsRepository.getNewDrawingsObserver();
    }

    public MutableLiveData<List<Drawing>> getSkippedDrawings(String firstDrawingAvailable) {
        return drawingsRepository.getSkippedDrawings(firstDrawingAvailable);
    }

}