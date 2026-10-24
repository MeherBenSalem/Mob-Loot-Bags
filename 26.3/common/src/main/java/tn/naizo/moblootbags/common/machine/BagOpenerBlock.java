package tn.naizo.moblootbags.common.machine;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import tn.naizo.moblootbags.common.content.ModContent;

public class BagOpenerBlock extends MachineBlock {
    public BagOpenerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected Supplier<? extends BlockEntityType<? extends MachineBlockEntity>> entityType() {
        return ModContent.BAG_OPENER_ENTITY;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BagOpenerBlockEntity(pos, state);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(ACTIVE) || random.nextInt(3) != 0) {
            return;
        }
        Direction facing = state.getValue(FACING);
        double x = pos.getX() + 0.5 + facing.getStepX() * 0.52 + (random.nextDouble() - 0.5) * 0.4;
        double y = pos.getY() + 0.3 + random.nextDouble() * 0.4;
        double z = pos.getZ() + 0.5 + facing.getStepZ() * 0.52 + (random.nextDouble() - 0.5) * 0.4;
        level.addParticle(ParticleTypes.WAX_OFF, x, y, z, 0.0, 0.0, 0.0);
    }
}
