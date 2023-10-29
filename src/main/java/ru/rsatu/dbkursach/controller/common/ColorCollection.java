package ru.rsatu.dbkursach.controller.common;

import javafx.scene.paint.Paint;

public class ColorCollection {
    public static Paint greenOk() {
        return Paint.valueOf("#33f507");
    }

    public static Paint redFailed() {
        return Paint.valueOf("#f50505");
    }
}
