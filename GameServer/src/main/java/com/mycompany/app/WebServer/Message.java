package com.mycompany.app.WebServer;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mycompany.app.Game.Pente.PenteGameSettings;

public class Message {
    public String namespace;
    public String action;
    // public final String data;
    // public final String sender;

    // public final String reciever;

    public Message(MessageBuilder builder) {

    }

    public Message(String rawMessage) {
        HashMap<String, Object> map = parseJson(rawMessage);
        
        Object namespace = map.get("namespace");
        if (namespace != null && namespace instanceof String) {
            this.namespace = (String)namespace;
        } else {
            this.namespace = "";
        }

        Object action = map.get("namespace");
        if (action != null && action instanceof String) {
            this.action = (String)action;
        } else {
            this.action = "";
        }

    }

    public static HashMap<String, Object> parseJson(String jsonString) {
        HashMap<String, Object> result = new HashMap<>();
        parseObject(jsonString, result);
        return result;
    }

    private static void parseObject(String jsonString, Map<String, Object> result) {
        Pattern pattern = Pattern.compile("\"(\\w+)\":\\s*([^,\\}\\]]+)");
        Matcher matcher = pattern.matcher(jsonString);

        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2).trim();

            if (value.startsWith("{")) {
                Map<String, Object> nestedMap = new HashMap<>();
                result.put(key, nestedMap);
                parseObject(value, nestedMap);
            } else if (value.startsWith("[")) {
                result.put(key, parseArray(value));
            } else if (value.startsWith("\"")) {
                result.put(key, value.substring(1, value.length() - 1));
            } else {
                result.put(key, parsePrimitive(value));
            }
        }
    }

    private static Object parsePrimitive(String value) {
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            return Boolean.parseBoolean(value);
        } else if (value.contains(".")) {
            return Double.parseDouble(value);
        } else {
            return Long.parseLong(value);
        }
    }

    private static Object[] parseArray(String arrayString) {
        String[] elements = arrayString.substring(1, arrayString.length() - 1).split(",");
        Object[] resultArray = new Object[elements.length];

        for (int i = 0; i < elements.length; i++) {
            String element = elements[i].trim();
            if (element.startsWith("{")) {
                Map<String, Object> nestedMap = new HashMap<>();
                parseObject(element, nestedMap);
                resultArray[i] = nestedMap;
            } else if (element.startsWith("\"")) {
                resultArray[i] = element.substring(1, element.length() - 1);
            } else {
                resultArray[i] = parsePrimitive(element);
            }
        }

        return resultArray;
    }


    public static class MessageBuilder {
        private int numInARowToWin;
        private int capturesToWin;

        public MessageBuilder setToDefaultValues() {
            this.numInARowToWin = 5;
            this.capturesToWin = 5;
            return this;
        }
        
        public MessageBuilder setCapturesToWin(int capturesToWin) {
            this.capturesToWin = capturesToWin;
            return this;
        }

        public MessageBuilder setNumInARowToWin(int numInARowToWin) {
            this.numInARowToWin = numInARowToWin;
            return this;
        }

        public Message build() {
            return new Message(this);
        }
    }
}
