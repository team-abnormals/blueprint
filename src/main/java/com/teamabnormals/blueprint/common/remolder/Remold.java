package com.teamabnormals.blueprint.common.remolder;

import com.teamabnormals.blueprint.common.remolder.data.Molding;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Record class for storing bytecode-related information used to construct new {@link Remolding} classes and instances.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record Remold(String type, Visitor visitor, Fields fields) {
	/**
	 * The interface for describing a data-relative function that visits an ASM method.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public interface Visitor {
		void visit(Molding<?> molding, String owner, MethodVisitor method) throws Exception;
	}

	/**
	 * Record class for storing bytecode-related information used to create fields as storage in {@link Remolding} instances.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public record Field(Supplier<?> parameter, String parameterType, String descriptor,
						BiConsumer<Molding<?>, MethodVisitor> visitor) {}

	/**
	 * The class for handling the setup of {@link Field} instances in {@link Remolding} instances.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	public static final class Fields {
		private final HashMap<String, Field> map;

		public Fields() {
			this(new HashMap<>());
		}

		public Fields(HashMap<String, Field> map) {
			this.map = map;
		}

		public void addField(String name, Field field) {
			var map = this.map;
			if (map.containsKey(name)) throw new RuntimeException("Duplicate field name: " + name);
			map.put(name, field);
		}

		public void addFields(Fields fields) {
			for (var entry : fields.map.entrySet()) this.addField(entry.getKey(), entry.getValue());
		}

		public Object getParameterValue(String name) {
			return this.map.get(name).parameter().get();
		}

		public void visitFields(String owner, Molding<?> molding, ClassVisitor classVisitor, MethodVisitor constructorVisitor) {
			for (var entry : this.map.entrySet()) {
				String name = entry.getKey();
				Field field = entry.getValue();
				String descriptor = field.descriptor();
				// Add field to class
				classVisitor.visitField(Opcodes.ACC_PRIVATE, name, descriptor, null, null);
				// Load 'this' and fields
				constructorVisitor.visitVarInsn(Opcodes.ALOAD, 0);
				constructorVisitor.visitVarInsn(Opcodes.ALOAD, 1);
				// Get parameter value from map
				constructorVisitor.visitLdcInsn(name);
				constructorVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/teamabnormals/blueprint/common/remolder/Remold$Fields", "getParameterValue", "(Ljava/lang/String;)Ljava/lang/Object;", false);
				// Cast to parameter type
				constructorVisitor.visitTypeInsn(Opcodes.CHECKCAST, field.parameterType());
				// Map parameter value to field value if needed
				field.visitor().accept(molding, constructorVisitor);
				// Put field
				constructorVisitor.visitFieldInsn(Opcodes.PUTFIELD, owner, name, descriptor);
			}
		}
	}
}
