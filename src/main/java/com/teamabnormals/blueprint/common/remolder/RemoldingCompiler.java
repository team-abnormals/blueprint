package com.teamabnormals.blueprint.common.remolder;

import com.teamabnormals.blueprint.common.remolder.data.Molding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Predicate;

import static org.objectweb.asm.Opcodes.*;

/**
 * A {@link ClassLoader} subclass that compiles {@link Remold} instances into {@link Remolder} instances.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class RemoldingCompiler extends ClassLoader {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ExportEntry[] exports;

	public RemoldingCompiler(ClassLoader parent, ExportEntry... exports) {
		super(parent);
		this.exports = exports;
	}

	private static String formatIdentifierForClassName(String string) {
		StringBuilder result = new StringBuilder();
		boolean capitalizeNextLetter = true;
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if (Character.isLetterOrDigit(c)) {
				result.append(capitalizeNextLetter ? Character.toUpperCase(c) : c);
				capitalizeNextLetter = false;
			} else capitalizeNextLetter = true;
		}
		return result.toString();
	}

	private static String formatIdentifierForExport(String string) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			result.append(Character.isLetterOrDigit(c) ? c : "_");
		}
		return result.toString();
	}

	public <T> Remolding<T> compile(String identifier, Molding<T> molding, Remold... remolds) throws Throwable {
		return this.compile(remolds[0].type(), identifier, molding, remolds);
	}

	@SuppressWarnings("unchecked")
	public <T> Remolding<T> compile(String type, String identifier, Molding<T> molding, Remold... remolds) throws Throwable {
		int remoldsLength = remolds.length;
		if (remoldsLength == 0) throw new IllegalArgumentException("Cannot compile an empty array of Remolds");
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		String name = formatIdentifierForClassName(identifier) + type + "Remolding" + System.nanoTime();
		Type dataType = molding.getDataType();
		String typeDescriptor = dataType.getDescriptor();
		classWriter.visit(
				V17,
				ACC_PUBLIC,
				name,
				"L" + name + "<" + typeDescriptor + ">;Lcom/teamabnormals/blueprint/common/remolder/Remolding<" + typeDescriptor + ">;",
				"java/lang/Object",
				new String[]{"com/teamabnormals/blueprint/common/remolder/Remolding"}
		);
		MethodVisitor constructor = classWriter.visitMethod(
				ACC_PUBLIC,
				"<init>",
				"(Lcom/teamabnormals/blueprint/common/remolder/Remold$Fields;)V",
				null,
				null
		);
		constructor.visitVarInsn(ALOAD, 0);
		constructor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		Remold.Fields fields = new Remold.Fields();
		for (Remold remold : remolds) fields.addFields(remold.fields());
		fields.visitFields(name, molding, classWriter, constructor);
		constructor.visitInsn(RETURN);
		constructor.visitMaxs(0, 0);
		constructor.visitEnd();

		MethodVisitor toString = classWriter.visitMethod(
				ACC_PUBLIC,
				"toString",
				"()Ljava/lang/String;",
				null,
				null
		);
		toString.visitLdcInsn(identifier + " (" + name + ")");
		toString.visitInsn(ARETURN);
		toString.visitMaxs(0, 0);
		toString.visitEnd();

		String realApplyDescriptor = "(Lcom/mojang/serialization/DynamicOps;" + typeDescriptor + typeDescriptor + typeDescriptor + ")Lcom/mojang/datafixers/util/Pair;";
		String applySignature = "(Lcom/mojang/serialization/DynamicOps<" + typeDescriptor + ">;" + typeDescriptor + typeDescriptor + typeDescriptor + ")" + "Lcom/mojang/datafixers/util/Pair<" + typeDescriptor + typeDescriptor + ">;";
		MethodVisitor apply = classWriter.visitMethod(
				ACC_PUBLIC,
				"apply",
				realApplyDescriptor,
				applySignature,
				null
		);
		for (Remold remold : remolds) remold.visitor().visit(molding, name, apply);
		apply.visitVarInsn(ALOAD, 2);
		apply.visitVarInsn(ALOAD, 3);
		apply.visitMethodInsn(INVOKESTATIC, "com/mojang/datafixers/util/Pair", "of", "(Ljava/lang/Object;Ljava/lang/Object;)Lcom/mojang/datafixers/util/Pair;", false);
		apply.visitInsn(ARETURN);
		apply.visitMaxs(0, 0);
		apply.visitEnd();

		MethodVisitor bridgeApply = classWriter.visitMethod(
				ACC_PUBLIC | ACC_BRIDGE | ACC_SYNTHETIC,
				"apply",
				"(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/mojang/datafixers/util/Pair;",
				null,
				null
		);
		bridgeApply.visitVarInsn(ALOAD, 0);
		bridgeApply.visitVarInsn(ALOAD, 1);
		bridgeApply.visitVarInsn(ALOAD, 2);
		String dataTypeInternalName = dataType.getInternalName();
		bridgeApply.visitTypeInsn(CHECKCAST, dataTypeInternalName);
		bridgeApply.visitVarInsn(ALOAD, 3);
		bridgeApply.visitTypeInsn(CHECKCAST, dataTypeInternalName);
		bridgeApply.visitVarInsn(ALOAD, 4);
		bridgeApply.visitTypeInsn(CHECKCAST, dataTypeInternalName);
		bridgeApply.visitMethodInsn(INVOKEVIRTUAL, name, "apply", realApplyDescriptor, false);
		bridgeApply.visitInsn(ARETURN);
		bridgeApply.visitMaxs(0, 0);
		bridgeApply.visitEnd();

		byte[] data = classWriter.toByteArray();
		for (var export : this.exports) {
			if (!export.predicate.test(identifier)) continue;
			String exportName = formatIdentifierForExport(identifier) + ".class";
			try {
				Files.write(Paths.get(export.folder + "/" + exportName), data);
			} catch (IOException exception) {
				LOGGER.error("Failed to export Remolder class '{}' that matched '{}': {}", exportName, export.pattern, exception);
			}
		}
		return ((Class<? extends Remolding<T>>) this.defineClass(name, data, 0, data.length)).getConstructor(Remold.Fields.class).newInstance(fields);
	}

	public record ExportEntry(String folder, String pattern, Predicate<String> predicate) {}
}
