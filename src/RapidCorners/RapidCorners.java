package src.RapidCorners;

import java.util.HashMap;

import dev.robocode.tankroyale.botapi.*;
import dev.robocode.tankroyale.botapi.events.GameStartedEvent;
import dev.robocode.tankroyale.botapi.events.ScannedBotEvent;
import dev.robocode.tankroyale.botapi.events.TickEvent;
import shared.FlameFishSharedUtil.*;

public class RapidCorners extends Bot {
    public double radarTurning = 60.0;
    public double gunTolerance = 5.0;

    public double gunTurnRate = 20.0;

    public Translate[] cornerPositions;

    public class Corner {
        public final Translate position;
        public final double targetAngle;

        public Corner(Translate position, double targetAngle) {
            this.position = position;
            this.targetAngle = targetAngle;
        }
    }

    public Corner[] corners;

    public Corner myCorner;

    public double radarMidPoint;

    public boolean targetting = false;

    public HashMap<Integer, DetectedRobot> detectedRobots = new HashMap<>();
    public Translate target;

    public static void main(String[] args) {
        new RapidCorners().start();
    }

    RapidCorners() {
        super(BotInfo.fromFile("src/RapidCorners/RapidCorners.json"));
    }

    @Override
    public void run() {
        this.detectedRobots = new HashMap<>();
        this.targetting = false;
        turnLeft(calcBearing(getAngleToCorner(myCorner)));
        forward(distanceTo(myCorner.position.x, myCorner.position.y));
        turnRadarLeft(calcRadarBearing(directionTo(getArenaWidth()/2, getArenaHeight()/2)));
        setAdjustRadarForBodyTurn(false);
        setAdjustRadarForGunTurn(false);
        this.radarMidPoint = getRadarDirection();

        setRadarTurnRate(45);
        this.targetting = true;
    }
    
    @Override
    public void onTick(TickEvent e) {
        if (targetting) {
            double delta = normalizeRelativeAngle(getRadarDirection() - radarMidPoint);
            
            if (delta >= radarTurning) {
                setRadarTurnRate(-45); // Reverse direction
            } else if (delta <= -radarTurning) {
                setRadarTurnRate(45); // Reverse direction
            }

            rapidFire();
        }

        WebVisualizer.updatePositions(detectedRobots, getTurnNumber());
    }

    public void rapidFire() {
        if (detectedRobots.isEmpty()) {
            return;
        }

        Translate closestBot = 
            detectedRobots.values().iterator().next().estimateCurrentPose(getTurnNumber()).getTranslate();
        for (DetectedRobot robot : detectedRobots.values()) {
            double distance = distanceTo(robot.estimateCurrentPose(getTurnNumber()).getX(), 
                robot.estimateCurrentPose(getTurnNumber()).getY());

            if (distance < distanceTo(closestBot.x, closestBot.y)) {
                closestBot = robot.estimateCurrentPose(getTurnNumber()).getTranslate();
            }
        }

        target = closestBot;

        double bearing = gunBearingTo(target.x, target.y);
        if (bearing > 0) {
            setGunTurnRate(Double.min(bearing, gunTurnRate));
        } else if (bearing < 0) {
            setGunTurnRate(Double.max(bearing, -gunTurnRate));
        } else {
            setGunTurnRate(0);
        }

        double error = Math.abs(gunBearingTo(target.x, target.y));

        if (error < gunTolerance) {
            fire(1);
        }
    }

    @Override
    public void onScannedBot(ScannedBotEvent e) {
        if (detectedRobots.containsKey(e.getScannedBotId())) {
            detectedRobots.get(e.getScannedBotId()).update(e);
        } else {
            detectedRobots.put(e.getScannedBotId(), new DetectedRobot(e));
        }
    }

    @Override
    public void onGameStarted(GameStartedEvent e) {
        this.cornerPositions = new Translate[] {
            new Translate(0, getArenaHeight()),
            new Translate(getArenaWidth(), getArenaHeight()),
            new Translate(0, 0),
            new Translate(getArenaWidth(), 0)
        };

        this.corners = new Corner[] {
            new Corner(cornerPositions[0], Math.atan2(
                cornerPositions[0].y - cornerPositions[3].y, 
                cornerPositions[0].x - cornerPositions[3].x)),
            new Corner(cornerPositions[1], Math.atan2(
                cornerPositions[1].y - cornerPositions[2].y, 
                cornerPositions[1].x - cornerPositions[2].x)),
            new Corner(cornerPositions[2], Math.atan2(
                cornerPositions[2].y - cornerPositions[1].y,
                cornerPositions[2].x - cornerPositions[1].x)),
            new Corner(cornerPositions[3], Math.atan2(
                cornerPositions[3].y - cornerPositions[0].y,
                cornerPositions[3].x - cornerPositions[0].x
            ))
        };

        myCorner = corners[0];

        WebVisualizer.initialize(getArenaWidth(), getArenaHeight());
    }

    public double getAngleToCorner(Corner targetCorner) {
        return directionTo(targetCorner.position.x, targetCorner.position.y);
    }
}