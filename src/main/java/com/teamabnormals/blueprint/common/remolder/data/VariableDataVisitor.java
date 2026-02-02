package com.teamabnormals.blueprint.common.remolder.data;

import com.teamabnormals.blueprint.common.remolder.util.JavaIdentifierUtil;
import org.objectweb.asm.Type;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Implementation of {@link DataVisitor} for variables.
 *
 * @author SmellyModder (Luke Tonon)
 */
public interface VariableDataVisitor extends DataVisitor {
	Local OPS = new Local("ops", DataType.DYNAMIC_OPS);
	Local THIS = new Local("this", DataType.REMOLDING);
	Local ROOT = new Local("root", ElementType.ELEMENTAL);
	Local META = new Local("meta", ElementType.ELEMENTAL);

	void set(Molding molding, DataVisitor value);

	ReturnType getReturnType();

	/**
	 * Implementation of {@link VariableDataVisitor} for local variables.
	 *
	 * @author SmellyModder (Luke Tonon)
	 */
	record Local(String name, @Nullable ReturnType type) implements VariableDataVisitor {
		public void allocate(Molding molding) {
			molding.allocateLocalVar(this.name, this.type.getDataType(molding));
		}

		@Override
		public DataType<?> visit(Molding molding) {
			var localVar = molding.getLocalVar(this.name);
			DataType<?> actualType = localVar.getFirst();
			var returnType = this.type;
			if (returnType != null) {
				DataType<?> type = returnType.getDataType(molding);
				if (!type.getClazz().isAssignableFrom(actualType.getClazz())) {
					throw new UnsupportedOperationException(
							"Local variable with name " + this.name
									+ " of type " + actualType
									+ " cannot be loaded as type " + type
					);
				}
			}
			molding.visitVarInsn(actualType.getType().getOpcode(ILOAD), localVar.getSecond());
			return actualType;
		}

		@Override
		public void set(Molding molding, DataVisitor value) {
			molding.storeLocalVar(this.name, value, this.type != null);
		}

		@Override
		public ReturnType getReturnType() {
			return this.type;
		}
	}

	// TODO: Possibly recover generic type information when possible
	record ObjectField(DataVisitor owner, String name) implements VariableDataVisitor {
		@Override
		public void set(Molding molding, DataVisitor value) {
			var ownerType = this.owner.visit(molding);
			var clazz = ownerType.getClazz();
			if (clazz.isArray()) {
				throw new UnsupportedOperationException("Cannot assign a field of an array");
			} else if (clazz.isPrimitive()) {
				throw new UnsupportedOperationException("Cannot assign to a field belonging to a primitive type");
			}
			String name = this.name;
			Field field = JavaIdentifierUtil.findField(clazz, name);
			checkField(field, name, ownerType);
			var fieldType = field.getType();
			if (value == DataVisitors.NULL) {
				if (fieldType.isPrimitive())
					throw new UnsupportedOperationException("Value to assign to primitive field " + name + " must be non-null");
				value.visit(molding);
			} else {
				var convertedVisitor = DataVisitors.tryToConvert(value, fieldType);
				if (convertedVisitor == null) {
					var valueType = value.visit(molding);
					if (!fieldType.isAssignableFrom(valueType.getClazz()))
						throw new UnsupportedOperationException("Value to assign to field " + name + " must be an instance of " + fieldType);
				} else convertedVisitor.visit(molding);
			}
			molding.visitFieldInsn(PUTFIELD, Type.getInternalName(field.getDeclaringClass()), field.getName(), Type.getDescriptor(fieldType));
		}

		@Override
		public DataType<?> visit(Molding molding) {
			var ownerType = this.owner.visit(molding);
			var clazz = ownerType.getClazz();
			String name = this.name;
			if (clazz.isArray()) {
				if (name.equals("length")) {
					molding.visitInsn(ARRAYLENGTH);
					return DataType.INT;
				}
				throw new UnsupportedOperationException("Cannot get a field of an array besides length");
			} else if (clazz.isPrimitive()) {
				throw new UnsupportedOperationException("Cannot get a field belonging to a primitive type");
			}
			Field field = JavaIdentifierUtil.findField(clazz, name);
			checkField(field, name, ownerType);
			molding.visitFieldInsn(GETFIELD, Type.getInternalName(field.getDeclaringClass()), field.getName(), Type.getDescriptor(field.getType()));
			return DataType.type(field.getType());
		}

		@Override
		public ReturnType getReturnType() {
			return molding -> {
				var ownerType = this.owner.visit(molding.createBuffer());
				String name = this.name;
				Field field = JavaIdentifierUtil.findField(ownerType.getClazz(), name);
				checkField(field, name, ownerType);
				return DataType.type(field.getType());
			};
		}

		private static void checkField(@Nullable Field field, String name, DataType<?> ownerType) {
			if (field == null)
				throw new IllegalArgumentException("No field with name " + name + " found in type " + ownerType);
			if (Modifier.isStatic(field.getModifiers()))
				throw new IllegalArgumentException("ObjectField only supports instance fields (found static " + field + ")");
		}
	}

	record Indexed(DataVisitor owner, @Nullable DataVisitor index) implements VariableDataVisitor {
		@Override
		public void set(Molding molding, DataVisitor value) {
			var index = this.index;
			// Use Molding's special set operation if possible
			try {
				var buffer = molding.createBuffer();
				buffer.set(this.owner, index, value, true);
				buffer.accept(molding);
				return;
			} catch (UnsupportedOperationException unsupported) {
			}
            Class<?> componentType = this.processOwnerComponentType(molding, index);
            if (value == DataVisitors.NULL) {
				if (componentType.isPrimitive())
					throw new UnsupportedOperationException("Value to assign at index of primitive array must be non-null");
				value.visit(molding);
			} else {
				var convertedValue = DataVisitors.tryToConvert(value, componentType);
				if (convertedValue != null) {
					convertedValue.visit(molding);
				} else if (!componentType.isAssignableFrom(value.visit(molding).getClazz()))
					throw new UnsupportedOperationException("Value to assign at array index must be an instance of " + componentType);
			}
			molding.visitInsn(Type.getType(componentType).getOpcode(IASTORE));
		}

        @Override
		public DataType<?> visit(Molding molding) {
			var index = this.index;
			try {
				var buffer = molding.createBuffer();
				buffer.get(this.owner, index);
				buffer.accept(molding);
				return molding.getDataType();
			} catch (UnsupportedOperationException ignored) {
			}
            var componentDataType = DataType.type(this.processOwnerComponentType(molding, index));
			molding.visitInsn(componentDataType.getType().getOpcode(IALOAD));
			return componentDataType;
		}

		@Override
		public ReturnType getReturnType() {
			return molding -> {
				try {
					molding.createBuffer().get(this.owner, this.index);
					return molding.getDataType();
				} catch (UnsupportedOperationException ignored) {
				}
				var componentType = this.owner.visit(molding.createBuffer()).getClazz().componentType();
				if (componentType == null)
					throw new UnsupportedOperationException("Cannot get at index if owner is not array-like");
				return DataType.type(componentType);
			};
		}

        private Class<?> processOwnerComponentType(Molding molding, DataVisitor index) {
            var componentType = this.owner.visit(molding).getClazz().componentType();
            if (componentType == null)
                throw new UnsupportedOperationException("Cannot visit at index if owner is not array-like");
            if (index == null) {
                molding.visitInsn(DUP);
                molding.visitInsn(ARRAYLENGTH);
                molding.visitInsn(ICONST_1);
                molding.visitInsn(ISUB);
            } else {
                var indexType = index.visit(molding);
                if (!indexType.isSmallInteger())
                    throw new UnsupportedOperationException("Cannot visit at index if index is not string or integer type");
                indexType.unbox(molding);
            }
            return componentType;
        }
	}

	record Provided<T>(String name, DataType<T> type, T initial) implements VariableDataVisitor {
		@Override
		public DataType<?> visit(Molding molding) {
			molding.getFieldOwnerVariable().visit(molding);
			molding.visitFieldInsn(GETFIELD, molding.getFieldOwnerTypeName(), this.name, this.type.getType().getDescriptor());
			return this.type;
		}

		@Override
		public void set(Molding molding, DataVisitor value) {
			molding.getFieldOwnerVariable().visit(molding);
			value.visit(molding);
			molding.visitFieldInsn(PUTFIELD, molding.getFieldOwnerTypeName(), this.name, this.type.getType().getDescriptor());
		}

		@Override
		public DataType<?> getReturnType() {
			return this.type;
		}
	}
}
