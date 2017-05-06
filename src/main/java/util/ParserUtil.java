package util;

import exception.ParserError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xiaoke on 17-5-6.
 */
public class ParserUtil {
    // simple parser token info used to store the information of tokens
    private static enum TOKENTYPE {
        KEY, VALUE, ERROR
    }

    static class TokenInfo {

        String token;
        TOKENTYPE type;

        TokenInfo() {
            this(null, TOKENTYPE.ERROR);
        }

        TokenInfo(String token, TOKENTYPE type) {
            this.token = token;
            this.type = type;
        }
    }

    private static class MapTmp extends HashMap<String, String> {
        @Override
        public String put(String key, String value) {
            String k = key.toUpperCase();
            return super.put(k, value);
        }

        @Override
        public String get(Object key) {
            String k = key.toString().toUpperCase();
            return super.get(k);
        }
    }
    // paras with key-value
    /**
     * this method is used to parse para string to key - value pair
     * like "-a aaa -b -c ccc" to (a,aaa) (b,null) (c,ccc)
     * @param legalKey: Only the legal keys can be accepted
     * @param para: Para string to be parsed
     * @return para: Parsed key - value pair
     * @throws ParserError: if error occurs or illegal key is contained
     */
    public static Map<String, String> parser(Set<String> legalKey, String para) throws ParserError {
        Set<String> existKey = new HashSet<String>();
        Map<String, String> paras = new MapTmp();
        List<TokenInfo> tokens = parsePara(para);
        boolean isType = false;
        String typeName = null;
        for (TokenInfo ti : tokens) {
            if (ti.type == TOKENTYPE.KEY) {
                if (isType) {
                    //typeName = typeName.toUpperCase();
                    checkKey(legalKey, existKey, typeName);
                    paras.put(typeName, null);
                } else {
                    isType = true;
                }
                typeName = ti.token;
            } else if (ti.type == TOKENTYPE.VALUE) {
                if (isType == false) {
                    throw new ParserError("illegal value", ti.token);
                } else {
                    //typeName = typeName.toUpperCase();
                    checkKey(legalKey, existKey, typeName);
                    paras.put(typeName, ti.token);
                    isType = false;
                }
            } else {
                throw new ParserError("illegal token", ti.token);
            }
        }

        if (isType) {
            //typeName = typeName.toUpperCase();
            checkKey(legalKey, existKey, typeName);
            paras.put(typeName, null);
        }
        return paras;
    }

    private static void checkKey(Set<String> legalKeys, Set<String> existKeys, String keyName) throws ParserError {
        if (legalKeys != null && !legalKeys.contains(keyName)) {
            throw new ParserError("unsupported key", keyName);
        }
        if (existKeys.contains(keyName)) {
            throw new ParserError("multi key", keyName);
        } else {
            existKeys.add(keyName);
        }
    }

    // use state machine to parser a string
    private static List<TokenInfo> parsePara(String args) {
        List<TokenInfo> tokens = new ArrayList<TokenInfo>();
        StringBuffer token = new StringBuffer();
        int status = 0;
        for_bp: for (int i = 0; i < args.length(); i++) {
            char c = args.charAt(i);
            switch (c) {
                case ' ': {
                    switch (status) {
                        case 0:
                            break;
                        case 2:
                            tokenIn(tokens, token, status);
                            status = 0;
                            break;
                        case 3:
                            tokenIn(tokens, token, status);
                            status = 0;
                            break;
                        default:
                            status = -1;
                            break for_bp;
                    }
                    break;
                }

                case '-': {
                    switch (status) {
                        case 0:
                            status = 1;
                            break;
                        default:
                            status = -1;
                            break for_bp;
                    }
                    break;
                }

                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z': {
                    switch (status) {
                        case 1:
                        case 2:
                            token.append(c);
                            status = 2;
                            break;
                        case 0:
                        case 3:
                            token.append(c);
                            status = 3;
                            break;
                        default:
                            status = -1;
                            break for_bp;

                    }
                    break;
                }

                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9': {
                    switch (status) {
                        case 0:
                            token.append(c);
                            status = 3;
                            break;
                        case 1:
                            token.append('-');
                            token.append(c);
                            status = 3;
                            break;
                        default:
                            token.append(c);
                            break;
                    }
                    break;
                }
                default: {
                    switch (status) {
                        case 0:
                            token.append(c);
                            status = 3;
                            break;
                        case 2:
                        case 3:
                            token.append(c);
                            break;
                        default:
                            status = -1;
                            break for_bp;
                    }
                    break;
                }
            }
        }

        tokenIn(tokens, token, status);

        return tokens;
    }

    private static void tokenIn(List<TokenInfo> tokens, StringBuffer sb, int status) {
        if (tokens == null) {
            tokens = new ArrayList<TokenInfo>();
        }
        TokenInfo ti;
        if (status == 2) {
            ti = new TokenInfo(sb.toString(), TOKENTYPE.KEY);
        } else if (status == 3) {
            ti = new TokenInfo(sb.toString(), TOKENTYPE.VALUE);
        } else {
            ti = new TokenInfo();
        }
        sb.setLength(0);
        tokens.add(ti);
    }
}
