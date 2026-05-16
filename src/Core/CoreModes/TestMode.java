/*
TestMode.java
Використовується для тестування функцій не в основній програмі
Це один з режимів програми
*/

package Core.CoreModes;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import Core.Lib.Ansi;
import Lang.Ukrainian;

public class TestMode {
    public static void main(String[] args) {

    @SuppressWarnings("resource")
    Scanner scanner = new Scanner(System.in);
    
    //Стартовий текст
    System.out.println(Ansi.Fore.BLUE + "Test Mode" + Ansi.RESET);

    //Всі команди
    Map<String, Runnable> commands = new HashMap<>();
    commands.put("test", () -> TestModeFunc.Test1.Test1Sound()); //Тест

    while (true) {
        
        System.out.print(Ansi.Fore.RED + ">>> " + Ansi.RESET); //Показ перед курсором
        String userInput = scanner.nextLine().toLowerCase(); //Головний інпут

        if (commands.containsKey(userInput)) { //Перевіряє чи дорівнює інпут якісь команді і якщо так тоді запускає те що вона робить
            commands.get(userInput).run();

        } else { //Якщо не відповідає жодній команді
            System.out.println(Ansi.Fore.RED + Ukrainian.InvalidCommand + Ansi.RESET);
        }
    }
  }
}
