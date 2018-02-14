package com.galaga.game.objects;

import com.galaga.engine.GameContainer;
import com.galaga.engine.Renderer;
import com.galaga.game.GameManager;
import com.galaga.game.GameObject;

public class Bullet extends GameObject {

    private int direction;
    private float speed = 200;

    private int tileX,tileY;
    private float offX,offY;

    public Bullet(int tileX, int tileY,float offX,float offY,int direction){
        this.direction = direction;
        this.posX = tileX * GameManager.TS + offX;
        this.posY = tileY * GameManager.TS + offY;
        this.tileX = tileX ;
        this.tileY = tileY ;
        this.offX = offX;
        this.offY = offY;
    }

    @Override
    public void update(GameContainer gc, GameManager gm, float dt) {

        switch(direction){
            case 0: offY -= speed * dt; break;
            case 1: offX += speed * dt; break;
            case 2: offY += speed * dt; break;
            case 3: offX -= speed * dt; break;
        }

        //final position
        if (offY > GameManager.TS){
            tileY++;
            offY -= GameManager.TS;
        }
        if (offY < 0){
            tileY--;
            offY += GameManager.TS;
        }
        if (offX > GameManager.TS){
            tileX++;
            offX -= GameManager.TS;
        }
        if (offX < 0){
            tileX--;
            offX += GameManager.TS;
        }

        if (gm.getCollision(tileX,tileY)){
            this.active = false;
        }

        posX = tileX * GameManager.TS + offX;
        posY = tileY * GameManager.TS + offY;
    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        r.drawFillRect((int)posX - 2,(int)posY - 2,4,4,0xffff0000);
    }
}
