package tn.naizo.moblootbags.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import tn.naizo.moblootbags.common.drop.TierRoller;

class TierRollerTest {
    private static final double[] WEIGHTS = {600, 260, 95, 35, 9, 1};

    @Test
    void distributionSumsToOne() {
        for (double luck : new double[] {0, 1, 2.5, 6}) {
            double sum = 0;
            for (double p : TierRoller.distribution(WEIGHTS, luck, 0.35, 0)) {
                sum += p;
            }
            assertEquals(1.0, sum, 1e-9);
        }
    }

    @Test
    void noLuckMatchesTheWeights() {
        double[] dist = TierRoller.distribution(WEIGHTS, 0, 0.35, 0);
        assertEquals(0.6, dist[0], 1e-9);
        assertEquals(0.001, dist[5], 1e-9);
    }

    @Test
    void luckShiftsOddsUpward() {
        double[] none = TierRoller.distribution(WEIGHTS, 0, 0.35, 0);
        double[] lucky = TierRoller.distribution(WEIGHTS, 2, 0.35, 0);
        assertTrue(lucky[0] < none[0]);
        for (int i = 2; i < WEIGHTS.length; i++) {
            assertTrue(lucky[i] > none[i], "tier " + i);
        }
    }

    @Test
    void minRankIsAFloor() {
        double[] dist = TierRoller.distribution(WEIGHTS, 0, 0.35, 4);
        assertEquals(0.0, dist[0] + dist[1] + dist[2] + dist[3], 1e-12);
        assertEquals(0.9, dist[4], 1e-9);
        for (double r = 0; r < 1; r += 0.01) {
            assertTrue(TierRoller.roll(WEIGHTS, 0, 0.35, 4, r) >= 4);
        }
    }

    @Test
    void allZeroWeightsFallBackToTheFloor() {
        double[] dist = TierRoller.distribution(new double[] {0, 0, 0, 0, 0, 0}, 1, 0.35, 2);
        assertEquals(1.0, dist[2], 1e-12);
        assertEquals(2, TierRoller.roll(new double[] {0, 0, 0, 0, 0, 0}, 1, 0.35, 2, 0.5));
    }

    @Test
    void rollCoversTheWholeRange() {
        assertEquals(0, TierRoller.roll(WEIGHTS, 0, 0.35, 0, 0.0));
        assertEquals(5, TierRoller.roll(WEIGHTS, 0, 0.35, 0, 0.99999));
    }

    @Test
    void healthScaling() {
        assertEquals(1.0, TierRoller.healthMultiplier(20, 20, 0.5, 2.5), 1e-9);
        assertEquals(0.5, TierRoller.healthMultiplier(4, 20, 0.5, 2.5), 1e-9);
        assertEquals(2.5, TierRoller.healthMultiplier(500, 20, 0.5, 2.5), 1e-9);
        assertEquals(0.0, TierRoller.healthLuck(20, 20, 2), 1e-9);
        assertEquals(1.0, TierRoller.healthLuck(40, 20, 2), 1e-9);
        assertEquals(2.0, TierRoller.healthLuck(1000, 20, 2), 1e-9);
    }
}
