package tn.naizo.moblootbags.common.bag;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.Heightmap;
import tn.naizo.moblootbags.common.MobLootBagsCommon;
import tn.naizo.moblootbags.common.config.BagsConfig;
import tn.naizo.moblootbags.common.config.LootBagsConfig;
import tn.naizo.moblootbags.common.content.BagType;
import tn.naizo.moblootbags.common.content.ModContent;
import tn.naizo.moblootbags.common.drop.DropEngine;
import tn.naizo.moblootbags.common.loot.LootService;

/** Cursed Loot Bags: an ambush to fight for bags, a jackpot, a hex or a chaos command. */
public final class Cursed {
    private Cursed() {
    }

    public static void open(ServerPlayer player, ServerLevel level) {
        BagsConfig config = LootBagsConfig.bags();
        RandomSource random = level.getRandom();
        playCurseSound(player, level, config);
        level.sendParticles(ParticleTypes.SOUL, player.getX(), player.getY() + 1.0, player.getZ(), 40, 0.6, 0.6, 0.6, 0.04);

        int total = config.cursedAmbush + config.cursedJackpot + config.cursedHex
                + (config.chaosCommands.isEmpty() ? 0 : config.cursedChaos);
        if (total <= 0) {
            jackpot(player, level, config);
            return;
        }
        int roll = random.nextInt(total);
        if ((roll -= config.cursedAmbush) < 0) {
            if (!ambush(player, level, config)) {
                hex(player, level, config);
            }
        } else if ((roll -= config.cursedJackpot) < 0) {
            jackpot(player, level, config);
        } else if ((roll -= config.cursedHex) < 0) {
            hex(player, level, config);
        } else {
            chaos(player, level, config);
        }
    }

    private static boolean ambush(ServerPlayer player, ServerLevel level, BagsConfig config) {
        if (level.getDifficulty() == Difficulty.PEACEFUL || config.ambushMobs.isEmpty()) {
            return false;
        }
        RandomSource random = level.getRandom();
        int count = Mth.clamp(config.ambushMin + level.getDifficulty().getId() - 1, config.ambushMin, config.ambushMax);
        int spawned = 0;
        for (int i = 0; i < count; i++) {
            Identifier id = Identifier.tryParse(config.ambushMobs.get(random.nextInt(config.ambushMobs.size())));
            EntityType<?> type = id == null ? null : BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null);
            if (type == null) {
                continue;
            }
            Entity entity = type.create(level, EntitySpawnReason.MOB_SUMMONED);
            if (!(entity instanceof Mob mob)) {
                continue;
            }
            double angle = (Math.PI * 2.0 * i) / count + random.nextDouble() * 0.6;
            double distance = 3.5 + random.nextDouble() * 2.0;
            int x = Mth.floor(player.getX() + Math.cos(angle) * distance);
            int z = Mth.floor(player.getZ() + Math.sin(angle) * distance);
            BlockPos pos = new BlockPos(x, player.getBlockY(), z);
            if (!level.getBlockState(pos).isAir() || !level.getBlockState(pos.above()).isAir()) {
                pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
            }
            mob.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, random.nextFloat() * 360.0F, 0.0F);
            mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), EntitySpawnReason.MOB_SUMMONED, null);
            mob.addTag(DropEngine.CURSED_TAG);
            mob.setCustomName(Component.translatable("entity.mob_loot_bags.cursed", mob.getType().getDescription())
                    .withColor(BagType.CURSED.color()));
            mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, 20 * 60, 0, false, false));
            mob.setTarget(player);
            level.addFreshEntityWithPassengers(mob);
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, mob.getX(), mob.getY() + 0.5, mob.getZ(), 20, 0.3, 0.6, 0.3, 0.02);
            spawned++;
        }
        if (spawned == 0) {
            return false;
        }
        visualLightning(level, player.getX(), player.getY(), player.getZ());
        player.sendSystemMessage(Component.translatable("message.mob_loot_bags.cursed.ambush", spawned).withColor(BagType.CURSED.color()));
        return true;
    }

    private static void jackpot(ServerPlayer player, ServerLevel level, BagsConfig config) {
        BagType tier = BagType.tier(config.jackpotRank);
        List<ItemStack> loot = BagOpening.merge(LootService.roll(level, player, player.position(), tier, null));
        BagOpening.deliver(player, loot);
        player.sendSystemMessage(Component.translatable("message.mob_loot_bags.cursed.jackpot",
                Component.translatable(tier.translationKey()).withColor(tier.color())).withColor(BagType.CURSED.color()));
        BagOpening.present(player, level, tier, 1, loot);
    }

    private static void hex(ServerPlayer player, ServerLevel level, BagsConfig config) {
        RandomSource random = level.getRandom();
        int effects = 1 + random.nextInt(2);
        for (int i = 0; i < effects && !config.hexEffects.isEmpty(); i++) {
            Identifier id = Identifier.tryParse(config.hexEffects.get(random.nextInt(config.hexEffects.size())));
            if (id == null) {
                continue;
            }
            BuiltInRegistries.MOB_EFFECT.get(id).ifPresent(effect -> {
                Holder<MobEffect> holder = effect;
                int seconds = holder.is(MobEffects.LEVITATION) ? Math.min(config.hexSeconds, 4) : config.hexSeconds;
                player.addEffect(new MobEffectInstance(holder, seconds * 20, 0));
            });
        }
        visualLightning(level, player.getX(), player.getY(), player.getZ());
        player.sendSystemMessage(Component.translatable("message.mob_loot_bags.cursed.hex").withStyle(ChatFormatting.DARK_PURPLE));
    }

    private static void chaos(ServerPlayer player, ServerLevel level, BagsConfig config) {
        String command = config.chaosCommands.get(level.getRandom().nextInt(config.chaosCommands.size()));
        try {
            level.getServer().getCommands().performPrefixedCommand(
                    player.createCommandSourceStack().withPermission(PermissionSet.ALL_PERMISSIONS).withSuppressedOutput(), command);
        } catch (RuntimeException e) {
            MobLootBagsCommon.LOG.warn("Cursed chaos command failed: {} ({})", command, e.toString());
        }
        player.sendSystemMessage(Component.translatable("message.mob_loot_bags.cursed.chaos").withStyle(ChatFormatting.DARK_PURPLE));
    }

    private static void playCurseSound(ServerPlayer player, ServerLevel level, BagsConfig config) {
        if (!config.cursedSound) {
            return;
        }
        SoundEvent sound = switch (level.getRandom().nextInt(4)) {
            case 0 -> ModContent.DRAMATIC.get();
            case 1 -> ModContent.LOOTBAG_SFX_1.get();
            case 2 -> ModContent.LOOTBAG_SFX_2.get();
            default -> ModContent.SOUND_CURSED.get();
        };
        level.playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private static void visualLightning(ServerLevel level, double x, double y, double z) {
        LightningBolt bolt = EntityTypes.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
        if (bolt != null) {
            bolt.setVisualOnly(true);
            bolt.snapTo(x, y, z);
            level.addFreshEntity(bolt);
        }
    }
}
