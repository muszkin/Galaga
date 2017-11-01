package com.galaga.game;

import com.galaga.engine.AbstractGame;
import com.galaga.engine.GameContainer;
import com.galaga.engine.Renderer;
import com.galaga.engine.audio.SoundClip;
import com.galaga.engine.gfx.Image;
import com.galaga.engine.gfx.ImageTile;
import com.galaga.engine.gfx.Light;
import com.galaga.game.objects.Player;

import java.util.ArrayList;

public class GameManager extends AbstractGame
{
    private SoundClip clip;

    private ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();

    private GameManager()
    {
        clip = new SoundClip("/Star_Trooper.wav");
        clip.loop();
        gameObjects.add(new Player(2,2));
    }

    @Override
    public void init(GameContainer gc) {
        gc.getRenderer().setAmbientColor(-1);
    }

    @Override
    public void update(GameContainer gc, float dt) {
        for (int i = 0; i < gameObjects.size(); i++){
            gameObjects.get(i).update(gc,dt);
            if (!gameObjects.get(i).isActive()){
                gameObjects.remove(i);
                i--;
            }
        }
    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        for (GameObject gameObject: gameObjects){
            gameObject.render(gc,r);
        }
    }

    public static void main(String args[])
    {
        GameContainer gc = new GameContainer(new GameManager());
        gc.start();


    }
}
