package com.teamabnormals.blueprint.common.remolder.data;

import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.teamabnormals.blueprint.common.remolder.data.DataVisitors.*;

/**
 * The class for parsing {@link DataVisitor} instances from string expressions.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class DataExpressionParser {
	private static final HashMap<String, FunctionParser> FUNCTIONS = new HashMap<>();

	static {
		registerFunction("get", (instance, tokens, index) -> {
			if (instance == null)
				throw new ParseException("Must have instance to get field value from", tokens[index.get() - 1].startIndex() + 1);
			int i = index.get();
			int tokensLength = tokens.length;
			if ((i == tokensLength - 1 && tokens[i].type() == TokenType.RIGHT_PARENTHESIS) || i + 1 > tokensLength)
				throw new ParseException("Missing parameter", tokens[i - 1].startIndex() + 1);
			return new EncapsulatedDataVisitor.Elemental(instance, parseTokens(tokens, index, TokenType.RIGHT_PARENTHESIS));
		});
		registerSingleParameterInstanceFunction("size", (instance, tokens, index) -> {
			return instance.then(molding -> molding.size(instance.getReturnType().getDataType(molding)), DataType.INT);
		});
		registerSingleParameterFunction("element", DataVisitors::element);
		registerSingleParameterFunction("str", DataVisitors::str);
		registerSingleParameterFunction("boolean", DataVisitors::convertToBoolean);
		registerSingleParameterFunction("Boolean", DataVisitors::convertToBooleanWrapper);
		registerSingleParameterFunction("char", DataVisitors::convertToChar);
		registerSingleParameterFunction("Character", DataVisitors::convertToCharWrapper);
		registerSingleParameterFunction("byte", DataVisitors::convertToByte);
		registerSingleParameterFunction("Byte", DataVisitors::convertToByteWrapper);
		registerSingleParameterFunction("short", DataVisitors::convertToShort);
		registerSingleParameterFunction("Short", DataVisitors::convertToShortWrapper);
		registerSingleParameterFunction("int", DataVisitors::convertToInt);
		registerSingleParameterFunction("Integer", DataVisitors::convertToIntWrapper);
		registerSingleParameterFunction("long", DataVisitors::convertToLong);
		registerSingleParameterFunction("Long", DataVisitors::convertToLongWrapper);
		registerSingleParameterFunction("float", DataVisitors::convertToFloat);
		registerSingleParameterFunction("Float", DataVisitors::convertToFloatWrapper);
		registerSingleParameterFunction("double", DataVisitors::convertToDouble);
		registerSingleParameterFunction("Double", DataVisitors::convertToDoubleWrapper);
	}

	public static synchronized void registerFunction(String name, FunctionParser parser) {
		FUNCTIONS.put(name, parser);
	}

	public static void registerSingleParameterInstanceFunction(String name, FunctionParser parser) {
		registerFunction(name, (instance, tokens, index) -> {
			if (instance == null)
				throw new ParseException("Must have instance to invoke " + name + "()", tokens[index.get() - 1].startIndex() + 1);
			checkTokenExists(TokenType.RIGHT_PARENTHESIS, index, tokens);
			return parser.parse(instance, tokens, index);
		});
	}

	public static void registerSingleParameterFunction(String name, Function<DataVisitor, DataVisitor> function) {
		registerFunction(name, (instance, tokens, index) -> {
			int i = index.get();
			if (instance != null)
				throw new ParseException("Cannot invoke " + name + "() on an instance", tokens[i - 1].startIndex() + 1);
			int tokensLength = tokens.length;
			if ((i == tokensLength - 1 && tokens[i].type() == TokenType.RIGHT_PARENTHESIS) || i + 1 > tokensLength)
				throw new ParseException("Missing parameter", tokens[i - 1].startIndex() + 1);
			return function.apply(parseTokens(tokens, index, TokenType.RIGHT_PARENTHESIS));
		});
	}

	public static Token[] tokenize(String expression) throws ParseException {
		int initialLength = expression.length();
		if (initialLength == 0) return new Token[0];
		TokenType[] tokenTypes = TokenType.values();
		int tokenTypesCount = tokenTypes.length;
		ArrayList<Token> tokens = new ArrayList<>();
		for (int i = 0; i < tokenTypesCount; ) {
			TokenType type = tokenTypes[i];
			Matcher matcher = type.pattern.matcher(expression);
			if (matcher.find() && matcher.start() == 0) {
				i = 0;
				int end = matcher.end();
				String tokenContents = expression.substring(0, end);
				tokens.add(new Token(type, tokenContents, initialLength - expression.length()));
				if ((expression = expression.substring(end)).isEmpty()) return tokens.toArray(Token[]::new);
			} else i++;
		}
		throw new ParseException("Unknown token(s) for remaining characters: " + expression, initialLength - expression.length());
	}

	public static DataVisitor parse(String expression) throws ParseException {
		AtomicInteger index = new AtomicInteger();
		Token[] tokens = tokenize(expression);
		try {
			return parseTokens(tokens, index, null);
		} catch (ParseException exception) {
			throw exception;
		} catch (Exception exception) {
			Token recentToken = tokens[index.get() - 1];
			throw new ParseException("Syntax unrelated exception: " + exception, recentToken.startIndex() + recentToken.contents().length());
		}
	}

	public static DataVisitor parseTokens(Token[] tokens, AtomicInteger index, @Nullable TokenType closerType) throws ParseException {
		DataVisitor dataVisitor = VariableDataVisitor.ROOT;
		int tokensLength = tokens.length;
		int localStartingIndex = index.get();
		int i = localStartingIndex;
		for (; i < tokensLength; i++) {
			var token = tokens[i];
			switch (token.type()) {
				case NUMBER -> {
					String contents = token.contents();
					if (contents.contains(".")) {
						dataVisitor = doubleValue(Double.parseDouble(contents));
						continue;
					}
					long longValue = Long.parseLong(contents);
					dataVisitor = longValue > Integer.MAX_VALUE || longValue < Integer.MIN_VALUE ? longValue(longValue) : intValue((int) longValue);
				}
				case STRING -> {
					String string = token.contents();
					string = token.contents().substring(1, string.length() - 1);
					dataVisitor = string(cleanEscapedCharacters(string));
				}
				case NAME -> {
					String tokenContents = token.contents();
					String name = cleanEscapedCharacters(tokenContents);
					if (i == localStartingIndex) {
						switch (name) {
							case "@":
								dataVisitor = VariableDataVisitor.THIS;
								continue;
							case "meta":
								dataVisitor = VariableDataVisitor.META;
								continue;
							case "this":
								continue;
						}
					}
					int nextIndex = i + 1;
					if (nextIndex < tokensLength && tokens[nextIndex].type() == TokenType.LEFT_PARENTHESIS) {
						FunctionParser functionParser = FUNCTIONS.get(name);
						if (functionParser == null)
							throw new ParseException("Unknown function: " + name, token.startIndex() + tokenContents.length());
						DataVisitor instance = null;
						int previousIndex = i - 1;
						if (previousIndex >= 1 && tokens[previousIndex].type() == TokenType.DOT)
							instance = dataVisitor;
						index.set(nextIndex + 1);
						dataVisitor = functionParser.parse(instance, tokens, index);
						checkTokenExists(TokenType.RIGHT_PARENTHESIS, index, tokens);
						i = index.get();
					} else {
						dataVisitor = dataVisitor == VariableDataVisitor.THIS ? new VariableDataVisitor.Local(name, ElementType.ELEMENTAL) : new EncapsulatedDataVisitor.Elemental(dataVisitor, string(name));
					}
				}
				case LEFT_BRACKET -> {
					int indexAfterLeftBracket = i + 1;
					if (indexAfterLeftBracket < tokensLength && tokens[indexAfterLeftBracket].type() == TokenType.RIGHT_BRACKET) {
						dataVisitor = new EncapsulatedDataVisitor.Elemental(dataVisitor, null);
						i = indexAfterLeftBracket;
						continue;
					}
					index.set(indexAfterLeftBracket);
					DataVisitor indexAccessor = parseTokens(tokens, index, TokenType.RIGHT_BRACKET);
					checkTokenExists(TokenType.RIGHT_BRACKET, index, tokens);
					i = index.get();
					dataVisitor = new EncapsulatedDataVisitor.Elemental(dataVisitor, indexAccessor);
				}
				case LEFT_PARENTHESIS -> {
					int indexAfterLeftParenthesis = i + 1;
					if (indexAfterLeftParenthesis >= tokensLength)
						throw new ParseException("Missing contents to right of left parenthesis", token.startIndex() + 1);
					if (tokens[indexAfterLeftParenthesis].type() == TokenType.RIGHT_PARENTHESIS)
						throw new ParseException("Missing contents inside parentheses", tokens[i].startIndex() + 1);
					index.set(indexAfterLeftParenthesis);
					dataVisitor = parseTokens(tokens, index, TokenType.RIGHT_PARENTHESIS);
					i = index.get();
					checkTokenExists(TokenType.RIGHT_PARENTHESIS, index, tokens);
				}
				case DOT -> {
					if (i <= 0 || (dataVisitor != VariableDataVisitor.THIS && !(dataVisitor.getReturnType() instanceof ElementType)))
						throw new ParseException("Cannot perform dot operation on non-elemental type", token.startIndex() + 1);
					int nextIndex = i + 1;
					if (nextIndex >= tokensLength || tokens[nextIndex].type() != TokenType.NAME)
						throw new ParseException("Expected to find name after dot", token.startIndex() + 1);
				}
				default -> {
					TokenType type = token.type();
					if (type != closerType)
						throw new ParseException("Found dangling token of type " + type, token.startIndex() + 1);
					index.set(i);
					return dataVisitor;
				}
			}
		}
		index.set(i);
		return dataVisitor;
	}

	private static void checkTokenExists(TokenType type, AtomicInteger index, Token[] tokens) throws ParseException {
		int i = index.get();
		int tokensLength = tokens.length;
		if (i >= tokensLength || tokens[i].type() != type) {
			Token tokenBefore = tokens[Math.min(i - 1, tokensLength - 1)];
			throw new ParseException("Expected token: " + type.name(), tokenBefore.startIndex() + tokenBefore.contents().length());
		}
	}

	private static String cleanEscapedCharacters(String string) {
		StringBuilder result = new StringBuilder();
		boolean isEscaping = false;
		for (int i = 0; i < string.length(); i++) {
			char character = string.charAt(i);
			if (isEscaping) {
				result.append(character);
				isEscaping = false;
			} else if (character == '\\') {
				isEscaping = true;
			} else {
				result.append(character);
			}
		}
		if (isEscaping) throw new IllegalArgumentException("Unmatched backslash at end of input");
		return result.toString();
	}

	public static String formatParseException(String parsedString, ParseException parseException) {
		StringBuilder builder = new StringBuilder();
		builder.append(parseException);
		builder.append(":\n");
		int cursor = Math.min(parsedString.length(), parseException.getErrorOffset());
		if (cursor > 10) builder.append("...");
		builder.append(parsedString, Math.max(0, cursor - 10), cursor);
		builder.append("<--[HERE]");
		return builder.toString();
	}

	public interface FunctionParser {
		DataVisitor parse(@Nullable DataVisitor instance, Token[] tokens, AtomicInteger index) throws ParseException;
	}

	public record Token(TokenType type, String contents, int startIndex) {}

	public enum TokenType {
		NAME("([a-zA-Z_@][a-zA-Z_0-9]*|\\\\.)+"),
		NUMBER("[+-]?\\d+(\\.\\d+)?"),
		STRING("\"([^\"\\\\]|\\\\.)*\""),
		LEFT_BRACKET("(?<!\\\\)\\["),
		RIGHT_BRACKET("(?<!\\\\)\\]"),
		LEFT_PARENTHESIS("(?<!\\\\)\\("),
		RIGHT_PARENTHESIS("(?<!\\\\)\\)"),
		DOT("(?<!\\\\)\\.");

		private final Pattern pattern;

		TokenType(String regex) {
			this.pattern = Pattern.compile(regex);
		}
	}
}
