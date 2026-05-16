package Core.Lib;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import Lang.Ukrainian;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class CoreFunc {
    public static void playMP3(String filePath) {
        new Thread(() -> {
            try {
                String resourcePath = filePath.replace("\\", "/");
                if (!resourcePath.startsWith("/")) {
                    resourcePath = "/" + resourcePath;
                }

                InputStream audioStream = CoreFunc.class.getResourceAsStream(resourcePath);
                if (audioStream == null) {
                    audioStream = new FileInputStream(filePath);
                }

                try (InputStream stream = audioStream) {
                    Player player = new Player(stream);
                    player.play();
                }
            } catch (JavaLayerException | IOException e) {
                Ansi.error(Ukrainian.SoundPlayError + filePath);
            }
        }).start();
    }

        public static void ClearConsole() {
        try {
            String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                // Спроба через cmd
                new ProcessBuilder("cmd", "/c", "cls")
                        .inheritIO()
                        .start()
                        .waitFor();
            } else {
                // Linux / Mac
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }

        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            // fallback якщо щось пішло не так
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

}
