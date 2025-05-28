package com.simon.simontree.api;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class TreeElement {
    protected static AtomicInteger id = new AtomicInteger(0);
    protected String name;
    protected int priorityValue;
    protected long cost;
    protected int level;
}
