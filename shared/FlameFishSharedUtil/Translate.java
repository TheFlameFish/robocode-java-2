package shared.FlameFishSharedUtil;

/**
 * The {@code Translate} class represents a translation in a 2D coordinate system.
 * It provides methods to perform vector addition and subtraction on translation vectors.
 * <p>
 * Instances of this class are immutable.
 * </p>
 */
public class Translate {
    public final double x;
    public final double y;

    public Translate() {
        this.x = 0;
        this.y = 0;
    }

    public Translate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /** @return a {@link Translate} representing the sum of translateA and translateB */
    public static Translate sum(Translate translateA, Translate translateB) {
        return new Translate(translateA.x + translateB.x, translateA.y + translateB.y);
    }

    /** @return a {@link Translate} representing the difference between translateA and translateB */
    public static Translate difference(Translate translateA, Translate translateB) {
        return new Translate(translateA.x - translateB.x, translateA.y - translateB.y);
    }

    public static Translate multiply(Translate translate, double factor) {
        return new Translate(translate.x/factor, translate.y/factor);
    }
}
