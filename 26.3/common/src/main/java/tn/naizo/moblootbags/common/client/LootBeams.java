package tn.naizo.moblootbags.common.client;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import tn.naizo.moblootbags.common.config.ClientConfig;
import tn.naizo.moblootbags.common.config.LootBagsConfig;
import tn.naizo.moblootbags.common.content.BagType;
import tn.naizo.moblootbags.common.item.LootBagItem;

/** A column of tier-coloured light above dropped bags, so a Legendary drop is visible across a battlefield. */
public final class LootBeams {
    private LootBeams() {
    }

    public static void tick(ItemEntity entity) {
        Level level = entity.level();
        ClientConfig config = LootBagsConfig.client();
        if (!config.lootBeams || (!entity.onGround() && entity.getDeltaMovement().lengthSqr() > 0.01)) {
            return;
        }
        BagType type = LootBagItem.typeOf(entity.getItem());
        if (type == null) {
            return;
        }
        int rank = type.isTier() ? type.rank() : BagType.RARE.rank();
        if (rank < config.beamMinRank) {
            return;
        }
        RandomSource random = level.getRandom();
        DustParticleOptions dust = new DustParticleOptions(type.color(), 0.9F + 0.15F * rank);
        int count = 1 + rank;
        // The column starts just above the bag so the bag itself stays readable.
        for (int i = 0; i < count; i++) {
            double height = random.nextDouble() * config.beamHeight;
            level.addParticle(dust, entity.getX() + (random.nextDouble() - 0.5) * 0.08, entity.getY() + 0.65 + height,
                    entity.getZ() + (random.nextDouble() - 0.5) * 0.08, 0.0, 0.0, 0.0);
        }
        if (rank >= BagType.EPIC.rank() && random.nextInt(6 - Math.min(4, rank - 2)) == 0) {
            level.addParticle(ParticleTypes.END_ROD, entity.getX(), entity.getY() + 0.6, entity.getZ(),
                    (random.nextDouble() - 0.5) * 0.01, 0.03 + random.nextDouble() * 0.03, (random.nextDouble() - 0.5) * 0.01);
        }
    }
}
