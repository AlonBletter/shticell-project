package engine.impl.function;

import engine.Function;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ExpressionTree {
    public static void main(String[] args) {
//        System.out.println(tokenizeExpression("{PLUS,2,3}")); // Output: 5
        System.out.println(tokenizeExpression("{MINUS,{PLUS,4,5},{POW,2,3}}")); // Output: 1
//        System.out.println(tokenizeExpression("{CONCAT,Hello,World}")); // Output: HelloWorld
//        System.out.println(tokenizeExpression("{ABS,{MINUS,4,5}}")); // Output: 1
//        System.out.println(tokenizeExpression("{POW,2,3}")); // Output: 8
//        System.out.println(tokenizeExpression("{SUB,hello,2,3}")); // Output: 8
//        System.out.println(tokenizeExpression("{MOD,4, 2}")); // Output: 0
        System.out.println(tokenizeExpression("5"));
        System.out.println(tokenizeExpression("BLABLBALLBA"));
    }

    public static class Node {
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

    public static Function<?> buildFunction(Node expression) {
        Function<?> resultFunction = null;
        List<Function<?>> argumentsList = new LinkedList<>();

        if(!expression.children.isEmpty()) { // IM AN OPERATION
            for(int i = 0; i < expression.children.size(); i++) {
                argumentsList.add(buildFunction(expression.children.get(i)));
            }

            Operation operation = Operation.valueOf(expression.value);
            resultFunction = operation.createFunction(argumentsList);
        } else { // IM A LEAF
            resultFunction = parseExpression(expression.value);
        }

        return resultFunction; // not evaluated!
    }

    public static Function<?> parseExpression(String expression) {
        String numberPattern = "^\\d+(\\.\\d+)?$";
        Function<?> parsingResult = null;

        if(expression.matches(numberPattern)) {
            double parsedValue = Double.parseDouble(expression);
            parsingResult = new Number(parsedValue);
        } else if (expression.equalsIgnoreCase("true") || expression.equalsIgnoreCase("false")) {
            boolean parsedValue = Boolean.parseBoolean(expression);
            parsingResult = new BooleanWrapper(parsedValue);
        } else {
            parsingResult = new Text(expression);
        }

        return parsingResult;
    }

    public static Node tokenizeExpression(String input) {
        input = input.trim();

        if (input.startsWith("{") && input.endsWith("}")) {
            int commaIndex = input.indexOf(',');
            String function = input.substring(0, commaIndex).trim();
            Node root = new Node(function, new ArrayList<>());

            return tokenizeSubExpression(root, input.substring(1, input.length() - 1).trim());
        } else {
            return new Node(input, null);
        }
    }

    private static Node tokenizeSubExpression(Node root, String input) {
        int commaIndex = input.indexOf(','), bracketCounter = 0;
        String argument;

        for(int i = commaIndex + 1; i < input.length(); i++) {
            if(input.charAt(i) == '{') {
                bracketCounter++;
            } else if (input.charAt(i) == '}') {
                bracketCounter--;
            } else if (input.charAt(i) == ',' && bracketCounter == 0) {
                argument = input.substring(commaIndex + 1, i).trim();
                commaIndex = i;
                root.children.add(tokenizeExpression(argument));
            }
        }

        argument = input.substring(commaIndex + 1).trim();
        root.children.add(tokenizeExpression(argument));

        return root;
    }
}
