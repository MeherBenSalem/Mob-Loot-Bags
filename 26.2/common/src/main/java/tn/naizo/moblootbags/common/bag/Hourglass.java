package tn.naizo.moblootbags.common.bag;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import tn.naizo.moblootbags.common.config.BagsConfig;
import tn.naizo.moblootbags.common.config.LootBagsConfig;
import tn.naizo.moblootbags.common.content.BagType;
import tn.naizo.moblootbags.common.content.ModContent;

/** Hourglass Loot Bags ripen after a random time, shown as an item bar, then glint and open into better bags. */
public final class Hourglass {
    private Hourglass() {
    }

    public static void tick(ItemStack stack, ServerLevel level, Entity owner) {
        long now = level.getGameTime();
        BagData.migrateLegacyTimer(stack, now);
        if (BagData.ripeAt(stack) < 0) {
            BagsConfig config = LootBagsConfig.bags();
            double minutes = config.hourglassMinMinutes
                    + level.getRandom().nextDouble() * (config.hourglassMaxMinutes - config.hourglassMinMinutes);
            BagData.startRipening(stack, now, Math.round(minutes * 60.0 * 20.0));
            return;
        }
        if (BagData.isRipe(stack, now) && !BagData.notified(stack)) {
            BagData.setNotified(stack);
            if (owner instanceof ServerPlayer player) {
                player.sendOverlayMessage(Component.translatable("message.mob_loot_bags.hourglass_ripe")
                        .withColor(BagType.HOURGLASS.color()));
                level.playSound(null, player.getX(), player.getY(), player.getZ(), ModContent.SOUND_HOURGLASS.get(),
                        SoundSource.PLAYERS, 0.8F, 1.0F);
            }
        }
    }

    /** 0 when the timer starts, 1 when ripe. */
    public static float progress(ItemStack stack, long now) {
        long ripeAt = BagData.ripeAt(stack);
        long total = BagData.ripenTicks(stack);
        if (ripeAt < 0) {
            return 0.0F;
        }
        if (total <= 0) {
            return now >= ripeAt ? 1.0F : 0.0F;
        }
        return Math.max(0.0F, Math.min(1.0F, 1.0F - (ripeAt - now) / (float) total));
    }

    /** "4m 05s" style remaining time. */
    public static String formatRemaining(long ticks) {
        long seconds = Math.max(0, (ticks + 19) / 20);
        long minutes = seconds / 60;
        return minutes > 0 ? String.format("%dm %02ds", minutes, seconds % 60) : seconds + "s";
    }
}
