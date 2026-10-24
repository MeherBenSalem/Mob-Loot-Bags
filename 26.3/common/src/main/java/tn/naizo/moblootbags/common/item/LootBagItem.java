package tn.naizo.moblootbags.common.item;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import tn.naizo.moblootbags.common.bag.BagData;
import tn.naizo.moblootbags.common.bag.BagOpening;
import tn.naizo.moblootbags.common.bag.Hourglass;
import tn.naizo.moblootbags.common.client.ClientBridge;
import tn.naizo.moblootbags.common.content.BagType;
import tn.naizo.moblootbags.common.content.ModContent;

/** Every loot bag. Hold Use to open it; sneak to open the whole stack. */
public class LootBagItem extends Item {
    private final BagType type;

    public LootBagItem(BagType type, Item.Properties properties) {
        super(properties);
        this.type = type;
    }

    public BagType type() {
        return type;
    }

    public static @Nullable BagType typeOf(ItemStack stack) {
        return stack.getItem() instanceof LootBagItem bag ? bag.type : null;
    }

    @Override
    public Component getName(ItemStack stack) {
        return super.getName(stack).copy().withColor(type.color());
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!BagOpening.canStartOpening(level, player, stack, type)) {
            return InteractionResult.FAIL;
        }
        if (BagOpening.holdTicks() <= 0) {
            if (player instanceof ServerPlayer serverPlayer) {
                BagOpening.open(serverPlayer, stack, hand);
            }
            return InteractionResult.SUCCESS;
        }
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return Math.max(1, BagOpening.holdTicks());
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
        int elapsed = getUseDuration(stack, entity) - remaining;
        if (level.isClientSide()) {
            if (elapsed % 2 == 0) {
                Vec3 look = entity.getLookAngle();
                Vec3 at = entity.getEyePosition().add(look.scale(0.6)).add(0, -0.35, 0);
                level.addParticle(new DustParticleOptions(type.color(), 0.6F),
                        at.x + (level.getRandom().nextDouble() - 0.5) * 0.4, at.y + level.getRandom().nextDouble() * 0.3,
                        at.z + (level.getRandom().nextDouble() - 0.5) * 0.4, 0, 0.02, 0);
            }
        } else if (elapsed % 4 == 0) {
            float progress = elapsed / (float) Math.max(1, getUseDuration(stack, entity));
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ModContent.SOUND_BAG_RUSTLE.get(), SoundSource.PLAYERS,
                    0.6F, 0.9F + progress * 0.5F);
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            BagOpening.open(player, stack, player.getUsedItemHand());
        }
        return stack;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        if (type == BagType.HOURGLASS) {
            Hourglass.tick(stack, level, owner);
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        if (type == BagType.HOURGLASS) {
            return BagData.isRipe(stack, ClientBridge.gameTime());
        }
        return type == BagType.MYTHIC || super.isFoil(stack);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return type == BagType.HOURGLASS && BagData.ripeAt(stack) >= 0 && !BagData.isRipe(stack, ClientBridge.gameTime());
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Mth.clamp(Math.round(Hourglass.progress(stack, ClientBridge.gameTime()) * 13.0F), 0, 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return type.color();
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        Component tierName = Component.translatable(type.translationKey()).withColor(type.color());
        String theme = BagData.theme(stack);
        if (theme != null) {
            tooltip.accept(Component.translatable("tooltip.mob_loot_bags.tier_theme", tierName,
                    Component.translatable("theme.mob_loot_bags." + theme).withStyle(ChatFormatting.GOLD)).withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.accept(Component.translatable("tooltip.mob_loot_bags.tier", tierName).withStyle(ChatFormatting.GRAY));
        }
        if (!type.isTier()) {
            tooltip.accept(Component.translatable("tooltip.mob_loot_bags." + type.key()).withStyle(ChatFormatting.DARK_AQUA));
        }
        if (type == BagType.HOURGLASS) {
            long now = ClientBridge.gameTime();
            if (BagData.isRipe(stack, now)) {
                tooltip.accept(Component.translatable("tooltip.mob_loot_bags.hourglass.ripe").withColor(type.color()));
            } else if (BagData.ripeAt(stack) >= 0) {
                tooltip.accept(Component.translatable("tooltip.mob_loot_bags.hourglass.wait",
                        Hourglass.formatRemaining(BagData.ripeAt(stack) - now)).withStyle(ChatFormatting.GRAY));
            }
        }
        if (BagOpening.holdTicks() > 0) {
            tooltip.accept(Component.translatable("tooltip.mob_loot_bags.open_hold").withStyle(ChatFormatting.DARK_GRAY));
        } else {
            tooltip.accept(Component.translatable("tooltip.mob_loot_bags.open_click").withStyle(ChatFormatting.DARK_GRAY));
        }
        if (type != BagType.CURSED) {
            ClientBridge.appendPreview(stack, type, theme, tooltip);
        }
    }
}
