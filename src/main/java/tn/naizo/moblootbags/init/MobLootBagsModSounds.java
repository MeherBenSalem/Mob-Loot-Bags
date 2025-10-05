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
	public static final RegistryObject<SoundEvent> DRAMATIC = REGISTRY.register("dramatic", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mob_loot_bags", "dramatic")));
	public static final RegistryObject<SoundEvent> LOOTBAG_SFX_1 = REGISTRY.register("lootbag_sfx_1", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mob_loot_bags", "lootbag_sfx_1")));
	public static final RegistryObject<SoundEvent> LOOTBAG_SFX_2 = REGISTRY.register("lootbag_sfx_2", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mob_loot_bags", "lootbag_sfx_2")));
}