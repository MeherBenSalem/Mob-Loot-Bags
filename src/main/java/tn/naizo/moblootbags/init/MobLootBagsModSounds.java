
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package tn.naizo.moblootbags.init;

import tn.naizo.moblootbags.MobLootBagsMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;

public class MobLootBagsModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MobLootBagsMod.MODID);
	public static final RegistryObject<SoundEvent> DRAMATIC = REGISTRY.register("dramatic", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("mob_loot_bags", "dramatic")));
}
