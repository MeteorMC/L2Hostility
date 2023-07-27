package dev.xkmc.l2complements.content.config;

import dev.xkmc.l2library.serial.config.BaseConfig;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.world.entity.EntityType;

import java.util.LinkedHashSet;

@SerialClass
public class ModifierConfig extends BaseConfig {

	public static final ModifierConfig DEFAULT = new ModifierConfig(10, 1);

	@SerialClass.SerialField
	public int cost;

	@SerialClass.SerialField
	public double chance;

	@SerialClass.SerialField
	public LinkedHashSet<EntityType<?>> blacklist = new LinkedHashSet<>();

	@SerialClass.SerialField
	public LinkedHashSet<EntityType<?>> whitelist = new LinkedHashSet<>();

	@Deprecated
	public ModifierConfig() {
	}

	public ModifierConfig(int cost, double chance) {
		this.cost = cost;
		this.chance = chance;
	}


}
