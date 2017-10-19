package com.galaga.game;

import com.galaga.engine.AbstractGame;
import com.galaga.engine.GameContainer;
import com.galaga.engine.Renderer;
import com.galaga.engine.gfx.Image;
import com.galaga.engine.gfx.ImageTile;

import java.awt.event.KeyEvent;

public class GameManager extends AbstractGame
{
    private Image image;

    private ImageTile imageTile;

    private float tempX = 0;

    private GameManager()
    {
        imageTile = new ImageTile("anim.png",64,64);
    }

    @Override
    public void update(GameContainer gc, float dt) {
        if (gc.getInput().isKeyDown(KeyEvent.VK_A))
        {
            System.out.println("A was pressed");
        }
        tempX += dt * 10;

        if (tempX > 3 ) {
            tempX = 0;
        }
    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        r.drawImageTile(imageTile,gc.getInput().getMouseX() - 32,gc.getInput().getMouseY() - 32,(int)tempX,0);
    }

    public static void main(String args[])
    {
        GameContainer gc = new GameContainer(new GameManager());
        gc.start();
    }
}
