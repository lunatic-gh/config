package de.chloedev.config.json;

import de.chloedev.config.json.element.JsonArray;
import de.chloedev.config.json.element.JsonElement;
import de.chloedev.config.json.element.JsonObject;
import de.chloedev.config.json.element.JsonPrimitive;

public class JsonParser {
    private final String json;
    private final int len;
    private int pos = 0;
    private JsonParser(String json) {
        this.json = json;
        this.len = json.length();
    }

    public static JsonElement parse(String json) {
        JsonParser p = new JsonParser(json);
        JsonElement root = p.parseValue();
        p.skipWhitespace();
        if (p.pos != p.len) {
            throw p.error("Unexpected trailing data");
        }
        return root;
    }

    private JsonElement parseValue() {
        skipWhitespace();
        if (pos >= len) {
            throw error("Unexpected end of input");
        }
        char c = json.charAt(pos);
        return switch (c) {
            case '{' -> parseObject();
            case '[' -> parseArray();
            case '"' -> parseString();
            case 't' -> parseLiteral("true", new JsonPrimitive(true));
            case 'f' -> parseLiteral("false", new JsonPrimitive(false));
            case 'n' -> parseLiteral("null", new JsonPrimitive((Object) null));
            default -> parseNumber();
        };
    }

    private JsonObject parseObject() {
        expect('{');
        JsonObject obj = new JsonObject();
        skipWhitespace();
        if (peek() == '}') {
            pos++;
            return obj;
        }
        while (true) {
            skipWhitespace();
            JsonPrimitive keyPrim = parseString();
            String key = keyPrim.getAsString();
            skipWhitespace();
            expect(':');
            JsonElement val = parseValue();
            obj.put(key, val);
            skipWhitespace();
            char next = peek();
            if (next == ',') {
                pos++;
                continue;
            } else if (next == '}') {
                pos++;
                break;
            } else {
                throw error("Expected ',' or '}' in object");
            }
        }
        return obj;
    }

    private JsonArray parseArray() {
        expect('[');
        JsonArray arr = new JsonArray();
        skipWhitespace();
        if (peek() == ']') {
            pos++;
            return arr;
        }
        while (true) {
            JsonElement elem = parseValue();
            arr.add(elem);
            skipWhitespace();
            char next = peek();
            if (next == ',') {
                pos++;
                continue;
            } else if (next == ']') {
                pos++;
                break;
            } else {
                throw error("Expected ',' or ']' in array");
            }
        }
        return arr;
    }

    private JsonPrimitive parseString() {
        expect('"');
        StringBuilder sb = new StringBuilder();
        while (pos < len) {
            char c = json.charAt(pos++);
            if (c == '"') break;
            if (c == '\\') {
                if (pos >= len) throw error("Unterminated escape");
                char esc = json.charAt(pos++);
                switch (esc) {
                    case '"':
                        sb.append('"');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    case '/':
                        sb.append('/');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'u':
                        if (pos + 4 > len) throw error("Invalid \\u escape");
                        String hex = json.substring(pos, pos + 4);
                        sb.append((char) Integer.parseInt(hex, 16));
                        pos += 4;
                        break;
                    default:
                        throw error("Invalid escape: \\" + esc);
                }
            } else {
                sb.append(c);
            }
        }
        return new JsonPrimitive(sb.toString());
    }

    private JsonPrimitive parseNumber() {
        int start = pos;
        if (peek() == '-') pos++;
        while (pos < len && Character.isDigit(peek())) pos++;
        if (peek() == '.') {
            while (pos < len && Character.isDigit(peek())) {
                pos++;
            }
        }
        if (peek() == 'e' || peek() == 'E') {
            pos++;
            if (peek() == '+' || peek() == '-') pos++;
            while (pos < len && Character.isDigit(peek())) pos++;
        }
        String numStr = json.substring(start, pos);
        Number n;
        if (numStr.contains(".") || numStr.contains("e") || numStr.contains("E")) {
            n = Double.parseDouble(numStr);
        } else {
            n = Long.parseLong(numStr);
        }
        return new JsonPrimitive(n);
    }

    private JsonElement parseLiteral(String lit, JsonElement result) {
        if (json.startsWith(lit, pos)) {
            pos += lit.length();
            return result;
        }
        throw error("Expected literal " + lit);
    }

    private void skipWhitespace() {
        while (pos < len && Character.isWhitespace(json.charAt(pos))) {
            pos++;
        }
    }

    private char peek() {
        return pos < len ? json.charAt(pos) : '\0';
    }

    private void expect(char ch) {
        if (peek() != ch) {
            throw error("Expected '" + ch + "'");
        }
        pos++;
    }

    private IllegalArgumentException error(String msg) {
        return new IllegalArgumentException(msg + " at position " + pos);
    }
}