# Lore backup and eco marker repair

HMCWraps used to store lore by replacing every section sign with an ampersand and joining lines with `;!;`. Its color-code reader could not recover eco's non-color `section-z` prefix. When a wrapped item had an original backup but no nonempty added-lore record, removing the wrap restored ordinary `&z` text which eco could not remove.

New backups use Bukkit PDC string lists under the existing keys. Raw legacy strings, literal ampersands, separator text and empty lines are preserved. Existing string backups remain readable: the previous color conversion is retained, and the leading legacy `&z` eco prefix is recovered. The normal APPEND/PREPEND removal path still removes the wrap's added lines from current lore rather than overwriting current lore with an old snapshot. No settings or scheduler changes are required.

The old format irreversibly conflated literal ampersands with section signs. Ambiguous old text cannot be perfectly reconstructed. Already unmarked stale lore is not automatically cleaned. This repair retains the existing Bukkit legacy lore API and does not add arbitrary Adventure component preservation.

## Migration and rollback

Upgrade by backing up the plugin, configuration and item/world/player data, then replacing HMCWraps during a normal server stop. Old string records are read on demand; newly created records use native lists. There is no bulk item scan or config migration.

An old plugin cannot read new list records. Rolling back requires restoring the corresponding pre-upgrade item data backup, or first removing all wraps created by this version using this version. Merely replacing the JAR is not a complete data-format rollback.

## Verification

- Upstream base: `f8c294b8de74e9e5eeb1f0ff6c30d69545c6d99b`. All 180 business classes in an unmodified full build matched the supplied production JAR byte-for-byte.
- Original JAR SHA256: `231199db5b9e47a78dba3a53f6ce72feff6d4900ccdba55afef691ddd7ce3c20`.
- Java 21 compilation, Gradle Wrapper 9.6.1, clean check/build: 8 JUnit tests passed. On the original source, the same suite failed the 3 new defect assertions.
- Runtime: supplied Shiroha/Folia `26.2-DEV-398b90d`, Java 25.0.3, supplied eco `2026.28-optimized-4`, EcoEnchants `2026.27-1` and the existing audit lab plugin stack.
- Same runtime suite: original 36 passed / 8 failed; repaired 44 passed / 0 failed. Counts are assertions, including repeated cycle checks, not distinct player scenarios.
- Covered real eco cleanup, production anniversary wrap through WrapperImpl, subsequent lore and enchantment changes, APPEND/PREPEND, repeated application/removal, literal ampersands, RGB/bold legacy text, separator collisions, blank/absent lore, item byte serialization and unrelated item data fixtures.
- Tests invoked real server item/API/display-module methods on detached test items with a null Player. No graphical client, live GUI session, historical abnormal-item cleanup, combat test or production deployment is claimed.
- The final artifact also excludes four Java source resources inherited from the mclogs dependency; runtime dependency classes are unchanged.

Build with `gradlew.bat clean check build` from an ASCII checkout/output path on Windows. The final artifact is the root `build/libs/HMCWraps-1.8.2.jar`. Plugin descriptor version remains 1.8.2; the delivery file is named HMCWraps-1.8.2-1.jar.
