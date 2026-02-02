package com.teamabnormals.blueprint.common.remolder.util;

import com.mojang.datafixers.util.Pair;
import com.teamabnormals.blueprint.common.remolder.data.*;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.LabelNode;

import java.text.ParseException;
import java.util.EnumSet;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.lang.Character.isWhitespace;
import static org.objectweb.asm.Opcodes.*;
import static com.teamabnormals.blueprint.common.remolder.data.DataType.*;
import static com.teamabnormals.blueprint.common.remolder.data.DataVisitors.*;

public final class DataExpression {
	private static final char ESCAPE_CHARACTER = '\\';
	@SuppressWarnings("unchecked")
	private static final Pair<String, DataType<?>>[] AVAILABLE_CASTINGS = new Pair[]{
			Pair.of("int", INT), Pair.of("long", DataType.LONG),
			Pair.of("char", CHAR), Pair.of("byte", BYTE), Pair.of("short", SHORT),
			Pair.of("boolean", BOOLEAN), Pair.of("float", DataType.FLOAT), Pair.of("double", DataType.DOUBLE),
			Pair.of("Integer", INTEGER_WRAPPER), Pair.of("Long", LONG_WRAPPER),
			Pair.of("Character", CHARACTER_WRAPPER), Pair.of("Byte", BYTE_WRAPPER), Pair.of("Short", SHORT_WRAPPER),
			Pair.of("Boolean", BOOLEAN_WRAPPER), Pair.of("Float", FLOAT_WRAPPER), Pair.of("Double", DOUBLE_WRAPPER),
			Pair.of("String", STRING)
	};
	private static final MoldingFunction[] MOLDING_FUNCTIONS = new MoldingFunction[]{
			function("elements", visitor -> molding -> molding.elements(visitor.visit(molding))),
			function("element", DataVisitors::element),
			function("list", visitor -> molding -> molding.listElement(visitor)),
			function("map", visitor -> molding -> molding.mapElement(visitor)),
			function("isList", visitor -> DataVisitors.elementalTest(visitor, Molding::testElementalList)),
			function("isMap", visitor -> DataVisitors.elementalTest(visitor, Molding::testElementalMap)),
			function("isNull", visitor -> DataVisitors.elementalTest(visitor, Molding::testNullElement)),
			function("isString", visitor -> DataVisitors.elementalTest(visitor, Molding::testStringElement)),
			function("isNumerical", visitor -> DataVisitors.elementalTest(visitor, Molding::testNumericalElement)),
			function("isBoolean", visitor -> DataVisitors.elementalTest(visitor, Molding::testBooleanElement)),
			function("size", visitor -> molding -> {
				molding.size(visitor.visit(molding));
				return INT;
			}),
			function("keys", visitor -> molding -> molding.keys(visitor.visit(molding))),
			function("entries", visitor -> molding -> molding.entries(visitor.visit(molding))),
			// TODO: Remove these when actual method calling is implemented
			function("hasNext", visitor -> molding -> {
				visitor.visit(molding);
				molding.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
				return BOOLEAN;
			}),
			function("nextEntry", visitor -> molding -> {
				visitor.visit(molding);
				molding.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
				molding.visitTypeInsn(CHECKCAST, MAP_ENTRY.getInternalName());
				return MAP_ENTRY;
			}),
			function("next", visitor -> molding -> {
				visitor.visit(molding);
				molding.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
				return OBJECT;
			}),
			function("remove", visitor -> molding -> {
				visitor.visit(molding);
				molding.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "remove", "()V", true);
				return VOID;
			}),
			function("getKey", visitor -> molding -> {
				visitor.visit(molding);
				molding.visitMethodInsn(INVOKEINTERFACE, "java/util/Map$Entry", "getKey", "()Ljava/lang/Object;", true);
				molding.visitTypeInsn(CHECKCAST, STRING.getInternalName());
				return STRING;
			}),
			function("getValue", visitor -> molding -> {
				visitor.visit(molding);
				molding.visitMethodInsn(INVOKEINTERFACE, "java/util/Map$Entry", "getValue", "()Ljava/lang/Object;", true);
				molding.visitTypeInsn(CHECKCAST, molding.getDataType().getInternalName());
				return molding.getDataType();
			})
	};
	private final String input;
	private int pos;

	private DataExpression(String input) {
		this.input = input;
	}

	private static MoldingFunction function(String name, Function<DataVisitor, DataVisitor> factory) {
		return new MoldingFunction(name, factory);
	}

	public static DataVisitor parse(String input) throws ParseException {
		return new DataExpression(input).parseExpression();
	}

	private DataVisitor parseExpression() throws ParseException {
		DataVisitor left = this.parsePrimary();
		return this.parseBinaryOp(0, left);
	}

	private void skipWhitespace() {
		String input = this.input;
		int length = input.length();
		while (this.pos < length && isWhitespace(input.charAt(this.pos))) {
			this.pos++;
		}
	}

	private char next() {
		this.skipWhitespace();
		return this.pos < this.input.length() ? this.input.charAt(this.pos) : '\0';
	}

	private void expectAndSkip(char character) throws ParseException {
		if (this.next() != character) throw new ParseException("Expected " + character, this.pos);
		this.pos++;
	}

	private boolean startsWith(String s) {
		return this.input.startsWith(s, this.pos);
	}

	private DataVisitor parsePrimary() throws ParseException {
		String input = this.input;
		int length = input.length();
		while (this.pos < length) {
			char c = input.charAt(this.pos);
			if (isWhitespace(c)) {
				this.pos++;
				continue;
			}

			if (c == '-') {
				int nextIndex = this.pos + 1;
				if (nextIndex >= length) throw new ParseException("Expected value after sign ", this.pos);
				char next = input.charAt(nextIndex);
				if (Character.isDigit(next) || next == '.') {
					return this.parseNumber(true);
				}
				this.pos = nextIndex;
				return negate(this.parsePrimary());
			}

			if (Character.isDigit(c) || c == '.')
				return this.parseNumber(false);

			if (c == '"') {
				return this.parseString(input, length);
			}

			if (this.startsWith("true")) {
				this.pos += 4;
				return TRUE;
			}
			if (this.startsWith("false")) {
				this.pos += 5;
				return FALSE;
			}

			if (c == '!') {
				this.pos++;
				return logicalComplement(this.parsePrimary());
			}

			if (c == '~') {
				this.pos++;
				return integerComplement(this.parsePrimary());
			}

			if (this.startsWith("null")) {
				this.pos += 4;
				return DataVisitors.NULL;
			}

			if (c == '(') {
				this.pos++;
				return this.parseParenOrCast();
			}

			if (c == '\'') {
				return this.parseChar();
			}

			return this.parseIdentifierUsage();
		}
		throw new ParseException("Unexpected expression end", this.pos);
	}

	private DataVisitor parseDouble(String numberString) throws ParseException {
		try {
			return doubleValue(Double.parseDouble(numberString));
		} catch (NumberFormatException e) {
			throw new ParseException("Invalid double literal: " + numberString, this.pos);
		}
	}

	private DataVisitor parseNumber(boolean negated) throws ParseException {
		int start = this.pos;
		boolean hasDot = false;
		boolean hasExp = false;
		String input = this.input;
		int length = input.length();
		if (negated) this.pos++;

		while (this.pos < length) {
			char c = input.charAt(this.pos);
			if (Character.isDigit(c)) {
				this.pos++;
			} else if (c == '.' && !hasDot) {
				hasDot = true;
				this.pos++;
			} else {
				break;
			}
		}

		if (this.pos < length) {
			char c = input.charAt(this.pos);
			if (c == 'e' || c == 'E') {
				hasExp = true;
				this.pos++;

				if (this.pos < length && ((c = input.charAt(this.pos)) == '+' || c == '-'))
					this.pos++;

				if (this.pos >= length || !Character.isDigit(input.charAt(this.pos)))
					throw new ParseException("Invalid exponent in number literal", this.pos);

				while (this.pos < length && Character.isDigit(input.charAt(this.pos))) {
					this.pos++;
				}
			}
		}

		char suffix;
		int numberEndPos = this.pos;
		if (numberEndPos < length) {
			suffix = input.charAt(this.pos);
		} else suffix = '\0';

		String numberString = input.substring(start, numberEndPos);
		switch (suffix) {
			case 'F', 'f':
				this.pos++;
				try {
					return floatValue(Float.parseFloat(numberString));
				} catch (NumberFormatException e) {
					throw new ParseException("Invalid float literal: " + numberString, this.pos);
				}
			case 'D', 'd': {
				this.pos++;
				return this.parseDouble(numberString);
			}
			case 'L', 'l': {
				try {
					this.pos++;
					return longValue(Long.parseLong(numberString));
				} catch (NumberFormatException e) {
					throw new ParseException("Invalid long literal: " + numberString, this.pos);
				}
			}
			default:
				if (hasDot || hasExp) {
					return this.parseDouble(numberString);
				} else {
					try {
						return intValue(Integer.parseInt(numberString));
					} catch (NumberFormatException e) {
						throw new RuntimeException("Invalid integer literal: " + numberString);
					}
				}
		}
	}

	private int parseUnicodeHex(String input, int length, boolean fitIntoChar) throws ParseException {
		// Java allows multiple 'u' characters before the hex digits
		while (this.pos < length && input.charAt(this.pos) == 'u')
			this.pos++;
		// Need exactly 4 hex digits
		if (this.pos + 4 > length)
			throw new ParseException("Incomplete Unicode escape in literal", this.pos);
		int c = 0;
		for (int i = 0; i < 4; i++) {
			char h = input.charAt(this.pos++);
			int v = Character.digit(h, 16);
			if (v < 0)
				throw new ParseException("Invalid hex digit in literal: '" + h + "'", this.pos - 1);
			c = (c << 4) | v;
		}
		return fitIntoChar ? c & 0xFFFF : c;
	}

	private int parseOctal(char esc, String input, int length, boolean fitIntoChar) throws ParseException {
		// Octal escape per JLS:
		// \ OctalDigit
		// \ OctalDigit OctalDigit
		// \ ZeroToThree OctalDigit OctalDigit
		if (esc >= '0' && esc <= '7') {
			int value = esc - '0';
			int maxExtra = (esc <= '3') ? 2 : 1;
			int consumed = 0;
			while (consumed < maxExtra && this.pos < length) {
				char o = input.charAt(this.pos);
				if (o < '0' || o > '7') break;
				value = (value << 3) + (o - '0');
				this.pos++;
				consumed++;
			}
			return fitIntoChar ? value & 0xFF : value;
		} else throw new ParseException("Invalid escape in literal: \\" + esc, this.pos - 1);
	}

	private DataVisitor parseString(String input, int length) throws ParseException {
		this.pos++;
		StringBuilder builder = new StringBuilder();
		while (this.pos < length) {
			char ch = input.charAt(this.pos);
			if (ch == ESCAPE_CHARACTER) {
				if (++this.pos >= length)
					throw new ParseException("Unterminated escape sequence", this.pos);
				char esc = input.charAt(this.pos++);
				switch (esc) {
					case 'b':
						builder.append('\b');
						break;
					case 't':
						builder.append('\t');
						break;
					case 'n':
						builder.append('\n');
						break;
					case 'f':
						builder.append('\f');
						break;
					case 'r':
						builder.append('\r');
						break;
					case '"':
						builder.append('\"');
						break;
					case '\'':
						builder.append('\'');
						break;
					case '\\':
						builder.append('\\');
						break;
					case 'u': {
						builder.append((char) this.parseUnicodeHex(input, length, false));
						break;
					}
					default:
						builder.append((char) this.parseOctal(esc, input, length, false));
				}
				continue;
			}
			if (ch == '"') {
				DataVisitor visitor = string(builder.toString());
				this.pos++;
				return visitor;
			}
			builder.append(ch);
			this.pos++;
		}
		throw new ParseException("Unterminated string literal", this.pos);
	}

	private DataVisitor parseChar() throws ParseException {
		this.pos++;
		String input = this.input;
		int length = input.length();
		if (this.pos >= length)
			throw new ParseException("Unterminated char literal", this.pos);
		char ch = input.charAt(this.pos);
		int code;
		if (ch == ESCAPE_CHARACTER) {
			this.pos++;
			if (this.pos >= length)
				throw new ParseException("Unterminated escape in char literal", this.pos);
			char esc = input.charAt(this.pos++);
			switch (esc) {
				case 'b':
					code = '\b';
					break;
				case 't':
					code = '\t';
					break;
				case 'n':
					code = '\n';
					break;
				case 'f':
					code = '\f';
					break;
				case 'r':
					code = '\r';
					break;
				case '"':
					code = '\"';
					break;
				case '\'':
					code = '\'';
					break;
				case '\\':
					code = '\\';
					break;
				case 'u': {
					code = this.parseUnicodeHex(input, length, true);
					break;
				}
				default:
					code = this.parseOctal(esc, input, length, true);
			}
		} else {
			if (ch == '\r' || ch == '\n')
				throw new ParseException("Line terminator in char literal", this.pos);
			if (ch == '\'')
				throw new ParseException("Empty char literal", this.pos);
			code = ch;
			this.pos++;
		}
		if (this.pos >= length || input.charAt(this.pos) != '\'')
			throw new ParseException("Unterminated or too long char literal", this.pos);
		this.pos++;
		return DataVisitors.charValue((char) code);
	}

	private DataVisitor parseParenOrCast() throws ParseException {
		this.skipWhitespace();
		// Handle casting to the molding's elemental type
		if (this.startsWith("#)")) {
			this.pos += 2;
			var object = this.parsePrimary();
			return molding -> {
				var clazz = object.visit(molding).getClazz();
				if (clazz.isPrimitive())
					throw new UnsupportedOperationException("Can't cast primitive to " + molding.getDataType().getClazz());
				molding.checkThenCast(clazz, molding.getDataType());
				return molding.getDataType();
			};
		}
		// Check if cast
		// For now, hardcode for primitives and wrappers only
		// TODO: Refactor later
		outer:
		for (var casting : AVAILABLE_CASTINGS) {
			int savedPos = this.pos;
			String identifier = casting.getFirst();
			if (this.startsWith(identifier)) {
				this.pos += identifier.length();
				char next = this.next();
				int dimensionality = 0;
				if (next != ')') {
					while (true) {
						if (next == '[') {
							this.pos++;
							next = this.next();
							if (next == ']') {
								dimensionality++;
								this.pos++;
								next = this.next();
							} else {
								this.pos = savedPos;
								break outer;
							}
						} else {
							if (dimensionality == 0) {
								this.pos = savedPos;
								break outer;
							}
							if (next != ')') {
								this.pos = savedPos;
								break outer;
							}
							break;
						}
					}
				}
				this.pos += 1;
				return DataVisitors.cast(
						this.parsePrimary(),
						dimensionality > 0 ?
								DataType.array(casting.getSecond().getClazz(), dimensionality)
								: casting.getSecond()
				);
			}
		}
		DataVisitor expression = this.parseExpression();
		this.expectAndSkip(')');
		return expression;
	}

	private BinaryOp peekBinaryOperator() {
		this.skipWhitespace();
		String input = this.input;
		int pos = this.pos;
		if (pos == input.length()) return null;
		for (BinaryOp binaryOp : BinaryOp.VALUES) {
			if (input.startsWith(binaryOp.identifier, pos)) return binaryOp;
		}
		return null;
	}

	private DataVisitor parseBinaryOp(int precedence, DataVisitor left) throws ParseException {
		while (true) {
			BinaryOp op = this.peekBinaryOperator();
			if (op == null || op.precedence < precedence) break;
			this.pos += op.identifier.length();
			int opPrecedence = op.precedence;
			DataVisitor right = this.parsePrimary();
			while (true) {
				BinaryOp nextOp = this.peekBinaryOperator();
				if (nextOp == null || nextOp.precedence <= opPrecedence) break;
				right = this.parseBinaryOp(nextOp.precedence, right);
			}
			left = new DataVisitors.BinaryOp(op, left, right);
		}
		return left;
	}

	private String parseIdentifier() throws ParseException {
		this.skipWhitespace();
		String input = this.input;
		if (this.pos >= input.length() || !Character.isJavaIdentifierStart(input.charAt(this.pos)))
			throw new ParseException("Expected identifier", this.pos);
		int start = this.pos++;
		while (this.pos < input.length() && Character.isJavaIdentifierPart(input.charAt(this.pos))) {
			this.pos++;
		}
		return input.substring(start, this.pos);
	}

	private DataVisitor parseIdentifierUsage() throws ParseException {
		this.skipWhitespace();
		String input = this.input;
		int length = input.length();
		if (this.pos >= length) throw new ParseException("Unexpected end while parsing variable path", this.pos);
		DataVisitor visitor;
		char c = input.charAt(this.pos);
		if (c == '#') {
			this.pos++;
			// Molding-specific function call
			for (var function : MOLDING_FUNCTIONS) {
				String name = function.name;
				if (!this.startsWith(name)) continue;
				this.pos += name.length();
				this.expectAndSkip('(');
				visitor = function.factory.apply(this.parseExpression());
				this.expectAndSkip(')');
				return visitor;
			}
			throw new ParseException("Unknown molding function usage", this.pos);
		}
		if (c == '$') {
			// Local variable
			this.pos++;
			visitor = new VariableDataVisitor.Local(this.parseIdentifier(), null);
		} else if (c == '@') {
			// Meta variable
			visitor = VariableDataVisitor.META;
			if (++this.pos < length && Character.isJavaIdentifierStart(input.charAt(this.pos))) {
				visitor = new EncapsulatedDataVisitor.Elemental(visitor, DataVisitors.string(this.parseIdentifier()));
			}
		} else if (Character.isJavaIdentifierStart(c)) {
			if (this.startsWith("this")) {
				int end = this.pos + 4;
				if (end == length) {
					// Root
					this.pos = end;
					return VariableDataVisitor.ROOT;
				} else if (!Character.isJavaIdentifierPart(input.charAt(end))) {
					// Root
					this.pos = end;
					visitor = VariableDataVisitor.ROOT;
				} else {
					// Child of root
					visitor = EncapsulatedDataVisitor.Elemental.childOfRoot(this.parseIdentifier());
				}
			} else {
				// Child of root
				visitor = EncapsulatedDataVisitor.Elemental.childOfRoot(this.parseIdentifier());
			}
		} else throw new ParseException("Expected variable path, but found invalid start", this.pos);

		while (true) {
			this.skipWhitespace();
			if (this.pos >= length) break;

			char ch = input.charAt(this.pos);
			if (ch == '.') {
				this.pos++;
				String name = this.parseIdentifier();
				if (visitor instanceof EncapsulatedDataVisitor || (visitor instanceof VariableDataVisitor variable && variable.getReturnType() instanceof ElementType)) {
					visitor = new EncapsulatedDataVisitor.Elemental(visitor, string(name));
				} else {
                    visitor = new VariableDataVisitor.ObjectField(visitor, name);
                }
				continue;
			}

			if (ch == '[') {
				this.pos++;
				this.skipWhitespace();
				DataVisitor index;
				if (this.pos < length && input.charAt(this.pos) == ']') {
					this.pos++;
					index = null;
				} else {
					index = this.parseExpression();
					this.skipWhitespace();
					this.expectAndSkip(']');
				}
				if (visitor instanceof EncapsulatedDataVisitor || (visitor instanceof VariableDataVisitor variable && variable.getReturnType() instanceof ElementType)) {
					visitor = new EncapsulatedDataVisitor.Elemental(visitor, index);
				} else visitor = new VariableDataVisitor.Indexed(visitor, index);
				continue;
			}

			break;
		}

		return visitor;
	}

	// TODO: Maybe do more optimization checks. For now, expect people to be good programmers...
	// TODO: Implement instanceof
	public enum BinaryOp {
		MULTIPLY("*", 11) {
			@Override
			public DataType<?> visit(Molding molding, DataVisitor left, DataVisitor right) throws UnsupportedOperationException {
				return visit(molding, left, right, DMUL, FMUL, LMUL, IMUL);
			}
		},
		DIVIDE("/", 11) {
			@Override
			public DataType<?> visit(Molding molding, DataVisitor left, DataVisitor right) throws UnsupportedOperationException {
				return visit(molding, left, right, DDIV, FDIV, LDIV, IDIV);
			}
		},
		MOD("%", 11) {
			@Override
			public DataType<?> visit(Molding molding, DataVisitor left, DataVisitor right) throws UnsupportedOperationException {
				return visit(molding, left, right, DREM, FREM, LREM, IREM);
			}
		},
		ADD("+", 10) {
			@Override
			public DataType<?> visit(Molding molding, DataVisitor left, DataVisitor right) throws UnsupportedOperationException {
				StringBuilder stringConcatRecipe = new StringBuilder();
				StringBuilder stringConcatDescriptor = new StringBuilder("(");
				DataType<?> type = visit(molding, left, right, stringConcatRecipe, stringConcatDescriptor);
				if (type == STRING) {
					stringConcatDescriptor.append(")Ljava/lang/String;");
					molding.visitInvokeDynamicInsn("makeConcatWithConstants", stringConcatDescriptor.toString(), MAKE_CONCAT_WITH_CONSTANTS, stringConcatRecipe.toString());
				}
				return type;
			}

			private static DataType<?> deepVisit(Molding molding, DataVisitor visitor, StringBuilder recipe, StringBuilder descriptor) {
				if (!(visitor instanceof DataVisitors.BinaryOp(
						BinaryOp op, DataVisitor left, DataVisitor right
				) && op == ADD)) {
					// Base case: Reached first operand in add-chain
					if (visitor instanceof Constant constant && constant.value() instanceof String string) {
						recipe.append(string);
						return STRING;
					}
					var type = visitor.visit(molding);
					if (type == STRING) {
						recipe.append('\u0001');
						descriptor.append("Ljava/lang/String;");
					}
					return type;
				}
				return visit(molding, left, right, recipe, descriptor);
			}

			private static DataType<?> visit(Molding molding, DataVisitor left, DataVisitor right, StringBuilder recipe, StringBuilder descriptor) {
				DataType<?> leftType = deepVisit(molding, left, recipe, descriptor);
				if (leftType == STRING) {
					if (right instanceof Constant constant) {
						recipe.append(constant.value());
					} else {
						concatenate(molding, right.visit(molding), recipe, descriptor);
					}
					return STRING;
				}
				var buffer = molding.createBuffer();
				var rightType = right.visit(buffer);
				if (rightType == STRING) {
					concatenate(molding, leftType, recipe, descriptor);
					buffer.accept(molding);
					return STRING;
				}
				// String concatenation not needed here, so numerically add
				return visit(molding, buffer, leftType, rightType, DADD, FADD, LADD, IADD);
			}

			private static void concatenate(Molding molding, DataType<?> type, StringBuilder recipe, StringBuilder descriptor) {
				recipe.append('\u0001');
				if (type.getClazz().isPrimitive()) {
					descriptor.append(type.getType().getDescriptor());
					return;
				}
				if (type != STRING) {
					try {
						// Try to use specialized string conversion
						molding.toString(type);
					} catch (UnsupportedOperationException unsupported) {
						molding.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;", false);
					}
				}
				descriptor.append("Ljava/lang/String;");
			}
		},
		SUBTRACT("-", 10) {
			@Override
			public DataType<?> visit(Molding molding, DataVisitor left, DataVisitor right) throws UnsupportedOperationException {
				return visit(molding, left, right, DSUB, FSUB, LSUB, ISUB);
			}
		},
		LEFT_SHIFT("<<", 9) {
			@Override
			public DataType<?> visit(Molding molding, DataVisitor left, DataVisitor right) throws UnsupportedOperationException {
				return shift(molding, left, right, ISHL, LSHL);
			}
		},
		RIGHT_SHIFT(">>", 9) {
			@Override
			public DataType<?> visit(Molding molding, DataVisitor left, DataVisitor right) throws UnsupportedOperationException {
				return shift(molding, left, right, ISHR, LSHR);
			}
		},
		UNSIGNED_RIGHT_SHIFT(">>>", 9) {
			@Override
			public DataType<?> visit(Molding molding, DataVisitor left, DataVisitor right) throws UnsupportedOperationException {
				return shift(molding, left, right, IUSHR, LUSHR);
			}
		},
		// We want on-the-fly branch condensation, so this is quite involved...
		// JVM does branch optimization, but we need to minimize initial bytecode like the standard Java compiler
		AND("&&", 3) {
			@Override
			public DataType<?> visit(Molding molding, DataVisitor left, DataVisitor right) throws UnsupportedOperationException {
				Label onFalse = molding.getExitLabel();
				if (onFalse == null) {
					onFalse = new Label();
					onFalse.info = new LabelNode(onFalse);
					Label endLabel = new Label();
					endLabel.info = new LabelNode(endLabel);
					if (isLogicalVisitor(left)) {
						DiscretionaryLabel rightLabel = DiscretionaryLabel.label();
						var buffer = molding.createBuffer(rightLabel, onFalse, false);
						left.visit(buffer);
						buffer.accept(molding);
						if (rightLabel.jumped) {
							molding.visitLabel(rightLabel);
						}
					} else {
						unboxBoolean(left, molding);
						molding.visitJumpInsn(IFEQ, onFalse);
					}
					if (isLogicalVisitor(right)) {
						DiscretionaryLabel onLocallyTrue = DiscretionaryLabel.label();
						var buffer = molding.createBuffer(onLocallyTrue, onFalse, false);
						right.visit(buffer);
						buffer.accept(molding);
						if (onLocallyTrue.jumped) {
							molding.visitLabel(onLocallyTrue);
						}
						molding.visitInsn(molding.isLogicallyComplemented() ? ICONST_0 : ICONST_1);
						molding.visitJumpInsn(GOTO, endLabel);
					} else {
						unboxBoolean(right, molding);
						if (molding.isLogicallyComplemented()) {
							molding.visitJumpInsn(IFEQ, onFalse);
							molding.visitInsn(ICONST_0);
							molding.visitJumpInsn(GOTO, endLabel);
						} else {
							molding.visitJumpInsn(GOTO, endLabel);
						}
					}
					molding.visitLabel(onFalse);
					molding.visitInsn(molding.isLogicallyComplemented() ? ICONST_1 : ICONST_0);
					molding.visitLabel(endLabel);
				} else {
					if (isLogicalVisitor(left)) {
						DiscretionaryLabel rightLabel = DiscretionaryLabel.label();
						var buffer = molding.createBuffer(rightLabel, onFalse, false);
						left.visit(buffer);
						buffer.accept(molding);
						if (rightLabel.jumped) {
							molding.visitLabel(rightLabel);
						}
					} else {
						var buffer = molding.createBuffer();
						unboxBoolean(left, buffer);
						buffer.accept(molding);
						molding.visitJumpInsn(IFEQ, onFalse);
						DiscretionaryLabel.tryToMark(onFalse);
					}
					if (isLogicalVisitor(right)) {
						var pass = molding.pass();
						right.visit(pass);
						pass.accept(molding);
					} else {
						var buffer = molding.createBuffer();
						unboxBoolean(right, buffer);
						buffer.accept(molding);
						Label onTrue = molding.getTrueLabel();
						if (molding.shortCircuitsWithOr() && onTrue != null) {
							molding.visitJumpInsn(IFNE, onTrue);
							DiscretionaryLabel.tryToMark(onTrue);
						} else {
							molding.visitJumpInsn(IFEQ, onFalse);
							DiscretionaryLabel.tryToMark(onFalse);
						}
					}
				}
				return BOOLEAN;
			}
		},
		OR("||", 2) {
			@Override
			public DataType<?> visit(Molding molding, DataVisitor left, DataVisitor right) throws UnsupportedOperationException {
				Label onTrue = molding.getTrueLabel();
				if (onTrue == null) {
					onTrue = new Label();
					onTrue.info = new LabelNode(onTrue);
					Label endLabel = new Label();
					endLabel.info = new LabelNode(endLabel);
					if (isLogicalVisitor(left)) {
						DiscretionaryLabel rightLabel = DiscretionaryLabel.label();
						var buffer = molding.createBuffer(onTrue, rightLabel, true);
						left.visit(buffer);
						buffer.accept(molding);
						if (rightLabel.jumped) {
							molding.visitLabel(rightLabel);
						}
					} else {
						unboxBoolean(left, molding);
						molding.visitJumpInsn(IFNE, onTrue);
					}
					if (isLogicalVisitor(right)) {
						DiscretionaryLabel onLocallyFalse = DiscretionaryLabel.label();
						var buffer = molding.createBuffer(onTrue, onLocallyFalse, true);
						right.visit(buffer);
						buffer.accept(molding);
						if (onLocallyFalse.jumped) {
							molding.visitLabel(onLocallyFalse);
						}
						molding.visitInsn(molding.isLogicallyComplemented() ? ICONST_1 : ICONST_0);
						molding.visitJumpInsn(GOTO, endLabel);
					} else {
						unboxBoolean(right, molding);
						if (molding.isLogicallyComplemented()) {
							molding.visitJumpInsn(IFNE, onTrue);
							molding.visitInsn(ICONST_1);
							molding.visitJumpInsn(GOTO, endLabel);
						} else {
							molding.visitJumpInsn(GOTO, endLabel);
						}
					}
					molding.visitLabel(onTrue);
					molding.visitInsn(molding.isLogicallyComplemented() ? ICONST_0 : ICONST_1);
					molding.visitLabel(endLabel);
				} else {
					if (isLogicalVisitor(left)) {
						DiscretionaryLabel rightLabel = DiscretionaryLabel.label();
						rightLabel.info = new LabelNode(rightLabel);
						var buffer = molding.createBuffer(onTrue, rightLabel, true);
						left.visit(buffer);
						buffer.accept(molding);
						if (rightLabel.jumped) {
							molding.visitLabel(rightLabel);
						}
					} else {
						var buffer = molding.createBuffer();
						unboxBoolean(left, buffer);
						buffer.accept(molding);
						molding.visitJumpInsn(IFNE, onTrue);
						DiscretionaryLabel.tryToMark(onTrue);
					}
					if (isLogicalVisitor(right)) {
						var pass = molding.pass();
						right.visit(pass);
						pass.accept(molding);
					} else {
						var buffer = molding.createBuffer();
						unboxBoolean(right, buffer);
						buffer.accept(molding);
						Label onFalse = molding.getExitLabel();
						if (!molding.shortCircuitsWithOr() && onFalse != null) {
							molding.visitJumpInsn(IFEQ, onFalse);
							DiscretionaryLabel.tryToMark(onFalse);
						} else {
							molding.visitJumpInsn(IFNE, onTrue);
							DiscretionaryLabel.tryToMark(onTrue);
						}
					}
				}
				return BOOLEAN;
			}
		},
		LESS_THAN_OR_EQUAL("<=", 8) {
			@Override
			public DataType<?> visit(Molding molding, DataVisitor left, DataVisitor right) throws UnsupportedOperationException {
				relate(molding, left, right, IFLE, IFGT, DCMPL, DCMPG, FCMPL, FCMPG, IF_ICMPLE, IF_ICMPGT);
				return BOOLEAN;
			}
		},
		LESS_THAN("<", 8) {
			@Override
			public DataType<?> visit(Molding molding, DataVisitor left, DataVisitor right) throws UnsupportedOperationException {
				relate(molding, left, right, IFLT, IFGE, DCMPL, DCMPG, FCMPL, FCMPG, IF_ICMPLT, IF_ICMPGE);
				return BOOLEAN;
			}
		},
		GREATER_THAN_OR_EQUAL(">=", 8) {
			@Override
			public DataType<?> visit(Molding molding, DataVisitor left, DataVisitor right) throws UnsupportedOperationException {
				relate(molding, left, right, IFGE, IFLT, DCMPG, DCMPL, FCMPG, FCMPL, IF_ICMPGE, IF_ICMPLT);
				return BOOLEAN;
			}
		},
		GREATER_THAN(">", 8) {
			@Override
			public DataType<?> visit(Molding molding, DataVisitor left, DataVisitor right) throws UnsupportedOperationException {
				relate(molding, left, right, IFGT, IFLE, DCMPG, DCMPL, FCMPG, FCMPL, IF_ICMPGT, IF_ICMPLE);
				return BOOLEAN;
			}
		},
		EQUALS("==", 7) {
			@Override
			public DataType<?> visit(Molding molding, DataVisitor left, DataVisitor right) throws UnsupportedOperationException {
				DataType<?> leftType;
				DataType<?> rightType;
				Molding buffer = molding.createBuffer();
				Molding secondBuffer = molding.createBuffer();
				boolean leftConstant = false, rightConstant = false;
				if (left == DataVisitors.NULL) {
					leftType = null;
				} else if (left instanceof Constant constant) {
					leftType = constant.type();
					leftConstant = true;
				} else {
					leftType = left.visit(buffer);
				}
				if (right == DataVisitors.NULL) {
					rightType = null;
				} else if (right instanceof Constant constant) {
					rightType = constant.type();
					rightConstant = true;
				} else {
					rightType = right.visit(secondBuffer);
				}
				int opcode;
				boolean compareFirst = false;
				boolean leftOnly = false;
				boolean tautological = false;
				boolean originalNegated = molding.isLogicallyComplemented();
				boolean negated = originalNegated;
				if (leftType == null) {
					opcode = IFNULL;
					if (rightType == null) {
						tautological = true;
					} else if (rightType.getClazz().isPrimitive()) {
						throw new UnsupportedOperationException("Primitive types cannot be compared to null");
					}
					leftOnly = true;
					buffer = secondBuffer;
				} else if (rightType == null) {
					if (leftType.getClazz().isPrimitive())
						throw new UnsupportedOperationException("Primitive types cannot be compared to null");
					leftOnly = true;
					opcode = IFNULL;
				} else if (leftType.isDouble()) {
					opcode = DCMPL;
					compareFirst = true;
					visitLeftAndConvertRight(DataVisitors::convertToDouble, DataVisitors::convertToDouble, buffer, left, leftType, leftConstant, secondBuffer, right, rightType, rightConstant);
				} else if (rightType.isDouble()) {
					opcode = DCMPL;
					compareFirst = true;
					convertLeftAndVisitRight(DataVisitors::convertToDouble, DataVisitors::convertToDouble, buffer, left, leftType, leftConstant, secondBuffer, right, rightType, rightConstant);
				} else if (leftType.isFloat()) {
					opcode = FCMPL;
					compareFirst = true;
					visitLeftAndConvertRight(DataVisitors::convertToFloat, DataVisitors::convertToFloat, buffer, left, leftType, leftConstant, secondBuffer, right, rightType, rightConstant);
				} else if (rightType.isFloat()) {
					opcode = FCMPL;
					compareFirst = true;
					convertLeftAndVisitRight(DataVisitors::convertToFloat, DataVisitors::convertToFloat, buffer, left, leftType, leftConstant, secondBuffer, right, rightType, rightConstant);
				} else if (leftType.isLong()) {
					opcode = LCMP;
					compareFirst = true;
					visitLeftAndConvertRight(DataVisitors::convertToLong, DataVisitors::convertToLong, buffer, left, leftType, leftConstant, secondBuffer, right, rightType, rightConstant);
				} else if (rightType.isLong()) {
					opcode = LCMP;
					compareFirst = true;
					convertLeftAndVisitRight(DataVisitors::convertToLong, DataVisitors::convertToLong, buffer, left, leftType, leftConstant, secondBuffer, right, rightType, rightConstant);
				} else if (leftType.isSmallInteger()) {
					Integer value = null;
					if (leftConstant) {
						value = (int) ((Constant) left).value();
						if (value == 0) {
							opcode = IFEQ;
							leftOnly = true;
							buffer = secondBuffer;
						} else if (value == 1) {
							opcode = IFEQ;
							leftOnly = true;
							negated = !negated;
							buffer = secondBuffer;
						} else {
							opcode = IF_ICMPEQ;
							left.visit(buffer);
							leftType.unbox(buffer);
						}
					} else {
						opcode = IF_ICMPEQ;
						leftType.unbox(buffer);
					}
					if (!rightType.isSmallInteger()) {
						if (rightConstant) {
							convertToInt(right).visit(secondBuffer);
						} else {
							convertToInt(secondBuffer, rightType);
						}
					} else {
						if (rightConstant) {
							if (value != null) {
								int rightValue = (int) ((Constant) right).value();
								tautological = true;
								negated = (value == rightValue) == originalNegated;
							} else {
								value = (int) ((Constant) right).value();
								if (value == 0) {
									opcode = IFEQ;
									leftOnly = true;
								} else if (value == 1) {
									opcode = IFEQ;
									leftOnly = true;
									negated = !negated;
								} else {
									right.visit(secondBuffer).unbox(secondBuffer);
								}
							}
						} else {
							rightType.unbox(secondBuffer);
						}
					}
				} else if (rightType.isSmallInteger()) {
					if (rightConstant) {
						int value = (int) ((Constant) right).value();
						if (value == 0) {
							opcode = IFEQ;
							leftOnly = true;
						} else if (value == 1) {
							opcode = IFEQ;
							leftOnly = true;
							negated = !negated;
						} else {
							opcode = IF_ICMPEQ;
							right.visit(secondBuffer).unbox(secondBuffer);
						}
					} else {
						rightType.unbox(secondBuffer);
						opcode = IF_ICMPEQ;
					}
					if (leftConstant) convertToInt(left).visit(buffer);
					else convertToInt(buffer, leftType);
				} else if (leftType.isBoolean()) { // No optimization for dummies who write (== true) and (== false)
					opcode = IF_ICMPEQ;
					visitLeftAndConvertRight(DataVisitors::convertToBoolean, DataVisitors::convertToBoolean, buffer, left, leftType, leftConstant, secondBuffer, right, rightType, rightConstant);
				} else if (rightType.isBoolean()) {
					opcode = IF_ICMPEQ;
					convertLeftAndVisitRight(DataVisitors::convertToBoolean, DataVisitors::convertToBoolean, buffer, left, leftType, leftConstant, secondBuffer, right, rightType, rightConstant);
				} else {
					// Must be objects
					opcode = IF_ACMPEQ;
				}
				if (!tautological) {
					buffer.accept(molding);
					if (!leftOnly) secondBuffer.accept(molding);
				}
				Label onTrue = molding.getTrueLabel();
				Label onFalse = molding.getExitLabel();
				if (onTrue == null && onFalse == null) {
					if (tautological) {
						molding.visitInsn(negated ? ICONST_0 : ICONST_1);
						return BOOLEAN;
					}
					onFalse = new Label();
					if (compareFirst) {
						molding.visitInsn(opcode);
						molding.visitJumpInsn(negated ? IFEQ : IFNE, onFalse);
					} else {
						molding.visitJumpInsn(negated ? opcode : opcode + 1, onFalse);
					}
					molding.visitInsn(ICONST_1);
					Label endLabel = new Label();
					molding.visitJumpInsn(GOTO, endLabel);
					molding.visitLabel(onFalse);
					molding.visitInsn(ICONST_0);
					molding.visitLabel(endLabel);
					return BOOLEAN;
				} else if (onTrue != null) {
					if (onFalse == null || molding.shortCircuitsWithOr()) {
						if (tautological) {
							if (!negated) {
								molding.visitJumpInsn(GOTO, onTrue);
								DiscretionaryLabel.tryToMark(onTrue);
							}
						} else {
							if (compareFirst) {
								molding.visitInsn(opcode);
								molding.visitJumpInsn(negated ? IFNE : IFEQ, onTrue);
							} else {
								molding.visitJumpInsn(negated ? opcode + 1 : opcode, onTrue);
							}
							DiscretionaryLabel.tryToMark(onTrue);
						}
						return BOOLEAN;
					}
				}
				if (tautological) {
					if (negated) {
						molding.visitJumpInsn(GOTO, onFalse);
						DiscretionaryLabel.tryToMark(onFalse);
					}
				} else {
					if (compareFirst) {
						molding.visitInsn(opcode);
						molding.visitJumpInsn(negated ? IFEQ : IFNE, onFalse);
					} else {
						molding.visitJumpInsn(negated ? opcode : opcode + 1, onFalse);
					}
					DiscretionaryLabel.tryToMark(onFalse);
				}
				return BOOLEAN;
			}
		},
		NOT_EQUALS("!=", 7) {
			@Override
			public DataType<?> visit(Molding molding, DataVisitor left, DataVisitor right) throws UnsupportedOperationException {
				Molding buffer = molding.negate(false);
				EQUALS.visit(buffer, left, right);
				buffer.accept(molding);
				return BOOLEAN;
			}
		},
		BITWISE_AND("&", 6) {
			@Override
			public DataType<?> visit(Molding molding, DataVisitor left, DataVisitor right) throws UnsupportedOperationException {
				return bitwise(molding, left, right, IAND, LAND);
			}
		},
		BITWISE_XOR("^", 5) {
			@Override
			public DataType<?> visit(Molding molding, DataVisitor left, DataVisitor right) throws UnsupportedOperationException {
				return bitwise(molding, left, right, IXOR, LXOR);
			}
		},
		BITWISE_OR("|", 4) {
			@Override
			public DataType<?> visit(Molding molding, DataVisitor left, DataVisitor right) throws UnsupportedOperationException {
				return bitwise(molding, left, right, IOR, LOR);
			}
		};

		private static final BinaryOp[] VALUES = values();
		public static final EnumSet<BinaryOp> LOGICAL_OPERATORS = EnumSet.of(AND, OR, EQUALS, NOT_EQUALS, LESS_THAN, GREATER_THAN, LESS_THAN_OR_EQUAL, GREATER_THAN_OR_EQUAL);
		private final String identifier;
		private final int precedence;

		BinaryOp(String identifier, int precedence) {
			this.identifier = identifier;
			this.precedence = precedence;
		}

		protected static DataType<?> visit(Molding molding, DataVisitor left, DataVisitor right, int doubleOpcode, int floatOpcode, int longOpcode, int intOpcode) {
			var leftType = left.visit(molding);
			var buffer = molding.createBuffer();
			var rightType = right.visit(buffer);
			return visit(molding, buffer, leftType, rightType, doubleOpcode, floatOpcode, longOpcode, intOpcode);
		}

		protected static DataType<?> visit(Molding molding, Molding buffer, DataType<?> leftType, DataType<?> rightType, int doubleOpcode, int floatOpcode, int longOpcode, int intOpcode) {
			int opcode;
			DataType<?> type;
			if (leftType.isDouble()) {
				leftType.unbox(molding);
				if (rightType != DataType.DOUBLE)
					convertToDouble(buffer, rightType);
				opcode = doubleOpcode;
				type = DataType.DOUBLE;
			} else if (rightType.isDouble()) {
				rightType.unbox(buffer);
				convertToDouble(molding, leftType);
				opcode = doubleOpcode;
				type = DataType.DOUBLE;
			} else if (leftType.isFloat()) {
				leftType.unbox(molding);
				if (rightType != DataType.FLOAT)
					convertToFloat(buffer, rightType);
				opcode = floatOpcode;
				type = DataType.FLOAT;
			} else if (rightType.isFloat()) {
				rightType.unbox(buffer);
				convertToFloat(molding, leftType);
				opcode = floatOpcode;
				type = DataType.FLOAT;
			} else if (leftType.isLong()) {
				leftType.unbox(molding);
				if (rightType != DataType.LONG)
					convertToLong(buffer, rightType);
				opcode = longOpcode;
				type = DataType.LONG;
			} else if (rightType.isLong()) {
				rightType.unbox(buffer);
				convertToLong(molding, leftType);
				opcode = longOpcode;
				type = DataType.LONG;
			} else {
				if (!leftType.isSmallInteger())
					convertToInt(molding, leftType);
				if (!rightType.isSmallInteger())
					convertToInt(buffer, rightType);
				opcode = intOpcode;
				type = INT;
			}
			buffer.accept(molding);
			molding.visitInsn(opcode);
			return type;
		}

		private static DataType<?> shift(Molding molding, DataVisitor left, DataVisitor right, int intOpcode, int longOpcode) {
			var leftType = left.visit(molding);
			int opcode;
			DataType<?> type;
			if (leftType.isSmallInteger()) {
				opcode = intOpcode;
				type = INT;
			} else if (leftType.isLong()) {
				opcode = longOpcode;
				type = DataType.LONG;
			} else throw new UnsupportedOperationException("Cannot shift on type: " + leftType);
			leftType.unbox(molding);
			if (right instanceof Constant constant) {
				if (!constant.type().isSmallInteger()) convertToInt(right).visit(molding);
				else right.visit(molding).unbox(molding);
			} else {
				var rightType = right.visit(molding);
				if (!rightType.isSmallInteger()) convertToInt(molding, rightType);
				else rightType.unbox(molding);
			}
			molding.visitInsn(opcode);
			return type;
		}

		private static DataType<?> bitwise(Molding molding, DataVisitor left, DataVisitor right, int intOpcode, int longOpcode) {
			var leftType = left.visit(molding);
			var buffer = molding.createBuffer();
			var rightType = right.visit(buffer);
			boolean leftBoolean = leftType.isBoolean();
			if (leftBoolean != rightType.isBoolean())
				throw new UnsupportedOperationException("Bitwise operation cannot use only one boolean operand");
			if (leftBoolean) {
				leftType.unbox(molding);
				buffer.accept(molding);
				rightType.unbox(molding);
				molding.visitInsn(intOpcode);
				return BOOLEAN;
			} else if (leftType.isSmallInteger() && rightType.isSmallInteger()) {
				leftType.unbox(molding);
				buffer.accept(molding);
				rightType.unbox(molding);
				molding.visitInsn(intOpcode);
				return INT;
			} else if (leftType.isLong() || rightType.isLong()) {
				if (!leftType.isLong()) convertToLong(molding, leftType);
				else leftType.unbox(molding);
				if (!rightType.isLong()) convertToLong(buffer, rightType);
				else rightType.unbox(buffer);
				buffer.accept(molding);
				molding.visitInsn(longOpcode);
				return DataType.LONG;
			}
			throw new UnsupportedOperationException("Bitwise operation must be of integers or booleans");
		}

		private static void relate(Molding molding, DataVisitor left, DataVisitor right, int trueOpcode, int falseOpcode, int doubleCmpl, int doubleCmpg, int floatCmpl, int floatCmpg, int trueIntOpcode, int falseIntOpcode) {
			DataType<?> leftType;
			DataType<?> rightType;
			Molding buffer = molding.createBuffer();
			Molding secondBuffer = molding.createBuffer();
			boolean leftConstant = false, rightConstant = false;
			if (left instanceof Constant constant) {
				leftType = constant.type();
				leftConstant = true;
			} else {
				leftType = left.visit(buffer);
			}
			if (right instanceof Constant constant) {
				rightType = constant.type();
				rightConstant = true;
			} else {
				rightType = right.visit(secondBuffer);
			}
			int opcode;
			boolean compareFirst = false;
			boolean leftOnly = false;
			boolean negated = molding.isLogicallyComplemented();
			if (leftType.isDouble()) {
				opcode = negated ? doubleCmpl : doubleCmpg;
				compareFirst = true;
				visitLeftAndConvertRight(DataVisitors::convertToDouble, DataVisitors::convertToDouble, buffer, left, leftType, leftConstant, secondBuffer, right, rightType, rightConstant);
			} else if (rightType.isDouble()) {
				opcode = negated ? doubleCmpl : doubleCmpg;
				compareFirst = true;
				convertLeftAndVisitRight(DataVisitors::convertToDouble, DataVisitors::convertToDouble, buffer, left, leftType, leftConstant, secondBuffer, right, rightType, rightConstant);
			} else if (leftType.isFloat()) {
				opcode = negated ? floatCmpl : floatCmpg;
				compareFirst = true;
				visitLeftAndConvertRight(DataVisitors::convertToFloat, DataVisitors::convertToFloat, buffer, left, leftType, leftConstant, secondBuffer, right, rightType, rightConstant);
			} else if (rightType.isFloat()) {
				opcode = negated ? floatCmpl : floatCmpg;
				compareFirst = true;
				convertLeftAndVisitRight(DataVisitors::convertToFloat, DataVisitors::convertToFloat, buffer, left, leftType, leftConstant, secondBuffer, right, rightType, rightConstant);
			} else if (leftType.isLong()) {
				opcode = LCMP;
				compareFirst = true;
				visitLeftAndConvertRight(DataVisitors::convertToLong, DataVisitors::convertToLong, buffer, left, leftType, leftConstant, secondBuffer, right, rightType, rightConstant);
			} else if (rightType.isLong()) {
				opcode = LCMP;
				compareFirst = true;
				convertLeftAndVisitRight(DataVisitors::convertToLong, DataVisitors::convertToLong, buffer, left, leftType, leftConstant, secondBuffer, right, rightType, rightConstant);
			} else if (leftType.isSmallInteger()) {
				opcode = trueIntOpcode;
				if (leftConstant) {
					left.visit(buffer).unbox(buffer);
				} else {
					leftType.unbox(buffer);
				}
				if (!rightType.isSmallInteger()) {
					if (rightConstant) convertToInt(right).visit(secondBuffer);
					else convertToInt(secondBuffer, rightType);
				} else {
					if (rightConstant) {
						right.visit(secondBuffer).unbox(secondBuffer);
					} else {
						rightType.unbox(secondBuffer);
					}
				}
			} else if (rightType.isSmallInteger()) {
				opcode = trueIntOpcode;
				if (rightConstant) right.visit(secondBuffer).unbox(secondBuffer);
				else rightType.unbox(secondBuffer);
				if (leftConstant) convertToInt(left).visit(buffer);
				else convertToInt(buffer, leftType);
			} else {
				// Must be non-numeric
				throw new UnsupportedOperationException("Relational operators are not supported for non-numerical types");
			}
			buffer.accept(molding);
			if (!leftOnly) secondBuffer.accept(molding);
			Label onTrue = molding.getTrueLabel();
			Label onFalse = molding.getExitLabel();
			if (onTrue == null && onFalse == null) {
				onFalse = new Label();
				if (compareFirst) {
					molding.visitInsn(opcode);
					molding.visitJumpInsn(negated ? trueOpcode : falseOpcode, onFalse);
				} else {
					molding.visitJumpInsn(negated ? opcode : falseIntOpcode, onFalse);
				}
				molding.visitInsn(ICONST_1);
				Label endLabel = new Label();
				molding.visitJumpInsn(GOTO, endLabel);
				molding.visitLabel(onFalse);
				molding.visitInsn(ICONST_0);
				molding.visitLabel(endLabel);
				return;
			} else if (onTrue != null && (onFalse == null || molding.shortCircuitsWithOr())) {
				if (compareFirst) {
					molding.visitInsn(opcode);
					molding.visitJumpInsn(negated ? falseOpcode : trueOpcode, onTrue);
				} else {
					molding.visitJumpInsn(negated ? falseIntOpcode : opcode, onTrue);
				}
				DiscretionaryLabel.tryToMark(onTrue);
				return;
			}
			if (compareFirst) {
				molding.visitInsn(opcode);
				molding.visitJumpInsn(negated ? trueOpcode : falseOpcode, onFalse);
			} else {
				molding.visitJumpInsn(negated ? opcode : falseIntOpcode, onFalse);
			}
			DiscretionaryLabel.tryToMark(onFalse);
		}

		private static void visitLeftAndConvertRight(Function<DataVisitor, DataVisitor> constantConverter, BiConsumer<Molding, DataType<?>> converter, Molding leftBuffer, DataVisitor left, DataType<?> leftType, boolean leftConstant, Molding rightBuffer, DataVisitor right, DataType<?> rightType, boolean rightConstant) {
			if (leftConstant) left.visit(leftBuffer);
			leftType.unbox(leftBuffer);
			if (!rightType.isDouble()) {
				if (rightConstant) {
					constantConverter.apply(right).visit(rightBuffer);
				} else {
					converter.accept(rightBuffer, rightType);
				}
			} else {
				if (rightConstant) right.visit(rightBuffer);
				rightType.unbox(rightBuffer);
			}
		}

		private static void convertLeftAndVisitRight(Function<DataVisitor, DataVisitor> constantConverter, BiConsumer<Molding, DataType<?>> converter, Molding leftBuffer, DataVisitor left, DataType<?> leftType, boolean leftConstant, Molding rightBuffer, DataVisitor right, DataType<?> rightType, boolean rightConstant) {
			if (leftConstant) {
				constantConverter.apply(left).visit(leftBuffer);
			} else {
				converter.accept(leftBuffer, leftType);
			}
			if (rightConstant) right.visit(rightBuffer);
			rightType.unbox(rightBuffer);
		}

		public abstract DataType<?> visit(Molding molding, DataVisitor left, DataVisitor right) throws UnsupportedOperationException;
	}

	public record MoldingFunction(String name, Function<DataVisitor, DataVisitor> factory) {}
}
