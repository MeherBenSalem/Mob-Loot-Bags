package tn.naizo.moblootbags.common.mixin;

import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tn.naizo.moblootbags.common.drop.DropEngine;

/** Remembers spawner mobs (for the spawner penalty) and gives some natural spawns a loot bag to carry. */
@Mixin(Mob.class)
public abstract class MobMixin {
    @Inject(method = "finalizeSpawn", at = @At("RETURN"))
    private void mobLootBags$onFinalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason,
                                            @Nullable SpawnGroupData groupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        DropEngine.onFinalizeSpawn((Mob) (Object) this, level.getLevel(), reason);
    }
}
