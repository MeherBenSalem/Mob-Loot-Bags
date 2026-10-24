package tn.naizo.moblootbags.common.drop;

/**
 * Pure tier math, kept free of Minecraft classes so it can be unit tested.
 *
 * <p>Each tier has a base weight. Bag Luck shifts the odds upward: tier {@code i}'s weight is multiplied by
 * {@code e^(perPoint * luck * i)}, so with the default {@code perPoint} of 0.35 one point of luck makes each tier about
 * 42% likelier relative to the tier below it. Tiers below {@code minRank} are never rolled.
 */
public final class TierRoller {
    private TierRoller() {
    }

    public static double[] distribution(double[] weights, double luck, double perPoint, int minRank) {
        double[] shifted = new double[weights.length];
        double total = 0.0D;
        for (int i = 0; i < weights.length; i++) {
            if (i < minRank) {
                continue;
            }
            double w = Math.max(0.0D, weights[i]) * Math.exp(perPoint * luck * i);
            shifted[i] = w;
            total += w;
        }
        if (total <= 0.0D) {
            // Every allowed tier has weight 0: fall back to the lowest allowed tier.
            int floor = Math.max(0, Math.min(weights.length - 1, minRank));
            shifted = new double[weights.length];
            shifted[floor] = 1.0D;
            return shifted;
        }
        for (int i = 0; i < shifted.length; i++) {
            shifted[i] /= total;
        }
        return shifted;
    }

    /** Picks a tier rank with {@code random01} in [0, 1). */
    public static int roll(double[] weights, double luck, double perPoint, int minRank, double random01) {
        double[] dist = distribution(weights, luck, perPoint, minRank);
        double acc = 0.0D;
        int last = 0;
        for (int i = 0; i < dist.length; i++) {
            if (dist[i] <= 0.0D) {
                continue;
            }
            last = i;
            acc += dist[i];
            if (random01 < acc) {
                return i;
            }
        }
        return last;
    }

    /** The drop-chance multiplier for a mob's max health: {@code health / reference}, clamped. */
    public static double healthMultiplier(double maxHealth, double reference, double min, double max) {
        if (reference <= 0.0D) {
            return 1.0D;
        }
        return clamp(maxHealth / reference, min, max);
    }

    /** Bag Luck from a mob's max health: one point per doubling over the reference, clamped to [0, max]. */
    public static double healthLuck(double maxHealth, double reference, double max) {
        if (reference <= 0.0D || maxHealth <= reference) {
            return 0.0D;
        }
        return clamp(Math.log(maxHealth / reference) / Math.log(2.0D), 0.0D, max);
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
