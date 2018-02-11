package com.project.yura.photoeditor.event;

import com.squareup.otto.Bus;

import java.util.HashMap;
import java.util.Map;

public class MainThreadBus extends Bus {

    private Map<Class, Object> registeredTypes = new HashMap<>();

    private static MainThreadBus instance;

    private MainThreadBus() {
    }

    public static MainThreadBus getInstance() {
        if (instance == null) {
            instance = new MainThreadBus();
        }
        return instance;
    }

    @Override
    public void register(Object object) {
        if (registeredTypes.containsKey(object.getClass())) {
            super.unregister(registeredTypes.get(object.getClass()));
        }

        registeredTypes.put(object.getClass(), object);
        super.register(object);
    }

    @Override
    public void unregister(Object object) {
        if (registeredTypes.containsKey(object.getClass()) && registeredTypes.get(object.getClass()) == object) {

            registeredTypes.remove(object.getClass());
            super.unregister(object);
        }
    }
}