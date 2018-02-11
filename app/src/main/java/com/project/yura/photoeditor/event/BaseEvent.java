package com.project.yura.photoeditor.event;

public abstract class BaseEvent<D> {

    private D data;

    public BaseEvent() {
    }

    public BaseEvent(D data) {
        this.data = data;
    }

    public D getData() {
        return data;
    }
}

