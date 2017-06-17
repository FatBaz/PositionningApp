package fr.utbm.lo53.p2017.positionningapp;

/**
 * Created by Valentin on 14/06/2017.
 */

public class TranslateCoordinate {
    private int mapWidth, mapHeight;
    public class Point {
        float x,y;
    }
    public Point PointToGeo(float xA, float yA)
    {
        Point p = new Point();
        p.x = (xA/mapWidth)*100;
        p.y = (yA/mapWidth)*100;
        return p;
    }
}