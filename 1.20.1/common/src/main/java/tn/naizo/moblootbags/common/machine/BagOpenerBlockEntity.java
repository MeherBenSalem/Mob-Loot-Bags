package tn.naizo.moblootbags.common.machine;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import tn.naizo.moblootbags.common.bag.BagData;
import tn.naizo.moblootbags.common.config.LootBagsConfig;
import tn.naizo.moblootbags.common.content.BagType;
import tn.naizo.moblootbags.common.content.ModContent;
import tn.naizo.moblootbags.common.item.LootBagItem;
import tn.naizo.moblootbags.common.loot.LootService;

/**
 * Opens loot bags from its 9 input slots into its 18 output slots. Redstone mode: always on, on with a signal, on
 * without a signal, or one bag per redstone pulse.
 */
public class BagOpenerBlockEntity extends MachineBlockEntity {
    public static final int INPUTS = 9;
    public static final int OUTPUTS = 18;
    public static final int MODE_ALWAYS = 0;
    public static final int MODE_HIGH = 1;
    public static final int MODE_LOW = 2;
    public static final int MODE_PULSE = 3;
    public static final int STATUS_IDLE = 0;
    public static final int STATUS_WORKING = 1;
    public static final int STATUS_FULL = 2;
    public static final int STATUS_WAITING = 3;
    private static final String LAYOUT_KEY = "mlb_layout";

    private int progress;
    private int mode = MODE_ALWAYS;
    private int status;
    private int pulses;
    private boolean wasPowered;
    private final List<ItemStack> pending = new ArrayList<>();

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> LootBagsConfig.machines().openerTicksPerBag;
                case 2 -> mode;
                case 3 -> status;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 2) {
                mode = Math.floorMod(value, 4);
                pulses = 0;
                setChanged();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public BagOpenerBlockEntity(BlockPos pos, BlockState state) {
        super(ModContent.BAG_OPENER_ENTITY.get(), pos, state, INPUTS, OUTPUTS);
    }

    public static boolean canOpen(ItemStack stack) {
        BagType type = LootBagItem.typeOf(stack);
        return type != null && (type.isTier() || type == BagType.SUMMONING || type == BagType.HOURGLASS);
    }

    @Override
    public boolean acceptsInput(ItemStack stack) {
        return canOpen(stack);
    }

    @Override
    public void serverTick(ServerLevel level, BlockPos pos, BlockState state) {
        boolean powered = level.hasNeighborSignal(pos);
        if (mode == MODE_PULSE && powered && !wasPowered) {
            pulses = Math.min(pulses + 1, 64);
        }
        wasPowered = powered;

        flushPending();
        int slot = nextBag(level);
        boolean enabled = switch (mode) {
            case MODE_HIGH -> powered;
            case MODE_LOW -> !powered;
            case MODE_PULSE -> pulses > 0;
            default -> true;
        };
        if (!pending.isEmpty() || (slot >= 0 && freeOutputSlots() == 0)) {
            status = STATUS_FULL;
            setActive(level, pos, state, false);
            return;
        }
        if (slot < 0 || !enabled) {
            status = slot < 0 && !items.subList(0, INPUTS).stream().allMatch(ItemStack::isEmpty) ? STATUS_WAITING : STATUS_IDLE;
            progress = 0;
            setActive(level, pos, state, false);
            return;
        }
        status = STATUS_WORKING;
        setActive(level, pos, state, true);
        if (++progress >= LootBagsConfig.machines().openerTicksPerBag) {
            progress = 0;
            if (mode == MODE_PULSE) {
                pulses--;
            }
            openBag(level, pos, slot);
        }
        setChanged();
    }

    /** The first input slot holding a bag that can be opened now (unripe Hourglass bags wait). */
    private int nextBag(ServerLevel level) {
        for (int i = 0; i < INPUTS; i++) {
            ItemStack stack = items.get(i);
            if (stack.isEmpty() || !canOpen(stack)) {
                continue;
            }
            if (LootBagItem.typeOf(stack) == BagType.HOURGLASS) {
                BagData.migrateLegacyTimer(stack, level.getGameTime());
                if (!BagData.isRipe(stack, level.getGameTime())) {
                    continue;
                }
            }
            return i;
        }
        return -1;
    }

    private void openBag(ServerLevel level, BlockPos pos, int slot) {
        ItemStack bag = items.get(slot);
        BagType type = LootBagItem.typeOf(bag);
        if (type == null) {
            return;
        }
        String theme = BagData.theme(bag);
        List<ItemStack> loot = LootService.roll(level, null, Vec3.atCenterOf(pos), type, theme);
        bag.shrink(1);
        for (ItemStack drop : loot) {
            ItemStack left = insertOutput(drop);
            if (!left.isEmpty()) {
                pending.add(left);
            }
        }
        level.playSound(null, pos, ModContent.SOUND_MACHINE.get(), SoundSource.BLOCKS, 0.7F, 0.9F + level.getRandom().nextFloat() * 0.2F);
        level.sendParticles(new DustParticleOptions(type.dustColor(), 1.0F), pos.getX() + 0.5, pos.getY() + 1.05, pos.getZ() + 0.5,
                8, 0.25, 0.1, 0.25, 0.02);
        setChanged();
    }

    private void flushPending() {
        pending.replaceAll(this::insertOutput);
        pending.removeIf(ItemStack::isEmpty);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        progress = tag.getInt("progress");
        mode = Math.floorMod(tag.getInt("mode"), 4);
        pulses = tag.getInt("pulses");
        pending.clear();
        ListTag saved = tag.getList("pending", Tag.TAG_COMPOUND);
        for (int i = 0; i < saved.size(); i++) {
            pending.add(ItemStack.of(saved.getCompound(i)));
        }
        if ((tag.contains(LAYOUT_KEY, Tag.TAG_ANY_NUMERIC) ? tag.getInt(LAYOUT_KEY) : 1) < 2) {
            relayoutLegacy();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("progress", progress);
        tag.putInt("mode", mode);
        tag.putInt("pulses", pulses);
        tag.putInt(LAYOUT_KEY, 2);
        if (!pending.isEmpty()) {
            ListTag saved = new ListTag();
            pending.forEach(stack -> saved.add(stack.save(new CompoundTag())));
            tag.put("pending", saved);
        }
    }

    /** 1.x openers were a 27-slot chest of bags. Bags go to the inputs, the rest wait in the outputs for pickup. */
    private void relayoutLegacy() {
        List<ItemStack> stacks = new ArrayList<>();
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }
        if (stacks.isEmpty()) {
            return;
        }
        items = NonNullList.withSize(INPUTS + OUTPUTS, ItemStack.EMPTY);
        int nextInput = 0;
        int nextOutput = INPUTS;
        for (ItemStack stack : stacks) {
            if (stack.getItem() instanceof LootBagItem && nextInput < INPUTS) {
                items.set(nextInput++, stack);
            } else if (nextOutput < items.size()) {
                items.set(nextOutput++, stack);
            } else {
                pending.add(stack);
            }
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (level != null) {
            NonNullList<ItemStack> leftover = NonNullList.create();
            leftover.addAll(pending);
            Containers.dropContents(level, pos, leftover);
            pending.clear();
        }
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.mob_loot_bags.bag_opener");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new BagOpenerMenu(containerId, inventory, this, data);
    }
}
