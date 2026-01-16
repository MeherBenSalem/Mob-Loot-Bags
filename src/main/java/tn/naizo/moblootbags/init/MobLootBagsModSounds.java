/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package tn.naizo.moblootbags.init;

import tn.naizo.moblootbags.MobLootBagsMod;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

public class MobLootBagsModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, MobLootBagsMod.MODID);
	public static final DeferredHolder<SoundEvent, SoundEvent> DRAMATIC = REGISTRY.register("dramatic", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mob_loot_bags", "dramatic")));
	public static final DeferredHolder<SoundEvent, SoundEvent> LOOTBAG_SFX_1 = REGISTRY.register("lootbag_sfx_1", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mob_loot_bags", "lootbag_sfx_1")));
	public static final DeferredHolder<SoundEvent, SoundEvent> LOOTBAG_SFX_2 = REGISTRY.register("lootbag_sfx_2", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mob_loot_bags", "lootbag_sfx_2")));
}