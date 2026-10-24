package tn.naizo.moblootbags.common.drop;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import tn.naizo.moblootbags.common.MobLootBagsCommon;
import tn.naizo.moblootbags.common.bag.BagData;
import tn.naizo.moblootbags.common.config.BagsConfig;
import tn.naizo.moblootbags.common.config.DropsConfig;
import tn.naizo.moblootbags.common.config.LootBagsConfig;
import tn.naizo.moblootbags.common.content.BagType;
import tn.naizo.moblootbags.common.content.ModContent;
import tn.naizo.moblootbags.common.content.ModTags;
import tn.naizo.moblootbags.common.loot.LootService;

/** Decides when a mob drops a bag, which bag, and presents the drop. */
public final class DropEngine {
    public static final String CURSED_TAG = "mob_loot_bags.cursed";
    public static final String SPAWNER_TAG = "mob_loot_bags.spawner";
    public static final String TRIAL_SPAWNER_TAG = "mob_loot_bags.trial_spawner";

    private DropEngine() {
    }

    public static void onDeath(LivingEntity victim, DamageSource source) {
        if (!(victim.level() instanceof ServerLevel level) || victim instanceof Player) {
            return;
        }
        DropsConfig config = LootBagsConfig.drops();
        ServerPlayer killer = creditedPlayer(victim, source, config);
        if (killer != null && !config.allowFakePlayers && MobLootBagsCommon.platform().isFakePlayer(killer)) {
            killer = null;
        }

        if (victim.getTags().contains(CURSED_TAG)) {
            if (killer != null) {
                BagsConfig bags = LootBagsConfig.bags();
                BagType tier = BagType.tier(rollTierRank(level.getRandom(), config, luck(killer, victim, level, config) + 1.0, bags.ambushRewardMinRank));
                spawnBag(level, victim, createBag(level, tier), killer);
            }
            return;
        }
        if (!config.enabled || (config.requirePlayer && killer == null)) {
            return;
        }
        EntityType<?> type = victim.getType();
        if (ModTags.is(type, ModTags.NO_BAGS) || !passesFilter(type, config)) {
            return;
        }
        if (ModTags.is(type, ModTags.BOSSES)) {
            bossDrop(level, victim, killer, config);
            return;
        }

        double chance = chance(victim, killer, config);
        if (chance <= 0.0) {
            return;
        }
        RandomSource random = level.getRandom();
        PlayerLootData.Entry pity = killer != null ? PlayerLootData.get(killer.getUUID()) : null;
        boolean forced = false;
        if (pity != null && config.pityEnabled) {
            pity.killsSinceBag++;
            forced = config.pityKills > 0 && pity.killsSinceBag >= config.pityKills;
            PlayerLootData.markDirty();
        }
        if (!forced && random.nextDouble() >= chance) {
            return;
        }
        if (pity != null) {
            pity.killsSinceBag = 0;
        }

        BagType bag = rollSpecial(random, config);
        if (bag == null) {
            int minRank = 0;
            if (pity != null && config.pityEnabled) {
                if (config.pityLegendary > 0 && pity.bagsSinceLegendary >= config.pityLegendary) {
                    minRank = BagType.LEGENDARY.rank();
                } else if (config.pityRare > 0 && pity.bagsSinceRare >= config.pityRare) {
                    minRank = BagType.RARE.rank();
                }
            }
            bag = BagType.tier(rollTierRank(random, config, luck(killer, victim, level, config), minRank));
            if (pity != null) {
                pity.bagsSinceRare = bag.rank() >= BagType.RARE.rank() ? 0 : pity.bagsSinceRare + 1;
                pity.bagsSinceLegendary = bag.rank() >= BagType.LEGENDARY.rank() ? 0 : pity.bagsSinceLegendary + 1;
            }
        }
        spawnBag(level, victim, createBag(level, bag), killer);
    }

    private static void bossDrop(ServerLevel level, LivingEntity boss, @Nullable ServerPlayer killer, DropsConfig config) {
        double luck = luck(killer, boss, level, config);
        for (int i = 0; i < config.bossBags; i++) {
            BagType tier = level.getRandom().nextDouble() < config.bossMythicChance
                    ? BagType.MYTHIC
                    : BagType.tier(rollTierRank(level.getRandom(), config, luck, config.bossMinRank));
            spawnBag(level, boss, createBag(level, tier), killer);
        }
    }

    /** The player credited with a kill: the attacker, a projectile's shooter, or (optionally) a pet's owner. */
    public static @Nullable ServerPlayer creditedPlayer(LivingEntity victim, DamageSource source, DropsConfig config) {
        Entity attacker = source.getEntity();
        if (attacker instanceof ServerPlayer player) {
            return player;
        }
        if (config.creditPetKills && attacker instanceof OwnableEntity pet && pet.getOwner() instanceof ServerPlayer owner) {
            return owner;
        }
        if (attacker == null && victim.getKillCredit() instanceof ServerPlayer recent) {
            return recent; // knocked into lava, off a cliff, into a trap...
        }
        return null;
    }

    public static double chance(LivingEntity victim, @Nullable ServerPlayer killer, DropsConfig config) {
        EntityType<?> type = victim.getType();
        boolean hostile = victim instanceof Enemy || type.getCategory() == MobCategory.MONSTER;
        double chance = hostile ? config.hostileChance : config.passiveChance;
        chance *= mobMultiplier(type, config);
        if (config.healthScaling) {
            chance *= TierRoller.healthMultiplier(victim.getMaxHealth(), config.referenceHealth,
                    config.minHealthMultiplier, config.maxHealthMultiplier);
        }
        if (victim.getTags().contains(SPAWNER_TAG)) {
            chance *= config.spawnerMultiplier;
        } else if (victim.getTags().contains(TRIAL_SPAWNER_TAG)) {
            chance *= config.trialSpawnerMultiplier;
        }
        if (killer != null && hasTalisman(killer)) {
            chance *= config.talismanChanceMultiplier;
        }
        return Math.min(1.0, chance * config.globalMultiplier);
    }

    /** Bag Luck for a kill. */
    public static double luck(@Nullable ServerPlayer killer, @Nullable LivingEntity victim, ServerLevel level, DropsConfig config) {
        double luck = 0.0;
        if (killer != null) {
            luck += EnchantmentHelper.getMobLooting(killer) * config.luckPerLooting;
            luck += killer.getLuck() * config.luckAttribute;
            if (hasTalisman(killer)) {
                luck += config.luckTalisman;
            }
        }
        if (victim != null && config.healthScaling) {
            luck += TierRoller.healthLuck(victim.getMaxHealth(), config.referenceHealth, config.maxHealthLuck);
        }
        if (level.dimension() == Level.NETHER) {
            luck += config.luckNether;
        } else if (level.dimension() == Level.END) {
            luck += config.luckEnd;
        }
        if (level.getDifficulty() == Difficulty.HARD) {
            luck += config.luckHard;
        }
        return luck;
    }

    public static int rollTierRank(RandomSource random, DropsConfig config, double luck, int minRank) {
        return TierRoller.roll(config.tierWeights, luck, config.luckPerPoint, minRank, random.nextDouble());
    }

    private static @Nullable BagType rollSpecial(RandomSource random, DropsConfig config) {
        if (random.nextDouble() < config.cursedChance) {
            return BagType.CURSED;
        }
        if (random.nextDouble() < config.hourglassChance) {
            return BagType.HOURGLASS;
        }
        if (random.nextDouble() < config.lockedChance) {
            return BagType.LOCKED;
        }
        if (random.nextDouble() < config.summoningChance) {
            return BagType.SUMMONING;
        }
        return null;
    }

    public static ItemStack createBag(ServerLevel level, BagType type) {
        ItemStack stack = new ItemStack(ModContent.bag(type));
        String theme = LootService.themeFor(level, type);
        if (theme != null) {
            BagData.setTheme(stack, theme);
        }
        return stack;
    }

    public static void spawnBag(ServerLevel level, LivingEntity source, ItemStack bag, @Nullable ServerPlayer finder) {
        RandomSource random = level.getRandom();
        ItemEntity entity = new ItemEntity(level, source.getX(), source.getY() + 0.3, source.getZ(), bag,
                (random.nextDouble() - 0.5) * 0.15, 0.3, (random.nextDouble() - 0.5) * 0.15);
        entity.setDefaultPickUpDelay();
        level.addFreshEntity(entity);

        BagType type = BagType.byKey(BuiltInRegistries.ITEM.getKey(bag.getItem()).getPath());
        if (type == null) {
            return;
        }
        if (finder != null) {
            PlayerLootData.get(finder.getUUID()).found[type.ordinal()]++;
            PlayerLootData.markDirty();
            finder.awardStat(ModContent.STAT_BAGS_FOUND);
        }
        int rank = type.isTier() ? type.rank() : BagType.RARE.rank();
        DropsConfig config = LootBagsConfig.drops();
        if (rank >= config.dropSoundMinRank) {
            level.playSound(null, source.getX(), source.getY(), source.getZ(), ModContent.SOUND_BAG_DROP.get(), SoundSource.NEUTRAL,
                    1.0F, 0.8F + 0.12F * rank);
            level.sendParticles(new DustParticleOptions(type.dustColor(), 1.2F), source.getX(), source.getY() + 0.8, source.getZ(),
                    12 + 4 * rank, 0.3, 0.5, 0.3, 0.02);
        }
        if (type.rank() >= BagType.LEGENDARY.rank()) {
            level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, source.getX(), source.getY() + 1.0, source.getZ(), 30, 0.4, 0.6, 0.4, 0.3);
        }
        if (type == BagType.MYTHIC) {
            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
            if (bolt != null) {
                bolt.setVisualOnly(true);
                bolt.moveTo(source.getX(), source.getY(), source.getZ());
                level.addFreshEntity(bolt);
            }
        }
        if (finder != null && type.isTier() && type.rank() >= config.announceMinRank) {
            Component message = Component.translatable("message.mob_loot_bags.announce", finder.getDisplayName(), bag.getDisplayName(),
                    source.getDisplayName()).withStyle(ChatFormatting.GRAY);
            level.getServer().getPlayerList().broadcastSystemMessage(message, false);
        }
    }

    /**
     * Called from {@code MobMixin} when a mob finishes spawning. 1.20.1 has no trial spawners, so nothing sets
     * {@link #TRIAL_SPAWNER_TAG} here; the tag (and its multiplier) still works on mobs tagged by commands or datapacks.
     */
    public static void onFinalizeSpawn(Mob mob, ServerLevel level, MobSpawnType reason) {
        if (reason == MobSpawnType.SPAWNER) {
            mob.addTag(SPAWNER_TAG);
            return;
        }
        boolean natural = reason == MobSpawnType.NATURAL || reason == MobSpawnType.CHUNK_GENERATION
                || reason == MobSpawnType.STRUCTURE || reason == MobSpawnType.PATROL;
        DropsConfig config = LootBagsConfig.drops();
        if (!natural || !config.enabled || config.carrierChance <= 0.0 || !ModTags.is(mob.getType(), ModTags.BAG_CARRIERS)
                || !mob.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty() || level.getRandom().nextDouble() >= config.carrierChance) {
            return;
        }
        double luck = config.carrierLuck + luck(null, mob, level, config);
        BagType tier = BagType.tier(rollTierRank(level.getRandom(), config, luck, 0));
        mob.setItemSlot(EquipmentSlot.OFFHAND, createBag(level, tier));
        mob.setGuaranteedDrop(EquipmentSlot.OFFHAND);
    }

    private static double mobMultiplier(EntityType<?> type, DropsConfig config) {
        if (config.mobMultipliers.isEmpty()) {
            return 1.0;
        }
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        Double exact = config.mobMultipliers.get(id.toString());
        if (exact != null) {
            return Math.max(0.0, exact);
        }
        double multiplier = 1.0;
        for (var entry : config.mobMultipliers.entrySet()) {
            if (entry.getKey().startsWith("#") && matchesTag(type, entry.getKey())) {
                multiplier *= Math.max(0.0, entry.getValue());
            }
        }
        return multiplier;
    }

    private static boolean passesFilter(EntityType<?> type, DropsConfig config) {
        if (config.filterMode.equals("off") || config.filterMode.isEmpty()) {
            return true;
        }
        boolean listed = matchesAny(type, config.filterMobs);
        return config.filterMode.equals("whitelist") == listed;
    }

    public static boolean matchesAny(EntityType<?> type, List<String> entries) {
        String id = BuiltInRegistries.ENTITY_TYPE.getKey(type).toString();
        for (String entry : entries) {
            if (entry.startsWith("#") ? matchesTag(type, entry) : entry.equals(id)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesTag(EntityType<?> type, String entry) {
        ResourceLocation tag = ResourceLocation.tryParse(entry.substring(1));
        return tag != null && ModTags.is(type, TagKey.create(Registries.ENTITY_TYPE, tag));
    }

    public static boolean hasTalisman(Player player) {
        return player.getInventory().hasAnyMatching(stack -> stack.is(ModContent.FORTUNE_TALISMAN.get()));
    }
}
