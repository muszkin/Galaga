package com.galaga.game;

import com.galaga.engine.GameContainer;
import com.galaga.engine.Renderer;

public abstract class GameObject {

    protected String tag;
    protected float posX,posY;
    protected int width,height;
    protected boolean active = true;

    public abstract void update(GameContainer gc,float dt);
    public abstract void render(GameContainer gc, Renderer r);

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
