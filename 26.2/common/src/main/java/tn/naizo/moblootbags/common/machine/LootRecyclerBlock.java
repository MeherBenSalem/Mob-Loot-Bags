package tn.naizo.moblootbags.common.machine;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import tn.naizo.moblootbags.common.content.ModContent;

public class LootRecyclerBlock extends MachineBlock {
    private static final MapCodec<LootRecyclerBlock> CODEC = simpleCodec(LootRecyclerBlock::new);

    public LootRecyclerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<LootRecyclerBlock> codec() {
        return CODEC;
    }

    @Override
    protected Supplier<? extends BlockEntityType<? extends MachineBlockEntity>> entityType() {
        return ModContent.LOOT_RECYCLER_ENTITY;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LootRecyclerBlockEntity(pos, state);
    }

    /** Sneak-use with an empty hand collects the stored experience, as in 1.x. */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.isShiftKeyDown() && level.getBlockEntity(pos) instanceof LootRecyclerBlockEntity recycler) {
            if (player instanceof ServerPlayer serverPlayer) {
                recycler.collectXp(serverPlayer);
            }
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(ACTIVE)) {
            return;
        }
        if (random.nextInt(2) == 0) {
            level.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.3 + random.nextDouble() * 0.4, pos.getY() + 1.02,
                    pos.getZ() + 0.3 + random.nextDouble() * 0.4, 0.0, 0.03, 0.0);
        }
        Direction facing = state.getValue(FACING);
        if (random.nextInt(4) == 0) {
            level.addParticle(ParticleTypes.CRIT, pos.getX() + 0.5 + facing.getStepX() * 0.55, pos.getY() + 0.45,
                    pos.getZ() + 0.5 + facing.getStepZ() * 0.55, facing.getStepX() * 0.1, 0.05, facing.getStepZ() * 0.1);
        }
    }
}
