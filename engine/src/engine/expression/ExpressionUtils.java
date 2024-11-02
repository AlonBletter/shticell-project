package engine.expression;

import engine.expression.api.Expression;
import engine.expression.api.Operation;
import engine.expression.type.BooleanWrapper;
import engine.expression.type.Numeric;
import engine.expression.type.Text;
import dto.coordinate.Coordinate;
import dto.coordinate.CoordinateFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionUtils {
    private static class Node {

        String value;
        List<Node> children;
        Node(String value, List<Node> children) {
            this.value = value;
            this.children = children;
        }

        @Override
        public String toString() {
            return "{" +
                    "value=" + value +
                    ", children=" + children +
                    '}';
        }

    }
    public static Expression buildExpressionFromString(String inputToParse) {
        Node tokenized = tokenizeExpression(inputToParse.trim());

        return parseExpression(tokenized);
    }

    private static Node tokenizeExpression(String inputToTokenize) {
        if (inputToTokenize.startsWith("{") && inputToTokenize.endsWith("}")) {
            int commaIndex = inputToTokenize.indexOf(',');
            String function = inputToTokenize.substring(1, commaIndex).toUpperCase();
            Node root = new Node(function, new ArrayList<>());

            return tokenizeFunctionExpression(root, inputToTokenize.substring(1, inputToTokenize.length() - 1));
        } else {
            return new Node(inputToTokenize, null);
        }
    }

    private static Node tokenizeFunctionExpression(Node root, String input) {
        int commaIndex = input.indexOf(','), bracketCounter = 0;
        String argument;

        for(int i = commaIndex + 1; i < input.length(); i++) {
            if(input.charAt(i) == '{') {
                bracketCounter++;
            } else if (input.charAt(i) == '}') {
                bracketCounter--;
            } else if (input.charAt(i) == ',' && bracketCounter == 0) {
                argument = input.substring(commaIndex + 1, i);
                commaIndex = i;
                root.children.add(tokenizeExpression(argument));
            }
        }

        argument = input.substring(commaIndex + 1);
        root.children.add(tokenizeExpression(argument));

        return root;
    }

    private static Expression parseExpression(Node expression) {
        Expression resultExpression;
        List<Expression> argumentsList = new LinkedList<>();

        if((expression.children != null && !expression.children.isEmpty())) {
            for(int i = 0; i < expression.children.size(); i++) {
                argumentsList.add(parseExpression(expression.children.get(i)));
            }

            Operation operation;

            try {
                operation = Operation.valueOf(expression.value);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("The system does not support " + expression.value.toUpperCase() + " function.");
            }

            resultExpression = operation.createExpression(argumentsList);
        } else {
            resultExpression = parseLeafExpression(expression.value);
        }

        return resultExpression;
    }

    private static Expression parseLeafExpression(String expression) {
        String numberPattern = "^-?\\d+(\\.\\d+)?$";
        Expression parsingResult;

        try {
            if(expression.matches(numberPattern)) {
                parsingResult = new Numeric(
                        Double.parseDouble(expression));
            } else if (expression.equalsIgnoreCase("true") || expression.equalsIgnoreCase("false")) {
                parsingResult = new BooleanWrapper(
                        Boolean.parseBoolean(expression));
            } else {
                parsingResult = new Text(expression);
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }

        return parsingResult;
    }

    public static Set<Coordinate> extractReferences(String input) {
        Set<Coordinate> coordinates = new HashSet<>();
        String regex = "\\{REF,([A-Z]+\\d+)\\}";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            Coordinate coordinate = CoordinateFactory.createCoordinate(matcher.group(1).toUpperCase());
            coordinates.add(coordinate);
        }

        return coordinates;
    }

    public static List<Coordinate> parseRange(String range) {
        List<Coordinate> coordinates = new ArrayList<>();
        String[] corners = range.split("\\.\\.");

        if (corners.length != 2) {
            throw new IllegalArgumentException("Invalid range format. Expected <coordinate of top left cell>..<coordinate of bottom right cell>");
        }

        Coordinate start;
        Coordinate end;
        try {
            start = CoordinateFactory.createCoordinate(corners[0]);
            end = CoordinateFactory.createCoordinate(corners[1]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid range format. Expected <coordinate of top left cell>..<coordinate of bottom right cell>");
        }

        coordinates.addFirst(start);
        coordinates.addLast(end);

        return coordinates;
    }

    public static Set<String> extractRanges(String cellOriginalValue) {
        String functionPattern = "(?i)\\{(sum|average),\\s*([^}]+)\\}";
        Pattern pattern = Pattern.compile(functionPattern);
        Matcher matcher = pattern.matcher(cellOriginalValue);

        Set<String> ranges = new HashSet<>();

        while (matcher.find()) {
            ranges.add(matcher.group(2));
        }

        return ranges;
    }
}
