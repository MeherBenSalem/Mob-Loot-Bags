package tn.naizo.moblootbags.common.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** An item with a one-line description, optionally with an enchantment glint. */
public class TooltipItem extends Item {
    private final String tooltipKey;
    private final boolean foil;

    public TooltipItem(Item.Properties properties, String tooltipKey, boolean foil) {
        super(properties);
        this.tooltipKey = tooltipKey;
        this.foil = foil;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return foil || super.isFoil(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(tooltipKey).withStyle(ChatFormatting.GRAY));
    }
}
