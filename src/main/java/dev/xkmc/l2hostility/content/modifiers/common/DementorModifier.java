package dev.xkmc.l2hostility.content.modifiers.common;

import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.l2damagetracker.contents.damage.DefaultDamageState;
import dev.xkmc.l2damagetracker.init.data.L2DamageTypes;
import dev.xkmc.l2hostility.content.modifiers.core.MobModifier;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class DementorModifier extends MobModifier {

	@Override
	public void onCreateSource(int level, LivingEntity attacker, CreateSourceEvent event) {
		event.enable(DefaultDamageState.BYPASS_ARMOR);
	}

	@Override
	public void onAttackedByOthers(int level, LivingEntity entity, LivingAttackEvent event) {
		if (!event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY) &&
				!event.getSource().is(DamageTypeTags.BYPASSES_EFFECTS) &&
				!event.getSource().is(L2DamageTypes.MAGIC)) {
			event.setCanceled(true);
		}
	}
}
