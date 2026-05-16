/*
TestModeStart.java
Початкова точка входу в режим налагодження
*/

package Core;

import Core.CoreModes.TestMode;
import Core.Lib.CoreFunc;

public class TestModeStart {
    public static void Start() {
        CoreFunc.ClearConsole();
        TestMode.main(new String[]{});
    }
}
