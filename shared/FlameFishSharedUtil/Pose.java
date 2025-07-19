package shared.FlameFishSharedUtil;

public class Pose {
    public final Translate translate;
    public final double rotation;

    public Pose() {
        this.translate = new Translate();
        this.rotation = 0.0d;
    }

    public Pose(Translate translate, double rotation) {
        this.translate = translate;
        this.rotation = rotation;
    }

    public Pose(double x, double y, double rotation) {
        this.translate = new Translate(x, y);
        this.rotation = rotation;
    }

    public Pose(double x, double y) {
        this.translate = new Translate(x, y);
        this.rotation = 0.0d;
    }

    public Pose(Translate translate) {
        this.translate = translate;
        this.rotation = 0.0d;
    }

    public double getX() {
        return translate.x;
    }

    public double getY() {
        return translate.y;
    }

    public Translate getTranslate() {
        return translate;
    }

    public double getRotation() {
        return rotation;
    }

    public static Pose sum(Pose poseA, Pose poseB) {
        return new Pose(Translate.sum(poseA.getTranslate(), poseB.getTranslate()),
            poseA.getRotation() + poseB.getRotation());
    }

    public static Pose difference(Pose poseA, Pose poseB) {
        return new Pose(Translate.difference(poseA.getTranslate(), poseB.getTranslate()), 
            poseA.getRotation() - poseB.getRotation());
    }
}