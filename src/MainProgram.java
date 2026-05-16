/*
MainProgram.java
Основний код програми
Rontl 2023-2026
*/

//Імпорт бібліотек
import Core.Lib.Ansi;
import Core.TestModeStart;
import Lang.Ukrainian;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MainProgram { //Головний клас
    public static void main(String[] args) {

    @SuppressWarnings("resource")
    Scanner scanner = new Scanner(System.in);
    
    //Стартовий текст
    System.out.println(Ansi.Fore.BLUE + "Rontl Java App" + Ansi.RESET);
    System.out.println(Ansi.Fore.LIGHTGREEN_EX + Ansi.Back.CYAN + Ukrainian.HelpInfo + Ansi.RESET);

    //Всі команди
    Map<String, Runnable> commands = new HashMap<>();
    commands.put(Ukrainian.HelpCom, () -> OtherMenu.HelpMenu.ShowHelpMenu()); //Відкриває меню допомоги
    commands.put(Ukrainian.InfoCom, () -> OtherMenu.HelpMenu.ShowProgramInfo()); //Відкриває інформацію про програму
    commands.put("test", () -> TestModeStart.Start()); //

    while (true) {
        System.out.print(Ansi.Fore.LIGHTMAGENTA_EX + ">>> " + Ansi.RESET); //Показ перед курсором
        String userInput = scanner.nextLine().toLowerCase(); //Головний інпут

        if (commands.containsKey(userInput)) { //Перевіряє чи дорівнює інпут якісь команді і якщо так тоді запускає те що вона робить
            commands.get(userInput).run();

        } else { //Якщо не відповідає жодній команді
            System.out.println(Ansi.Fore.RED + Ukrainian.InvalidCommand + Ansi.RESET);
        }
    }
  }
}
