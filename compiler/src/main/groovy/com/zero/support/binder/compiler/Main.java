package com.zero.support.binder.compiler;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        File dir = new File("./library/src/main/java/com/zero/support/binder");
        System.out.println(dir.getAbsoluteFile());
        File output = new File("./compiler/src/main/java/com/zero/support/binder/compiler/Constant.java");

        StringBuilder builder = new StringBuilder();

        builder.append("package com.zero.support.binder.compiler;");
        builder.append("\n");
        builder.append(" public class Constant{\n" +
                "        public static final String PACKAGE_NAME = \"com.zero.support\";");

        builder.append("\n");
        for (File file : dir.listFiles()) {
            try {
                String s = FileUtils.readFileToString(file);
                builder.append("public static final String ");
                String name = FilenameUtils.getBaseName(file.getName());
                char[] chars = name.toCharArray();
                builder.append(Character.toUpperCase(chars[0]));
                for (int i = 1; i < chars.length; i++) {
                    if (Character.isUpperCase(chars[i]) && i != chars.length - 1) {
                        builder.append("_");
                    }
                    builder.append(Character.toUpperCase(chars[i]));
                }

                builder.append(" = \"");
                builder.append("\";");
                builder.append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        builder.append("\n}");
        try {
            FileUtils.writeStringToFile(output, builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class Constant {
        public static final String PACKAGE_NAME = "com.zero.support";
    }
}
