package dev.xkmc.l2hostility.content.capability.mob;

import dev.xkmc.l2hostility.content.config.EntityConfig;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

@SerialClass
public class MinionData {

	@SerialClass.SerialField(toClient = true)
	public UUID uuid;

	@SerialClass.SerialField(toClient = true)
	public int id;

	@SerialClass.SerialField(toClient = true)
	public double linkDistance;

	@SerialClass.SerialField(toClient = true)
	public boolean protectMaster, discardOnUnlink;

	@Nullable
	public Mob master;

	public boolean tick(LivingEntity mob) {
		if (mob.level() instanceof ServerLevel sl) {
			if (master == null) {
				var e = sl.getEntity(uuid);
				if (e instanceof Mob mas) {
					master = mas;
				} else if (discardOnUnlink) {
					mob.discard();
				}
			}
			if (master != null && master.distanceTo(mob) > linkDistance) {
				var next = MasterData.getRandomPos(sl, mob.getType(), master, (int) (linkDistance * 0.5), 16);
				if (next != null) {
					mob.moveTo(Vec3.atCenterOf(next));
				} else if (discardOnUnlink) {
					mob.discard();
				}
			}
		} else {
			if (master == null) {
				var e = mob.level().getEntity(id);
				if (e instanceof Mob mas && mas.getUUID().equals(uuid)) {
					master = mas;
				}
			}
			if (master != null && id != master.getId()) {
				id = master.getId();
				return true;
			}
		}
		return false;
	}

	public MinionData init(Mob mob, EntityConfig.Minion config) {
		uuid = mob.getUUID();
		id = mob.getId();
		linkDistance = config.linkDistance();
		protectMaster = config.protectMaster();
		discardOnUnlink = config.discardOnUnlink();
		master = mob;
		return this;
	}

}
