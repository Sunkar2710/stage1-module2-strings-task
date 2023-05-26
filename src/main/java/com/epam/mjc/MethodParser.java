package com.epam.mjc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodParser {

    /**
     * Parses string that represents a method signature and stores all it's members into a {@link MethodSignature} object.
     * signatureString is a java-like method signature with following parts:
     * 1. access modifier - optional, followed by space: ' '
     * 2. return type - followed by space: ' '
     * 3. method name
     * 4. arguments - surrounded with braces: '()' and separated by commas: ','
     * Each argument consists of argument type and argument name, separated by space: ' '.
     * Examples:
     * accessModifier returnType methodName(argumentType1 argumentName1, argumentType2 argumentName2)
     * private void log(String value)
     * Vector3 distort(int x, int y, int z, float magnitude)
     * public DateTime getCurrentDateTime()
     *
     * @param signatureString source string to parse
     * @return {@link MethodSignature} object filled with parsed values from source string
     */
    public MethodSignature parseFunction(String signatureString) {

        List<MethodSignature.Argument> argumentList = new ArrayList<>();

        String methodName = Objects.requireNonNull(parseSourceString(signatureString, "([a-zA-Z]+\\s*(?=\\())")).trim();

        String arguments = Objects.requireNonNull(parseSourceString(signatureString, "((?<=\\()[a-zA-Z\\s,]*(?=\\)))")).trim();

        if (!arguments.isEmpty()) {

            String[] argumentsStringArray = arguments.split(",\s");

            for (String arg : argumentsStringArray) {
                String type = Objects.requireNonNull(parseSourceString(arg, "(^\s*[a-zA-Z]+)")).trim();
                String name = Objects.requireNonNull(parseSourceString(arg, "([a-zA-Z]+\\s*$)")).trim();
                argumentList.add(new MethodSignature.Argument(type, name));
            }

        }

        MethodSignature methodSignature = new MethodSignature(methodName, argumentList);

        methodSignature.setAccessModifier(parseSourceString(signatureString, "((?=private|public|protected)[a-z]+)"));

        methodSignature.setReturnType(parseSourceString(signatureString, "(((?<=private\\s|public\\s|protected\\s)\\s*\\w+)" +
                "|((?!private|public|protected)^\\s*\\w+))"));

        return methodSignature;
    }

    private static String parseSourceString(String signatureString, String regex) {

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(signatureString);

        while (matcher.find()) {
            return signatureString.substring(matcher.start(), matcher.end());
        }

        return null;
    }
}
