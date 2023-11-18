package com.teamabnormals.blueprint.common.remolder;

import com.teamabnormals.blueprint.common.remolder.data.Molding;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * A {@link ClassLoader} subclass that compiles {@link Remold} instances into {@link Remolder} instances.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class RemoldingCompiler extends ClassLoader {

	public RemoldingCompiler(ClassLoader parent) {
		super(parent);
	}

	public <T> Remolding<T> compile(String identifier, Molding<T> molding, Remold... remolds) throws Exception {
		return this.compile(remolds[0].type(), identifier, molding, remolds);
	}

	@SuppressWarnings("unchecked")
	public <T> Remolding<T> compile(String type, String identifier, Molding<T> molding, Remold... remolds) throws Exception {
		int remoldsLength = remolds.length;
		if (remoldsLength == 0) throw new IllegalArgumentException("Cannot compile an empty array of Remolds");
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		String name = type + "Remolding" + System.nanoTime();
		Type dataType = molding.getDataType();
		String typeDescriptor = dataType.getDescriptor();
		classWriter.visit(
				Opcodes.V17,
				Opcodes.ACC_PUBLIC,
				name,
				"L" + name + "<" + typeDescriptor + ">;Lcom/teamabnormals/blueprint/common/remolder/Remolding<" + typeDescriptor + ">;",
				"java/lang/Object",
				new String[]{"com/teamabnormals/blueprint/common/remolder/Remolding"}
		);
		MethodVisitor constructor = classWriter.visitMethod(
				Opcodes.ACC_PUBLIC,
				"<init>",
				"(Lcom/teamabnormals/blueprint/common/remolder/Remold$Fields;)V",
				null,
				null
		);
		constructor.visitVarInsn(Opcodes.ALOAD, 0);
		constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		Remold.Fields fields = new Remold.Fields();
		for (Remold remold : remolds) fields.addFields(remold.fields());
		fields.visitFields(name, molding, classWriter, constructor);
		constructor.visitInsn(Opcodes.RETURN);
		constructor.visitMaxs(0, 0);
		constructor.visitEnd();

		MethodVisitor toString = classWriter.visitMethod(
				Opcodes.ACC_PUBLIC,
				"toString",
				"()Ljava/lang/String;",
				null,
				null
		);
		toString.visitLdcInsn("'" + identifier + " (" + name + ")'");
		toString.visitInsn(Opcodes.ARETURN);
		toString.visitMaxs(0, 0);
		toString.visitEnd();

		String realApplyDescriptor = "(Lcom/mojang/serialization/DynamicOps;" + typeDescriptor + typeDescriptor + typeDescriptor + ")Lcom/mojang/datafixers/util/Pair;";
		String applySignature = "(Lcom/mojang/serialization/DynamicOps<" + typeDescriptor + ">;" + typeDescriptor + typeDescriptor + typeDescriptor + ")" + "Lcom/mojang/datafixers/util/Pair<" + typeDescriptor + typeDescriptor + ">;";
		MethodVisitor apply = classWriter.visitMethod(
				Opcodes.ACC_PUBLIC,
				"apply",
				realApplyDescriptor,
				applySignature,
				null
		);
		for (Remold remold : remolds) remold.visitor().visit(molding, name, apply);
		apply.visitVarInsn(Opcodes.ALOAD, 2);
		apply.visitVarInsn(Opcodes.ALOAD, 3);
		apply.visitMethodInsn(Opcodes.INVOKESTATIC, "com/mojang/datafixers/util/Pair", "of", "(Ljava/lang/Object;Ljava/lang/Object;)Lcom/mojang/datafixers/util/Pair;", false);
		apply.visitInsn(Opcodes.ARETURN);
		apply.visitMaxs(0, 0);
		apply.visitEnd();

		MethodVisitor bridgeApply = classWriter.visitMethod(
				Opcodes.ACC_PUBLIC | Opcodes.ACC_BRIDGE | Opcodes.ACC_SYNTHETIC,
				"apply",
				"(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/mojang/datafixers/util/Pair;",
				null,
				null
		);
		bridgeApply.visitVarInsn(Opcodes.ALOAD, 0);
		bridgeApply.visitVarInsn(Opcodes.ALOAD, 1);
		bridgeApply.visitVarInsn(Opcodes.ALOAD, 2);
		String dataTypeInternalName = dataType.getInternalName();
		bridgeApply.visitTypeInsn(Opcodes.CHECKCAST, dataTypeInternalName);
		bridgeApply.visitVarInsn(Opcodes.ALOAD, 3);
		bridgeApply.visitTypeInsn(Opcodes.CHECKCAST, dataTypeInternalName);
		bridgeApply.visitVarInsn(Opcodes.ALOAD, 4);
		bridgeApply.visitTypeInsn(Opcodes.CHECKCAST, dataTypeInternalName);
		bridgeApply.visitMethodInsn(Opcodes.INVOKEVIRTUAL, name, "apply", realApplyDescriptor, false);
		bridgeApply.visitInsn(Opcodes.ARETURN);
		bridgeApply.visitMaxs(0, 0);
		bridgeApply.visitEnd();

		byte[] data = classWriter.toByteArray();
		// TODO: Possibly add an option to export the generated classes to make debugging easier
		return ((Class<? extends Remolding<T>>) this.defineClass(name, data, 0, data.length)).getConstructor(Remold.Fields.class).newInstance(fields);
	}

}
