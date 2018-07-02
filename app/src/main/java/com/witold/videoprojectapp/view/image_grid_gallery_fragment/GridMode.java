package com.witold.videoprojectapp.view.image_grid_gallery_fragment;

import java.io.Serializable;

public class GridMode implements Serializable {
    private int colSpan;
    private int rowSpan;

    public GridMode(int colSpan, int rowSpan) {
        this.colSpan = colSpan;
        this.rowSpan = rowSpan;
    }

    public int getColSpan() {
        return colSpan;
    }

    public int getRowSpan() {
        return rowSpan;
    }

    public int getLandscapeColSpan() {
        return rowSpan;
    }

    public int getLandscapeRowSpan() {
        return colSpan;
    }

    @Override
    public boolean equals(Object obj) {
        return this.colSpan == ((GridMode)obj).colSpan && this.rowSpan == ((GridMode)obj).rowSpan;
    }
}
