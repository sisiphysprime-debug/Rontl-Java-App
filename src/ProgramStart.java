/*
ProgramStart.java
Це головний стартовий клас який запускає MainProgram.java
Якщо jar відкрили подвійним кліком без консолі він перевідкриває програму у консольному вікні
*/

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ProgramStart {
    private static final String CONSOLE_RELAUNCH_FLAG = "rontl.console.relaunched";

    public static void main(String[] args) {
        if (relaunchJarWithConsoleEncodingIfNeeded(args)) {
            return;
        }

        configureCurrentConsoleEncoding();
        MainProgram.main(args);
    }

    private static boolean relaunchJarWithConsoleEncodingIfNeeded(String[] args) {
        if (!isWindows() || Boolean.getBoolean(CONSOLE_RELAUNCH_FLAG)) {
            return false;
        }

        try {
            File appFile = new File(ProgramStart.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (!isJarFile(appFile)) {
                return false;
            }

            File javaExe = new File(System.getProperty("java.home"), "bin/java.exe");
            if (!javaExe.isFile()) {
                return false;
            }

            if (System.console() == null) {
                relaunchJarInNewConsole(appFile, javaExe, args);
            } else {
                relaunchJarInCurrentConsole(appFile, javaExe, args);
            }

            return true;
        } catch (IOException | URISyntaxException | SecurityException e) {
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private static void configureCurrentConsoleEncoding() {
        ConsoleEncoding encoding = getCurrentConsoleEncoding();
        applyEncodingProperties(encoding);

        try {
            System.setOut(new PrintStream(System.out, true, encoding.stdout));
            System.setErr(new PrintStream(System.err, true, encoding.stderr));
        } catch (SecurityException e) {
        }
    }

    private static void relaunchJarInNewConsole(File appFile, File javaExe, String[] args) throws IOException {
        File launcherScript = createConsoleLauncherScript(appFile, javaExe, args);

        String command = "start \"\" /D "
                + cmdQuote(appFile.getParentFile().getAbsolutePath())
                + " cmd.exe /k "
                + cmdQuote(launcherScript.getAbsolutePath());

        new ProcessBuilder("cmd.exe", "/c", command).start();
        System.exit(0);
    }

    private static void relaunchJarInCurrentConsole(File appFile, File javaExe, String[] args)
            throws IOException, InterruptedException {
        String command = "chcp 65001 >nul && "
                + toCmdCommand(buildJavaJarCommand(appFile, javaExe, args, ConsoleEncoding.utf8()));

        Process process = new ProcessBuilder("cmd.exe", "/d", "/c", command)
                .inheritIO()
                .start();

        try {
            int exitCode = process.waitFor();
            System.exit(exitCode);
        } catch (InterruptedException e) {
            process.destroy();
            throw e;
        }
    }

    private static File createConsoleLauncherScript(File appFile, File javaExe, String[] args) throws IOException {
        File script = File.createTempFile("rontl-console-", ".cmd");
        StringBuilder content = new StringBuilder();
        content.append("@echo off\r\n");
        content.append("chcp 65001 >nul\r\n");
        content.append("cd /d ")
                .append(cmdQuote(appFile.getParentFile().getAbsolutePath()))
                .append("\r\n");
        content.append(toCmdCommand(buildJavaJarCommand(appFile, javaExe, args, ConsoleEncoding.utf8()))).append("\r\n");
        content.append("echo.\r\n");
        content.append("echo Program finished.\r\n");
        content.append("pause\r\n");

        Files.writeString(script.toPath(), content.toString(), StandardCharsets.UTF_8);
        return script;
    }

    private static List<String> buildJavaJarCommand(
            File appFile,
            File javaExe,
            String[] args,
            ConsoleEncoding encoding
    ) {
        List<String> command = new ArrayList<>();
        command.add(javaExe.getAbsolutePath());
        command.add("-Dfile.encoding=" + encoding.stdin.name());
        command.add("-Dstdin.encoding=" + encoding.stdin.name());
        command.add("-Dsun.stdin.encoding=" + encoding.stdin.name());
        command.add("-Dstdout.encoding=" + encoding.stdout.name());
        command.add("-Dsun.stdout.encoding=" + encoding.stdout.name());
        command.add("-Dstderr.encoding=" + encoding.stderr.name());
        command.add("-Dsun.stderr.encoding=" + encoding.stderr.name());
        command.add("-D" + CONSOLE_RELAUNCH_FLAG + "=true");
        command.add("-jar");
        command.add(appFile.getAbsolutePath());

        Collections.addAll(command, args);

        return command;
    }

    private static ConsoleEncoding getCurrentConsoleEncoding() {
        Charset fallback = System.console() == null
                ? Charset.defaultCharset()
                : System.console().charset();
        Charset stdin = getCharsetProperty("stdin.encoding", fallback);
        Charset stdout = getCharsetProperty("stdout.encoding", fallback);
        Charset stderr = getCharsetProperty("stderr.encoding", stdout);

        return new ConsoleEncoding(stdin, stdout, stderr);
    }

    private static Charset getCharsetProperty(String propertyName, Charset fallback) {
        String value = System.getProperty(propertyName);
        if (value == null || value.isBlank()) {
            return fallback;
        }

        try {
            return Charset.forName(value);
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    private static void applyEncodingProperties(ConsoleEncoding encoding) {
        System.setProperty("file.encoding", encoding.stdin.name());
        System.setProperty("stdin.encoding", encoding.stdin.name());
        System.setProperty("sun.stdin.encoding", encoding.stdin.name());
        System.setProperty("stdout.encoding", encoding.stdout.name());
        System.setProperty("sun.stdout.encoding", encoding.stdout.name());
        System.setProperty("stderr.encoding", encoding.stderr.name());
        System.setProperty("sun.stderr.encoding", encoding.stderr.name());
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
    }

    private static boolean isJarFile(File file) {
        return file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".jar");
    }

    private static String cmdQuote(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private static String toCmdCommand(List<String> command) {
        StringBuilder result = new StringBuilder();

        for (String value : command) {
            if (result.length() > 0) {
                result.append(' ');
            }

            result.append(cmdQuote(value));
        }

        return result.toString();
    }

    private static class ConsoleEncoding {
        private final Charset stdin;
        private final Charset stdout;
        private final Charset stderr;

        private ConsoleEncoding(Charset stdin, Charset stdout, Charset stderr) {
            this.stdin = stdin;
            this.stdout = stdout;
            this.stderr = stderr;
        }

        private static ConsoleEncoding utf8() {
            return new ConsoleEncoding(StandardCharsets.UTF_8, StandardCharsets.UTF_8, StandardCharsets.UTF_8);
        }
    }

}
