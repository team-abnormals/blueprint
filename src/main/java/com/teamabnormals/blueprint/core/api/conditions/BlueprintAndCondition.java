package com.teamabnormals.blueprint.core.api.conditions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.teamabnormals.blueprint.core.Blueprint;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import java.util.ArrayList;
import java.util.List;

/**
 * A special version of the {@link net.minecraftforge.common.crafting.conditions.AndCondition} that stops reading if a false condition is met.
 * <p>This is useful for testing another condition only if the former conditions are met.</p>
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class BlueprintAndCondition implements ICondition {
	private final ResourceLocation location;
	private final List<ICondition> children;

	public BlueprintAndCondition(ResourceLocation location, List<ICondition> children) {
		this.location = location;
		this.children = children;
	}

	@Override
	public ResourceLocation getID() {
		return this.location;
	}

	@Override
	public boolean test(IContext context) {
		return !this.children.isEmpty();
	}

	public static class Serializer implements IConditionSerializer<BlueprintAndCondition> {
		private final ResourceLocation location;

		public Serializer() {
			this.location = new ResourceLocation(Blueprint.MOD_ID, "and");
		}

		@Override
		public void write(JsonObject json, BlueprintAndCondition value) {
			JsonArray values = new JsonArray();
			for (ICondition child : value.children) {
				values.add(CraftingHelper.serialize(child));
			}
			json.add("values", values);
		}

		@Override
		public BlueprintAndCondition read(JsonObject json) {
			List<ICondition> children = new ArrayList<>();
			for (JsonElement elements : GsonHelper.getAsJsonArray(json, "values")) {
				if (!elements.isJsonObject()) {
					throw new JsonSyntaxException("And condition values must be an array of JsonObjects");
				}
				ICondition condition = CraftingHelper.getCondition(elements.getAsJsonObject());
				if (!condition.test(IContext.EMPTY)) {
					children.clear();
					break;
				} else {
					children.add(condition);
				}
			}
			return new BlueprintAndCondition(this.location, children);
		}

		@Override
		public ResourceLocation getID() {
			return this.location;
		}
	}
}