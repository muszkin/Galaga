package com.galaga.engine;

import com.galaga.engine.gfx.*;

import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Renderer
{
    private Font font = Font.STANDARD;
    private ArrayList<ImageRequest> imageRequests = new ArrayList<ImageRequest>();
    private ArrayList<LightRequest> lightRequests = new ArrayList<LightRequest>();

    private int pW, pH;
    private int[] p;
    private int[] zBuffer;
    private int[] lightMap;
    private int[] lightBlock;

    private int zDepth = 0;
    private int ambientColor = 0xff6b6b6b;
    private boolean processing = false;


    public Renderer(GameContainer gc)
    {
        pW = gc.getWidth();
        pH = gc.getHeight();
        p = ((DataBufferInt)gc.getWindow().getImage().getRaster().getDataBuffer()).getData();
        zBuffer = new int[p.length];
        lightMap = new int[p.length];
        lightBlock = new int[p.length];
    }

    public void clear()
    {
        for (int i = 0; i < p.length; i++)
        {
            p[i] = 0;
            zBuffer[i] = 0;
            lightMap[i] = ambientColor;
            lightBlock[i] = 0;
        }
    }

    public void process()
    {
        processing = true;

        Collections.sort(imageRequests, new Comparator<ImageRequest>() {
            @Override
            public int compare(ImageRequest o1, ImageRequest o2) {
                if (o1.zDepth < o2.zDepth){
                    return -1;
                }else if (o1.zDepth > o2.zDepth){
                    return 1;
                }else{
                    return 0;
                }
            }
        });

        for (int i = 0; i < imageRequests.size(); i++){
            ImageRequest ir = imageRequests.get(i);
            setzDepth(ir.zDepth);
            drawImage(ir.image,ir.offX,ir.offY);
        }

        for (int i = 0; i < lightRequests.size(); i++){
            LightRequest lightRequest = lightRequests.get(i);
            this.drawLightRequest(lightRequest.light,lightRequest.locX,lightRequest.locY);
        }

        for (int i=0;i < p.length;i++){
            float r = ((lightMap[i] >> 16 ) & 0xff)/255f;
            float g = ((lightMap[i] >> 8 ) & 0xff)/255f;
            float b = (lightMap[i] & 0xff)/255f;

            p[i] = ((int)(((p[i] >> 16) & 0xff) * r) << 16 |(int)(((p[i] >> 8) & 0xff) * g) << 8|(int)((p[i] & 0xff) * b));
        }

        imageRequests.clear();
        lightRequests.clear();
        processing = false;
    }

    public void setLightMap(int x, int y, int value){

        if (x < 0 || x >= pW || y < 0 || y >= pH ){
            return;
        }

        int baseColor = lightMap[x + y * pW];

        int maxRed = Math.max((baseColor >> 16) & 0xff,(value >> 16) & 0xff);
        int maxGreen = Math.max((baseColor >> 8) & 0xff,(value >> 8) & 0xff);
        int maxBlue = Math.max(baseColor & 0xff,value & 0xff);

        lightMap[x + y * pW] = ( maxRed << 16 | maxGreen << 8 | maxBlue);
    }

    public void setLightBlock(int x, int y, int value){

        if (x < 0 || x >= pW || y < 0 || y >= pH ){
            return;
        }

        if (zBuffer[x + y * pW] > zDepth){
            return;
        }

        lightBlock[x + y * pW] = value;
    }

    public void setPixel(int x ,int y ,int value)
    {
        int alpha = (value >> 24) & 0xff;

        if (x < 0 || x >= pW || y < 0 || y >= pH || alpha == 0){
            return;
        }

        int index = x + y * pW;

        if (zBuffer[index] > zDepth){
            return;
        }
        zBuffer[index] = zDepth;

        if (alpha == 255) {
            p[index] = value;
        }else{
            int pixelColor = p[x + y * pW];
            int newRed = ((pixelColor >> 16) & 0xff) - (int)((((pixelColor >> 16) & 0xff) - ((value >> 16) & 0xff)) * (alpha / 255f));
            int newGreen = ((pixelColor >> 8) & 0xff) - (int)((((pixelColor >> 8) & 0xff) - ((value >> 8) & 0xff)) * (alpha / 255f));
            int newBlue = (pixelColor & 0xff) - (int)(((pixelColor & 0xff) - (value & 0xff)) * (alpha / 255f));
            p[index] = ( 255 << 24 | newRed << 16 | newGreen << 8 | newBlue);
        }
    }

    public void drawImage(Image image, int offX, int offY)
    {
        if (image.isAlpha() && !processing){
            imageRequests.add(new ImageRequest(image,zDepth,offX,offY));
            return;
        }
        if (offX < -image.getW() ) return;
        if (offY < -image.getH() ) return;
        if (offX >= pW) return;
        if (offY >= pH) return;

        int newX = 0;
        int newY = 0;
        int newWidth = image.getW();
        int newHeight = image.getH();



        if (offX  < 0){ newX -= offX; }
        if (offY < 0){ newY -= offY; }
        if (newWidth + offX > pW){ newWidth -= (newWidth + offX - pW); }
        if (newHeight + offY > pH){ newHeight -= (newHeight + offY - pH); }

        for (int y = newY; y < newHeight; y++){
            for (int x = newX; x < newWidth; x++){
                setPixel(x + offX,y + offY, image.getP()[x + y * image.getW()]);
                setLightBlock(x + offX,y + offY,image.getLightBlock());
            }
        }
    }

    public void drawImageTile(ImageTile image, int offX, int offY, int tileX, int tileY)
    {
        if (image.isAlpha() && !processing){
            imageRequests.add(new ImageRequest(image.getTileImage(tileX,tileY),zDepth,offX,offY));
            return;
        }

        if (offX < -image.getTileW() ) return;
        if (offY < -image.getTileH() ) return;
        if (offX >= pW) return;
        if (offY >= pH) return;

        int newX = 0;
        int newY = 0;
        int newWidth = image.getTileW();
        int newHeight = image.getTileH();



        if (offX  < 0){ newX -= offX; }
        if (offY < 0){ newY -= offY; }
        if (newWidth + offX > pW){ newWidth -= (newWidth + offX - pW); }
        if (newHeight + offY > pH){ newHeight -= (newHeight + offY - pH); }

        for (int y = newY; y < newHeight; y++){
            for (int x = newX; x < newWidth; x++){
                setPixel(x + offX,y + offY, image.getP()[(x + tileX * image.getTileW()) + (y + tileY * image.getTileH()) * image.getW()]);
                setLightBlock(x + offX,y + offY,image.getLightBlock());
            }
        }
    }

    public void drawText(String text, int offX, int offY, int color){
        int offset = 0;

        for (int i= 0; i < text.length(); i++){
            int unicode = text.codePointAt(i);

            for(int y = 0; y < font.getFontImage().getH();y++){
                for (int x = 0; x < font.getWidths()[unicode];x++){
                    if (font.getFontImage().getP()[(x + font.getOffsets()[unicode]) + y * font.getFontImage().getW()] == 0xffffffff){
                        setPixel(x + offX + offset,y + offY,color);
                    }
                }
            }

            offset += font.getWidths()[unicode];
        }
    }

    public void drawRect(int offX, int offY, int width, int height,int color) {
        for (int y = 0; y <= height; y++) {
            setPixel(offX, y + offY, color);
            setLightBlock(offX, y + offY, color);
            setPixel(offX + width, y + offY, color);
            setLightBlock(offX + width, y + offY, color);
        }
        for (int x = 0; x <= width; x++) {
            setPixel(x + offX, offY, color);
            setLightBlock(x + offX, offY, color);
            setPixel(x + offX, offY + height, color);
            setLightBlock(x + offX, offY + height, color);

        }
    }

    public void drawFillRect(int offX, int offY, int width, int height,int color) {
        if (offX < -width ) return;
        if (offY < -height ) return;
        if (offX >= pW) return;
        if (offY >= pH) return;

        int newX = 0;
        int newY = 0;
        int newWidth = width;
        int newHeight = height;

        if (offX  < 0){ newX -= offX; }
        if (offY < 0){ newY -= offY; }
        if (newWidth + offX > pW){ newWidth -= (newWidth + offX - pW); }
        if (newHeight + offY > pH){ newHeight -= (newHeight + offY - pH); }

        for (int y = newY; y < newHeight; y++) {
            for (int x = newX; x < newWidth; x++) {
                setPixel(x + offX, y + offY, color);
                setLightBlock(x + offX,y + offY,color);
            }
        }

    }

    public void drawLight(Light light, int offX, int offY){
        lightRequests.add(new LightRequest(light,offX,offY));
    }

    public void drawLightRequest(Light light, int offX, int offY){
        for (int i = 0;i < light.getDiameter();i++){
            drawLightLine(light,light.getRadius(),light.getRadius(),i,0,offX,offY);
            drawLightLine(light,light.getRadius(),light.getRadius(),i,light.getDiameter(),offX,offY);
            drawLightLine(light,light.getRadius(),light.getRadius(),0,i,offX,offY);
            drawLightLine(light,light.getRadius(),light.getRadius(),light.getDiameter(),i,offX,offY);
        }
    }

    private void drawLightLine(Light light,int x0,int y0,int x1,int y1,int offX,int offY){
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;

        int err1 = dx - dy;
        int err2;

        while(true){
            int screenX = x0 - light.getRadius() + offX;
            int screenY = y0 - light.getRadius() + offY;

            if (screenX < 0 || screenX >= pW || screenY < 0 || screenY >= pH){
                return;
            }

            int lightColor = light.getLightValue(x0,y0);
            if (lightColor == 0){
                return;
            }

            if (lightBlock[screenX + screenY * pW] == Light.FULL){
                return;
            }

            setLightMap(screenX,screenY,lightColor);


            if (x0 == x1 && y0 == y1)
                break;

            err2 = 2 * err1;

            if (err2 > -1 * dy){
                err1 -= dy;
                x0 += sx;
            }

            if (err2 < dx){
                err1 += dx;
                y0 += sy;
            }
        }
    }


    public int getzDepth() {
        return zDepth;
    }

    public void setzDepth(int zDepth) {
        this.zDepth = zDepth;
    }

    public int getAmbientColor() {
        return ambientColor;
    }

    public void setAmbientColor(int ambientColor) {
        this.ambientColor = ambientColor;
    }
}
