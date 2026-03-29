package tn.naizo.moblootbags.common;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public final class LootBagRecyclerBlock extends Block implements EntityBlock {
    public LootBagRecyclerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 15;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof LootBagRecyclerBlockEntity recycler && player instanceof ServerPlayer serverPlayer) {
                ItemStack held = player.getItemInHand(hand);
                if (serverPlayer.isShiftKeyDown() && held.isEmpty()) {
                    int xp = recycler.takeStoredXp();
                    if (xp > 0) {
                        serverPlayer.giveExperiencePoints(xp);
                    } else {
                        serverPlayer.displayClientMessage(Component.literal("not enough experience in the recycle block"), true);
                    }
                    return InteractionResult.CONSUME;
                }
                serverPlayer.openMenu(recycler);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof LootBagRecyclerBlockEntity recycler) {
                Containers.dropContents(level, pos, recycler);
                int xp = recycler.takeStoredXp();
                if (xp > 0 && level instanceof ServerLevel serverLevel) {
                    ExperienceOrb.award(serverLevel, Vec3.atCenterOf(pos), xp);
                }
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LootBagRecyclerBlockEntity(pos, state);
    }
}
