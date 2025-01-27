package src.RapidCorners;

import dev.robocode.tankroyale.botapi.*;
import dev.robocode.tankroyale.botapi.events.GameStartedEvent;
import shared.FlameFishSharedUtil.*;

public class RapidCorners extends Bot {
    public enum Corner {
        UPPER_LEFT,
        UPPER_RIGHT,
        LOWER_LEFT,
        LOWER_RIGHT
    }

    public Translate[] corners;

    public Corner myCorner = Corner.LOWER_LEFT;

    public static void main(String[] args) {
        new RapidCorners().start();
    }

    RapidCorners() {
        super(BotInfo.fromFile("src/RapidCorners/RapidCorners.json"));
    }

    @Override
    public void run() {
        turnLeft(calcBearing(getAngleToCorner(myCorner)));
        forward(distanceTo(corners[myCorner.ordinal()].x, corners[myCorner.ordinal()].y));
    }

    @Override
    public void onGameStarted(GameStartedEvent e) {
        this.corners = new Translate[] {
            new Translate(0, getArenaHeight()),
            new Translate(getArenaWidth(), getArenaHeight()),
            new Translate(0, 0),
            new Translate(getArenaWidth(), 0)
        };
    }

    public double getAngleToCorner(Corner targetCorner) {
        return directionTo(corners[targetCorner.ordinal()].x, corners[targetCorner.ordinal()].y);
    }
}