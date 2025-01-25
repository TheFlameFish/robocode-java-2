import dev.robocode.tankroyale.botapi.*;
import lib.FlameFishSharedUtil.*;

public class RapidCorners extends Bot {
    public enum Corner {
        UPPER_LEFT,
        UPPER_RIGHT,
        LOWER_LEFT,
        LOWER_RIGHT
    }

    public Translate[] corners = {
        new Translate(0, getArenaHeight()),
        new Translate(getArenaWidth(), getArenaHeight()),
        new Translate(0, 0),
        new Translate(getArenaWidth(), 0)
    };

    public static void main(String[] args) {
        new RapidCorners().start();
    }

    RapidCorners() {
        super(BotInfo.fromFile("RapidCorners.json"));
    }

    @Override
    public void run() {
        turnLeft(calcBearing(getAngleToCorner(Corner.LOWER_LEFT)));
    }

    public double getAngleToCorner(Corner targetCorner) {
        return directionTo(corners[targetCorner.ordinal()].x, corners[targetCorner.ordinal()].y);
    }
}