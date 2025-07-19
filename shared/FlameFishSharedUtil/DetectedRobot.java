package shared.FlameFishSharedUtil;

import dev.robocode.tankroyale.botapi.events.ScannedBotEvent;

public class DetectedRobot {
    public Pose pose;
    /** Velocity measured in units per turn */
    public Translate velocity;
    /** The last turn at which the bot was scanned. */
    public Integer lastScanned;

    public double energy;

    public DetectedRobot(ScannedBotEvent e) {
        this.pose = new Pose(e.getX(), e.getY(), e.getDirection());
        this.lastScanned = e.getTurnNumber();
        this.velocity = new Translate();
    }

    public void update(ScannedBotEvent e) {
        Pose newPose = new Pose(e.getX(), e.getY(), e.getDirection());
        Integer turn = e.getTurnNumber();

        this.velocity = Translate.multiply(
            Translate.difference(pose.getTranslate(), newPose.getTranslate()),
            1/(turn - lastScanned)
        );
        this.pose = newPose;
        this.lastScanned = turn;
        this.energy = e.getEnergy();
    }

    public Pose getPose() {
        return this.pose;
    }

    public Translate getTranslate() {
        return this.pose.getTranslate();
    }

    public Pose estimateCurrentPose(int turn) {
        int deltaTime = turn - this.lastScanned;
        if (deltaTime <= 0) {
            return this.pose; // Just in case
        } else if (deltaTime >= 10) {
            return new Pose(Double.MAX_VALUE, Double.MAX_VALUE); // Discard this robot. This info probably isn't useful anymore.
        }

        // if (true) {
        //     return this.pose; // Velocity estimation no worky =(
        // }
        
        Translate displacement = Translate.multiply(this.velocity, deltaTime);
        return new Pose(
            this.pose.getX() + displacement.x,
            this.pose.getY() + displacement.y,
            this.pose.getRotation()
        );
    }
}
