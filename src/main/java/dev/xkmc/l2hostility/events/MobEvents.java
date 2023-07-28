package dev.xkmc.l2hostility.events;

import dev.xkmc.l2hostility.content.capability.mob.MobModifierCap;
import dev.xkmc.l2hostility.init.L2Hostility;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = L2Hostility.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MobEvents {

	@SubscribeEvent
	public static void onMobAttack(LivingAttackEvent event) {
		if (MobModifierCap.HOLDER.isProper(event.getEntity())) {
			MobModifierCap.HOLDER.get(event.getEntity()).modifiers
					.forEach((k, v) -> k.onAttackedByOthers(v, event.getEntity(), event));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onMobHurt(LivingHurtEvent event) {
		if (MobModifierCap.HOLDER.isProper(event.getEntity())) {
			MobModifierCap.HOLDER.get(event.getEntity()).modifiers
					.forEach((k, v) -> k.onHurtByOthers(v, event.getEntity(), event));
		}
	}

}
