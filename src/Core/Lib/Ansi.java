/*
Це можна сказати бібліотека на рівні ядра програми
Вона допомагає робити текст кольоровим
І це все що я можу сказати про неї
*/

package Core.Lib;

public class Ansi {

    // Reset
    public static final String RESET = "\u001B[0m";

    // Foreground (текст)
    public static class Fore {
        public static final String BLACK = "\u001B[30m";
        public static final String RED = "\u001B[31m";
        public static final String GREEN = "\u001B[32m";
        public static final String YELLOW = "\u001B[33m";
        public static final String BLUE = "\u001B[34m";
        public static final String MAGENTA = "\u001B[35m";
        public static final String CYAN = "\u001B[36m";
        public static final String WHITE = "\u001B[37m";

        // Яскраві (як Light у colorama)
        public static final String LIGHTBLACK_EX = "\u001B[90m";
        public static final String LIGHTRED_EX = "\u001B[91m";
        public static final String LIGHTGREEN_EX = "\u001B[92m";
        public static final String LIGHTYELLOW_EX = "\u001B[93m";
        public static final String LIGHTBLUE_EX = "\u001B[94m";
        public static final String LIGHTMAGENTA_EX = "\u001B[95m";
        public static final String LIGHTCYAN_EX = "\u001B[96m";
        public static final String LIGHTWHITE_EX = "\u001B[97m";
    }

    // Background (фон)
    public static class Back {
        public static final String BLACK = "\u001B[40m";
        public static final String RED = "\u001B[41m";
        public static final String GREEN = "\u001B[42m";
        public static final String YELLOW = "\u001B[43m";
        public static final String BLUE = "\u001B[44m";
        public static final String MAGENTA = "\u001B[45m";
        public static final String CYAN = "\u001B[46m";
        public static final String WHITE = "\u001B[47m";

        // Яскраві фони
        public static final String LIGHTBLACK_EX = "\u001B[100m";
        public static final String LIGHTRED_EX = "\u001B[101m";
        public static final String LIGHTGREEN_EX = "\u001B[102m";
        public static final String LIGHTYELLOW_EX = "\u001B[103m";
        public static final String LIGHTBLUE_EX = "\u001B[104m";
        public static final String LIGHTMAGENTA_EX = "\u001B[105m";
        public static final String LIGHTCYAN_EX = "\u001B[106m";
        public static final String LIGHTWHITE_EX = "\u001B[107m";
    }

    // Стилі
    public static class Style {
        public static final String BRIGHT = "\u001B[1m";
        public static final String DIM = "\u001B[2m";
        public static final String NORMAL = "\u001B[22m";
    }

    // ===== ОСНОВНІ МЕТОДИ =====
    public static void print(String color, String text) {
        System.out.print(color + text + RESET);
    }

    public static void println(String color, String text) {
        System.out.println(color + text + RESET);
    }

    // ===== ГОТОВІ МЕТОДИ =====
    public static void success(String text) {
        println(Fore.GREEN, "[SUCCESS] " + text);
    }

    public static void error(String text) {
        println(Fore.RED, "[ERROR] " + text);
    }

    public static void warning(String text) {
        println(Fore.YELLOW, "[WARNING] " + text);
    }

    public static void info(String text) {
        println(Fore.CYAN, "[INFO] " + text);
    }

    // ===== КОМБІНАЦІЯ =====
    public static String colorize(String color, String text) {
        return color + text + RESET;
    }
}
