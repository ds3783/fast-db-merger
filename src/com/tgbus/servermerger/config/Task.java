package com.tgbus.servermerger.config;

/**
 * Created by IntelliJ IDEA.
 * User: liuhy
 * Date: 2010-9-20
 * Time: 20:18:21
 * To change this template use File | Settings | File Templates.
 */
public class Task {
    String type;
    String side;
    String text;
    String name;

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Task(String type, String name, String side, String text) {
        this.type = type;
        this.side = side;
        this.text = text;
        this.name = name;
    }
}
