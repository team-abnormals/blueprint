package com.minecraftabnormals.abnormals_core.core.api.conditions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.minecraftabnormals.abnormals_core.core.AbnormalsCore;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SmellyModder (Luke Tonon)
 */
public final class ACAndRecipeCondition implements ICondition {
	private final ResourceLocation location;
	private final List<ICondition> children;
	
	public ACAndRecipeCondition(ResourceLocation location, List<ICondition> children) {
		this.location = location;
		this.children = children;
	}
	
	@Override
	public ResourceLocation getID() {
		return this.location;
	}

	@Override
	public boolean test() {
		return !this.children.isEmpty();
	}

	public static class Serializer implements IConditionSerializer<ACAndRecipeCondition> {
		private final ResourceLocation location;

		public Serializer() {
			this.location = new ResourceLocation(AbnormalsCore.MODID, "and");
		}

		@Override
		public void write(JsonObject json, ACAndRecipeCondition value) {
			JsonArray values = new JsonArray();
			for (ICondition child : value.children) {
				values.add(CraftingHelper.serialize(child));
			}
			json.add("values", values);
		}

		@Override
		public ACAndRecipeCondition read(JsonObject json) {
			List<ICondition> children = new ArrayList<>();
			for (JsonElement elements : GsonHelper.getAsJsonArray(json, "values")) {
				if (!elements.isJsonObject()) {
					throw new JsonSyntaxException("And condition values must be an array of JsonObjects");
				}
				ICondition condition = CraftingHelper.getCondition(elements.getAsJsonObject());
				if (!condition.test()) {
					children.clear();
					break;
				} else {
					children.add(condition);
				}
			}
			return new ACAndRecipeCondition(this.location, children);
		}

		@Override
		public ResourceLocation getID() {
			return this.location;
		}
	}
}