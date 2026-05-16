/*
OtherMenu.java
Тут є додаткові меню наприклад меню допомоги і інші
*/

import Core.Info;
import Core.Lib.Ansi;
import Lang.Ukrainian;

public class OtherMenu {

    public static class HelpMenu {
        //Меню допомоги
        public static void ShowHelpMenu() {
            System.out.println(Ansi.Fore.CYAN + Ukrainian.HelpComInfo + Ansi.RESET);
            System.out.println(Ansi.Fore.CYAN + Ukrainian.TestModeComInfo + Ansi.RESET);
        }
        
        //Інформація про програму
        public static void ShowProgramInfo() {
            System.out.println(Ansi.Fore.YELLOW + Ukrainian.VersionInfo + Info.ProgramVersion + Ansi.RESET);
            System.out.println(Ansi.Fore.YELLOW + Ukrainian.CoreVersionInfo + Info.CoreVersion + Ansi.RESET);
            System.out.println(Ansi.Fore.YELLOW + Ukrainian.DeveloperInfo + Info.Developer + Ansi.RESET);
            System.out.println(Ansi.Fore.YELLOW + Ukrainian.BuildInfo + Info.Build + Ansi.RESET);
        }

    }
}
