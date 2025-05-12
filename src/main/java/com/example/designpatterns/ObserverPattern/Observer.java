package com.example.designpatterns.ObserverPattern;

import com.example.util.ParsedData;

public interface Observer {
    void update(ParsedData data);
}
