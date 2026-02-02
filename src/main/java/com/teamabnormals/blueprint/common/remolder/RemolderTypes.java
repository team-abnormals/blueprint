package com.teamabnormals.blueprint.common.remolder;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.teamabnormals.blueprint.common.remolder.data.DynamicReference;
import com.teamabnormals.blueprint.core.util.registry.BasicRegistry;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

import static com.teamabnormals.blueprint.common.remolder.data.DynamicReference.parse;
import static com.teamabnormals.blueprint.common.remolder.data.DynamicReference.target;

/**
 * The global registry for {@link Remolder} types/serializers.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class RemolderTypes {
	static final BasicRegistry<MapCodec<? extends Remolder>> REGISTRY = new BasicRegistry<>();

	static {
		REGISTRY.register("replace", ReplaceRemolder.CODEC);
		REGISTRY.register("remove", RemoveRemolder.CODEC);
		REGISTRY.register("add", AddRemolder.CODEC);
		REGISTRY.register("noop", NoopRemolder.CODEC);
		REGISTRY.register("sequence", SequenceRemolder.CODEC);
		REGISTRY.register("conditional", ConditionalRemolder.CODEC);
		REGISTRY.register("loop", LoopRemolder.CODEC);
		REGISTRY.register("continue", ContinueRemolder.CODEC);
		REGISTRY.register("break", BreakRemolder.CODEC);
	}

	public static synchronized void register(ResourceLocation name, MapCodec<? extends Remolder> codec) {
		REGISTRY.register(name, codec);
	}

	public static ReplaceRemolder replace(DynamicReference.Expression target, DynamicReference value) {
		return new ReplaceRemolder(target, value);
	}

	public static RemoveRemolder remove(DynamicReference.Expression target) {
		return new RemoveRemolder(target);
	}

	public static AddRemolder add(DynamicReference.Expression target, DynamicReference value) {
		return new AddRemolder(target, value);
	}

	public static NoopRemolder noop() {
		return NoopRemolder.INSTANCE;
	}

	public static SequenceRemolder sequence(Remolder... remolders) {
		return new SequenceRemolder(List.of(remolders));
	}

	public static SequenceRemolder.Builder sequence() {
		return new SequenceRemolder.Builder();
	}

	public static ConditionalRemolder remoldIf(DynamicReference.Expression condition, Remolder remolder) {
		return new ConditionalRemolder(List.of(Pair.of(condition, remolder)), null);
	}

	public static ConditionalRemolder ifElse(DynamicReference.Expression condition, Remolder remolder, Remolder elseRemolder) {
		return new ConditionalRemolder(List.of(Pair.of(condition, remolder)), elseRemolder);
	}

	@SafeVarargs
	public static ConditionalRemolder ifElseIf(Pair<DynamicReference.Expression, Remolder>... branches) {
		return new ConditionalRemolder(List.of(branches), null);
	}

	@SafeVarargs
	public static ConditionalRemolder ifElseIfElse(@Nullable Remolder elseRemolder, Pair<DynamicReference.Expression, Remolder>... branches) {
		return new ConditionalRemolder(List.of(branches), elseRemolder);
	}

	public static LoopRemolder loopWhile(DynamicReference.Expression condition, Remolder remolder) {
		return new LoopRemolder(condition, Either.left(remolder));
	}

	public static LoopRemolder doWhile(Remolder remolder, DynamicReference.Expression condition) {
		return new LoopRemolder(condition, Either.right(remolder));
	}

	public static BreakRemolder breakLoop() {
		return BreakRemolder.INSTANCE;
	}

	public static ContinueRemolder continueLoop() {
		return ContinueRemolder.INSTANCE;
	}

	public static Remolder[] addAllToList(String target, String listName, DynamicReference list) {
		String targetList = "$safe_" + listName;
		return new Remolder[]{
				replace(target("$" + listName), list),
				replace(target(targetList), parse("#list(" + target + ")")),
				replace(target("$generic_adding_iterator"), parse("#elements(#list($" + listName + "))")),
				loopWhile(parse("#hasNext($generic_adding_iterator)"), add(target(targetList), parse("(#) #next($generic_adding_iterator)")))
		};
	}

	public static Remolder[] addAllToMap(String target, String mapName, DynamicReference map) {
		String targetMap = "$safe_" + mapName;
		return new Remolder[]{
				replace(target("$" + mapName), map),
				replace(target(targetMap), parse("#map(" + target + ")")),
				replace(target("$generic_adding_iterator"), parse("#entries(#map($" + mapName + "))")),
				loopWhile(
						parse("#hasNext($generic_adding_iterator)"),
						sequence(
								replace(target("$next_entry"), parse("#nextEntry($generic_adding_iterator)")),
								replace(target(targetMap + "[#getKey($next_entry)]"), parse("#getValue($next_entry)"))
						)
				)
		};
	}

	public static Remolder[] removeAllFromMap(String target, String mapName, DynamicReference keys) {
		String targetMap = "$safe_" + mapName;
		return new Remolder[]{
				replace(target("$" + mapName), keys),
				replace(target(targetMap), parse("#map(" + target + ")")),
				replace(target("$generic_removing_iterator"), parse("#elements(#list($" + mapName + "))")),
				loopWhile(
						parse("#hasNext($generic_removing_iterator)"),
						remove(target(targetMap + "[(String) (#) #next($generic_removing_iterator)]"))
				)
		};
	}
}
