package com.project.yura.photoeditor.event.ui;

import com.project.yura.photoeditor.event.BaseEvent;
import com.project.yura.photoeditor.processing.model.PreviewData;

public class FilterSelectedEvent extends BaseEvent<PreviewData> {
    public FilterSelectedEvent(PreviewData data) {
        super(data);
    }
}
