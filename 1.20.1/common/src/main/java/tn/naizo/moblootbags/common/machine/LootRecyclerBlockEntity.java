package tn.naizo.moblootbags.common.machine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import tn.naizo.moblootbags.common.bag.BagData;
import tn.naizo.moblootbags.common.config.LootBagsConfig;
import tn.naizo.moblootbags.common.config.MachinesConfig;
import tn.naizo.moblootbags.common.content.BagType;
import tn.naizo.moblootbags.common.content.ModContent;
import tn.naizo.moblootbags.common.item.LootBagItem;

/**
 * Grinds mob-farm junk into salvage points and turns every {@code points_per_bag} points into a loot bag. Loot bags fed
 * to it become stored experience, collected from the menu or by sneak-using the block.
 */
public class LootRecyclerBlockEntity extends MachineBlockEntity {
    public static final int INPUTS = 9;
    public static final int OUTPUTS = 3;
    private static final String LAYOUT_KEY = "mlb_layout";
    private static final String LEGACY_XP_KEY = "lootbags_blockxp";

    private int points;
    private int storedXp;
    private int progress;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> points;
                case 1 -> LootBagsConfig.machines().pointsPerBag;
                case 2 -> storedXp & 0x7FFF;
                case 3 -> (storedXp >> 15) & 0x7FFF;
                case 4 -> progress;
                case 5 -> LootBagsConfig.machines().recyclerTicksPerItem;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 6;
        }
    };

    public LootRecyclerBlockEntity(BlockPos pos, BlockState state) {
        super(ModContent.LOOT_RECYCLER_ENTITY.get(), pos, state, INPUTS, OUTPUTS);
    }

    @Override
    public boolean acceptsInput(ItemStack stack) {
        return LootBagItem.typeOf(stack) != null || junkValue(stack) > 0;
    }

    /** Experience for one bag; a ripe Hourglass bag (or a 1.x one marked ripe with an enchantment) is worth more. */
    public static int bagXp(ItemStack stack, long now) {
        BagType type = LootBagItem.typeOf(stack);
        if (type == null) {
            return 0;
        }
        MachinesConfig config = LootBagsConfig.machines();
        if (type == BagType.HOURGLASS && (stack.isEnchanted() || BagData.isRipe(stack, now))) {
            return config.ripeHourglassXp;
        }
        return config.bagXp.getOrDefault(type, type.defaultXp());
    }

    /** Salvage points for one item: an exact id wins over tags. */
    public static int junkValue(ItemStack stack) {
        if (stack.isEmpty() || LootBagItem.typeOf(stack) != null) {
            return 0;
        }
        Map<String, Double> junk = LootBagsConfig.machines().junk;
        Double exact = junk.get(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
        if (exact != null) {
            return (int) Math.max(0, Math.round(exact));
        }
        for (Map.Entry<String, Double> entry : junk.entrySet()) {
            if (entry.getKey().startsWith("#")) {
                ResourceLocation tag = ResourceLocation.tryParse(entry.getKey().substring(1));
                if (tag != null && stack.is(TagKey.create(Registries.ITEM, tag))) {
                    return (int) Math.max(0, Math.round(entry.getValue()));
                }
            }
        }
        return 0;
    }

    @Override
    public void serverTick(ServerLevel level, BlockPos pos, BlockState state) {
        MachinesConfig config = LootBagsConfig.machines();
        produceBags();
        int slot = nextInput(config, level.getGameTime());
        if (slot < 0) {
            progress = 0;
            setActive(level, pos, state, false);
            return;
        }
        setActive(level, pos, state, true);
        if (++progress < config.recyclerTicksPerItem) {
            return;
        }
        progress = 0;
        ItemStack stack = items.get(slot);
        int xp = bagXp(stack, level.getGameTime());
        if (xp > 0) {
            storedXp = (int) Math.min(Integer.MAX_VALUE / 2, (long) storedXp + xp);
            level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.3F, 0.6F + level.getRandom().nextFloat() * 0.4F);
        } else {
            points += junkValue(stack);
        }
        stack.shrink(1);
        produceBags();
        if (level.getRandom().nextInt(3) == 0) {
            level.playSound(null, pos, ModContent.SOUND_GRIND.get(), SoundSource.BLOCKS, 0.5F, 0.85F + level.getRandom().nextFloat() * 0.3F);
        }
        setChanged();
    }

    /** First input slot that can be processed: bags always, junk only while there is room for the bag it would make. */
    private int nextInput(MachinesConfig config, long now) {
        boolean outputBlocked = points >= config.pointsPerBag;
        for (int i = 0; i < INPUTS; i++) {
            ItemStack stack = items.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (bagXp(stack, now) > 0 || (!outputBlocked && junkValue(stack) > 0)) {
                return i;
            }
        }
        return -1;
    }

    private void produceBags() {
        MachinesConfig config = LootBagsConfig.machines();
        while (points >= config.pointsPerBag) {
            ItemStack bag = new ItemStack(ModContent.bag(config.recyclerOutput));
            if (!outputAccepts(bag)) {
                return;
            }
            insertOutput(bag);
            points -= config.pointsPerBag;
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, worldPosition.getX() + 0.5, worldPosition.getY() + 1.1,
                        worldPosition.getZ() + 0.5, 8, 0.3, 0.1, 0.3, 0.0);
                serverLevel.playSound(null, worldPosition, ModContent.SOUND_BAG_DROP.get(), SoundSource.BLOCKS, 0.6F, 1.2F);
            }
            setChanged();
        }
    }

    public int storedXp() {
        return storedXp;
    }

    public void collectXp(ServerPlayer player) {
        if (storedXp <= 0) {
            player.displayClientMessage(Component.translatable("message.mob_loot_bags.recycler_empty"), true);
            return;
        }
        player.giveExperiencePoints(storedXp);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.6F, 1.2F);
        storedXp = 0;
        setChanged();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        int legacyLayout = tag.contains(LAYOUT_KEY, Tag.TAG_ANY_NUMERIC) ? tag.getInt(LAYOUT_KEY) : 1;
        if (legacyLayout < 2) {
            loadLegacy(tag);
        } else {
            storedXp = Math.max(0, tag.getInt("xp"));
        }
        points = tag.getInt("points");
        progress = tag.getInt("progress");
    }

    /** 1.x recyclers were 27-slot chests of bags; bags that no longer fit become stored experience. */
    private void loadLegacy(CompoundTag tag) {
        NonNullList<ItemStack> legacy = NonNullList.withSize(27, ItemStack.EMPTY);
        net.minecraft.world.ContainerHelper.loadAllItems(tag, legacy);
        items = NonNullList.withSize(INPUTS + OUTPUTS, ItemStack.EMPTY);
        List<ItemStack> overflow = new ArrayList<>();
        int next = 0;
        for (ItemStack stack : legacy) {
            if (stack.isEmpty()) {
                continue;
            }
            if (next < INPUTS) {
                items.set(next++, stack);
            } else {
                overflow.add(stack);
            }
        }
        int xp = Math.max(0, tag.getInt(LEGACY_XP_KEY));
        for (ItemStack stack : overflow) {
            xp += bagXp(stack, Long.MIN_VALUE) * stack.getCount();
        }
        storedXp = xp;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt(LAYOUT_KEY, 2);
        tag.putInt("points", points);
        tag.putInt("xp", storedXp);
        tag.putInt("progress", progress);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (storedXp > 0 && level instanceof ServerLevel serverLevel) {
            ExperienceOrb.award(serverLevel, Vec3.atCenterOf(pos), storedXp);
            storedXp = 0;
        }
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.mob_loot_bags.loot_recycler");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new LootRecyclerMenu(containerId, inventory, this, data);
    }
}
