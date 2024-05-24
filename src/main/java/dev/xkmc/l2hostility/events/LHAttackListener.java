package dev.xkmc.l2hostility.events;

import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.AttackListener;
import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.l2damagetracker.contents.damage.DefaultDamageState;
import dev.xkmc.l2damagetracker.init.data.L2DamageTypes;
import dev.xkmc.l2hostility.compat.curios.CurioCompat;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.enchantments.HitTargetEnchantment;
import dev.xkmc.l2hostility.content.item.curio.core.CurseCurioItem;
import dev.xkmc.l2hostility.content.logic.TraitEffectCache;
import dev.xkmc.l2hostility.init.data.HostilityDamageState;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LHTagGen;
import dev.xkmc.l2hostility.init.registrate.LHItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public class LHAttackListener implements AttackListener {

	private static boolean masterImmunity(AttackCache cache) {
		if (cache.getAttacker() instanceof Mob mob && MobTraitCap.HOLDER.isProper(mob)) {
			var cap = MobTraitCap.HOLDER.get(mob);
			if (cap.asMinion != null) {
				if (cache.getAttackTarget() == cap.asMinion.master) {
					return true;
				}
			}
		}
		if (cache.getAttackTarget() instanceof Mob mob && MobTraitCap.HOLDER.isProper(mob)) {
			var cap = MobTraitCap.HOLDER.get(mob);
			if (cap.asMinion != null) {
				if (cache.getAttacker() == cap.asMinion.master) {
					return true;
				}
			}
			if (cap.asMaster != null) {
				for (var e : cap.asMaster.data) {
					if (e.minion.isAlive()) {
						var mcap = MobTraitCap.HOLDER.get(e.minion);
						if (mcap.asMinion != null && mcap.asMinion.protectMaster) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public void onAttack(AttackCache cache, ItemStack weapon) {
		var event = cache.getLivingAttackEvent();
		assert event != null;
		if (masterImmunity(cache)) {
			event.setCanceled(true);
		}
	}

	@Override
	public void onHurt(AttackCache cache, ItemStack weapon) {
		var event = cache.getLivingHurtEvent();
		assert event != null;
		if (event.getSource().is(L2DamageTypes.NO_SCALE))
			return;
		LivingEntity mob = cache.getAttacker();
		var target = cache.getAttackTarget();
		if (mob == target)
			return;
		if (MobTraitCap.HOLDER.isProper(target)) {
			MobTraitCap cap = MobTraitCap.HOLDER.get(target);
			for (var e : weapon.getAllEnchantments().entrySet()) {
				if (e.getKey() instanceof HitTargetEnchantment ench) {
					ench.hitMob(target, cap, e.getValue(), cache);
				}
			}
		}
		if (mob != null && MobTraitCap.HOLDER.isProper(mob)) {
			MobTraitCap cap = MobTraitCap.HOLDER.get(mob);
			if (!mob.getType().is(LHTagGen.NO_SCALING)) {
				int lv = cap.getLevel();
				double factor;
				if (LHConfig.COMMON.exponentialDamage.get()) {
					factor = Math.pow(1 + LHConfig.COMMON.damageFactor.get(), lv);
				} else {
					factor = 1 + lv * LHConfig.COMMON.damageFactor.get();
				}
				cache.addHurtModifier(DamageModifier.multTotal((float) factor));
			}
			TraitEffectCache traitCache = new TraitEffectCache(target);
			cap.traitEvent((k, v) -> k.onHurtTarget(v, mob, cache, traitCache));
		}
		if (mob != null) {
			for (var e : CurseCurioItem.getFromPlayer(mob)) {
				e.item().onHurtTarget(e.stack(), mob, cache);
			}
		}
	}

	@Override
	public void onDamage(AttackCache cache, ItemStack weapon) {
		var mob = cache.getAttackTarget();
		if (MobTraitCap.HOLDER.isProper(mob)) {
			MobTraitCap cap = MobTraitCap.HOLDER.get(mob);
			cap.traitEvent((k, v) -> k.onDamaged(v, mob, cache));
		}
		if (masterImmunity(cache)) {
			cache.addDealtModifier(DamageModifier.nonlinearFinal(10432, e -> 0));
		}
	}

	@Override
	public void onCreateSource(CreateSourceEvent event) {
		LivingEntity mob = event.getAttacker();
		if (MobTraitCap.HOLDER.isProper(mob)) {
			MobTraitCap.HOLDER.get(mob).traitEvent((k, v) -> k.onCreateSource(v, event.getAttacker(), event));
		}
		var type = event.getResult();
		if (type == null) return;
		var root = type.toRoot();
		if (root == L2DamageTypes.MOB_ATTACK || root == L2DamageTypes.PLAYER_ATTACK) {
			if (CurioCompat.hasItemInCurioOrSlot(mob, LHItems.IMAGINE_BREAKER.get())) {
				event.enable(DefaultDamageState.BYPASS_MAGIC);
			}
			if (CurioCompat.hasItemInCurio(mob, LHItems.PLATINUM_STAR.get())) {
				event.enable(HostilityDamageState.BYPASS_COOLDOWN);
			}
		}

	}

}
