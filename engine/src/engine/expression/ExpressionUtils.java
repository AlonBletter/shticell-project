package engine.expression;

import engine.expression.api.Expression;
import engine.expression.api.Operation;
import engine.expression.type.BooleanWrapper;
import engine.expression.type.Numeric;
import engine.expression.type.Text;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionUtils {
    public static void main(String[] args) {
//        System.out.println(tokenizeExpression("{PLUS,2,3}")); // Output: 5
//        System.out.println(tokenizeExpression("{MINUS,{PLUS,4,5},{POW,2,3}}")); // Output: 1
//        System.out.println(tokenizeExpression("{CONCAT,Hello,World}")); // Output: HelloWorld
//        System.out.println(tokenizeExpression("{ABS,{MINUS,4,5}}")); // Output: 1
//        System.out.println(tokenizeExpression("{POW,2,3}")); // Output: 8
//        System.out.println(tokenizeExpression("{SUB,hello,2,3}")); // Output: 8
//        System.out.println(tokenizeExpression("{MOD,4, 2}")); // Output: 0
//        System.out.println(tokenizeExpression("5"));
//        System.out.println(tokenizeExpression("BLABLBALLBA"));
//        Expression exp = buildExpressionFromString("{CONCAT,HELLO   ,123}");
//        System.out.println(exp.evaluate(null).extractValueWithExpectation(exp.evaluate(null).getCellType().getType()));

        String str = "{PLUS,{REF,A4},{MINUS,{REF,A5},6}}";

        System.out.println();
    }

    private static class Node {
        String value;
        List<Node> children;

        Node(String value, List<Node> children) {
            this.value = value;
            this.children = children;
        }

        public String getValue() {
            return value;
        }

        public List<Node> getChildren() {
            return children;
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
        Node tokenized = tokenizeExpression(inputToParse);

        return parseExpression(tokenized);
    }

    private static Node tokenizeExpression(String inputToTokenize) {
        if (inputToTokenize.startsWith("{") && inputToTokenize.endsWith("}")) {
            int commaIndex = inputToTokenize.indexOf(',');
            String function = inputToTokenize.substring(1, commaIndex);
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

            Operation operation = Operation.valueOf(expression.value);
            resultExpression = operation.createExpression(argumentsList);
        } else {
            resultExpression = parseLeafExpression(expression.value);
        }

        return resultExpression;
    }

    private static Expression parseLeafExpression(String expression) {
        String numberPattern = "^\\d+(\\.\\d+)?$";
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

    public static List<Coordinate> extractReferences(String input) {
        List<Coordinate> coordinates = new ArrayList<>();

        // Use regex to match the pattern {REF,Coordinate}
        String regex = "\\{REF,([A-Z]+\\d+)\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        // Find all matches and add them to the list
        while (matcher.find()) {
            Coordinate coordinate = CoordinateFactory.createCoordinate(matcher.group(1));
            coordinates.add(coordinate); // group(1) gets the coordinate part (e.g., A4, A5)
        }

        return coordinates;
    }
}
