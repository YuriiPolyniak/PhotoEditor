package com.project.yura.photoeditor.event.ui;

import com.project.yura.photoeditor.event.BaseEvent;
import com.project.yura.photoeditor.processing.model.PreviewData;

public class DeleteFilterEvent extends BaseEvent<PreviewData> {
    public DeleteFilterEvent(PreviewData data) {
        super(data);
    }
}
