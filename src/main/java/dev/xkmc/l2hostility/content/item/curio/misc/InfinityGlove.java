package dev.xkmc.l2hostility.content.item.curio.misc;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.xkmc.l2hostility.content.item.curio.core.CurioItem;
import dev.xkmc.l2hostility.content.item.curio.core.ICapItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.List;
import java.util.UUID;

public class InfinityGlove extends CurioItem implements ICapItem<InfinityGlove.Cap> {

	public InfinityGlove(Properties properties) {
		super(properties);
	}

	@Override
	public Cap create(ItemStack stack) {
		return new Cap(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
	}

	public record Cap(ItemStack stack) implements ICurio {

		@Override
		public ItemStack getStack() {
			return stack;
		}

		@Override
		public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid) {
			Multimap<Attribute, AttributeModifier> map = HashMultimap.create();
			CuriosApi.addSlotModifier(map, "ring", uuid, 5, AttributeModifier.Operation.ADDITION);
			CuriosApi.addSlotModifier(map, "charm", uuid, 1, AttributeModifier.Operation.ADDITION);
			return map;
		}
	}

}