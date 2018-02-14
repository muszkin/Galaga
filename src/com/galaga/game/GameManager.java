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
    public static final int TS = 16;
    private SoundClip clip;

    private ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();

    private boolean[] collision;
    private int levelW,levelH;

    private GameManager()
    {
        clip = new SoundClip("/Star_Trooper.wav");
        clip.loop();
        gameObjects.add(new Player(8,4));
        loadLevel("/level.png");
    }

    @Override
    public void init(GameContainer gc) {
        gc.getRenderer().setAmbientColor(-1);
    }

    @Override
    public void update(GameContainer gc, float dt) {
        for (int i = 0; i < gameObjects.size(); i++){
            gameObjects.get(i).update(gc,this,dt);
            if (!gameObjects.get(i).isActive()){
                gameObjects.remove(i);
                i--;
            }
        }
    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        for (int y = 0; y < levelH; y++) {
            for (int x = 0; x < levelW; x++) {
                if (collision[x + y * levelW]) {
                    r.drawFillRect(x * TS, y * TS, 16, 16, 0xff0f0f0f);
                }else{
                    r.drawFillRect(x * TS, y * TS, 16, 16, 0xfff9f9f9);
                }
            }
        }

        for (GameObject gameObject: gameObjects){
            gameObject.render(gc,r);
        }
    }

    public void loadLevel(String path){
        Image levelImage = new Image(path);

        levelW = levelImage.getW();
        levelH = levelImage.getH();

        collision = new boolean[levelW * levelH];

        for (int y = 0; y < levelH; y++){
            for (int x = 0; x < levelW; x++){
                if (levelImage.getP()[x + y * levelImage.getW()] == 0xff000000){
                    collision[x + y * levelImage.getW()] = true;
                }else{
                    collision[x + y * levelImage.getW()] = false;
                }
            }
        }
    }

    public void addObject(GameObject gameObject){
        gameObjects.add(gameObject);
    }

    public boolean getCollision(int x, int y)
    {
        if ( x < 0 || x >= levelW || y < 0 || y >= levelH){
            return true;
        }
        return collision[x + y * levelW];
    }

    public static void main(String args[])
    {
        GameContainer gc = new GameContainer(new GameManager());
        gc.start();


    }
}
