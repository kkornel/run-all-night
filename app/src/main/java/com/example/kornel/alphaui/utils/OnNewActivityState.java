package com.example.kornel.alphaui.utils;

// Updates the buttons in activity when coming from notification
// and after minimalising the app.
public interface OnNewActivityState {
    void updateButtons(boolean isPaused);
}
