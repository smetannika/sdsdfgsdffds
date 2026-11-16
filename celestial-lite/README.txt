Celestial Lite - Fabric 1.16.5, Java 8 target

Build (needs JDK 17 for Gradle 7.3.x; output is Java 8 bytecode):
  1) gradle wrapper --gradle-version 7.3.3     (once, or just open the folder in IntelliJ and reload Gradle)
  2) gradlew build
  3) jar: build/libs/celestial-lite-1.0.0.jar  -> put into .minecraft/mods (needs Fabric Loader + Fabric API 0.42.0+1.16)

Every module is toggled by a key: Options > Controls > "Celestial Lite" (all unbound by default). Settings are static fields at the top of each module class.
Add a module: extend Module, register it in CelestialLite.java and add a key name to lang/en_us.json.

Friends: middle-click a player to add/remove a friend (FriendProtect + Attack Aura/Trigger Bot skip friends).
Not ported (their logic was in classes that were not uploaded): AutoDuel, AutoUse, FastSwap, Popka.

Build on GitHub: push this folder to a repo -> Actions tab -> "Build mod" -> download artifact "celestial-lite" (workflow: .github/workflows/build.yml).
