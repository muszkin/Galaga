package com.galaga.game;

import com.galaga.engine.AbstractGame;
import com.galaga.engine.GameContainer;
import com.galaga.engine.Renderer;
import com.galaga.engine.audio.SoundClip;

public class GameManager extends AbstractGame
{
    private SoundClip clip;

    private GameManager()
    {
        clip = new SoundClip("/bass.wav");
    }

    @Override
    public void update(GameContainer gc, float dt) {
        if (gc.getInput().isButtonDown(1)){
            clip.play();
        }
        float volume = clip.getGainControl().getValue() + gc.getInput().getScroll();
        if (volume > clip.getGainControl().getMaximum()){
            volume = clip.getGainControl().getMaximum();
        }else if (volume < clip.getGainControl().getMinimum()){
            volume = clip.getGainControl().getMinimum();
        }
        clip.setVoulme(volume);

    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        r.drawText("FPS:" +  gc.getFps(),  0,0,0xff00ffff);
        r.drawText("Volume:" +  clip.getGainControl().getValue(),  0,20,0xff00ffff);
        r.drawFillRect(gc.getInput().getMouseX() - 120 ,gc.getInput().getMouseY() - 120 ,240,240,0xffffccff);
    }

    public static void main(String args[])
    {
        GameContainer gc = new GameContainer(new GameManager());
        gc.start();
    }
}
