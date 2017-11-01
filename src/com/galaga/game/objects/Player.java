package com.galaga.game.objects;

import com.galaga.engine.GameContainer;
import com.galaga.engine.Renderer;
import com.galaga.game.GameObject;

import java.awt.event.KeyEvent;

public class Player extends GameObject {

    private static int speed = 60;

    public Player(int posX, int posY){
        this.tag = "player";
        this.posX = posX * 16;
        this.posY = posY * 16;
        this.width = 32;
        this.height = 32;
    }
    @Override
    public void update(GameContainer gc, float dt) {
        if (gc.getInput().isKeyDown(KeyEvent.VK_W)){
            posY -= dt * speed;
        }
        if (gc.getInput().isKeyDown(KeyEvent.VK_S)){
            posY += dt * speed;
        }
        if (gc.getInput().isKeyDown(KeyEvent.VK_A)){
            posX -= dt * speed;
        }
        if (gc.getInput().isKeyDown(KeyEvent.VK_D)){
            posX += dt * speed;
        }
    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        r.drawFillRect((int)getPosX(),(int)getPosY(),getWidth(),getHeight(),0xff00ff00);
    }
}
