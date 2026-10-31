# Lore preservation repair

Goal: preserve eco's section-z marker when HMCWraps restores lore, without replacing current lore during normal APPEND/PREPEND removal.

Scope: the supplied audit report section 7, repository f8c294b8de74e9e5eeb1f0ff6c30d69545c6d99b, supplied original JAR SHA256 231199db5b9e47a78dba3a53f6ce72feff6d4900ccdba55afef691ddd7ce3c20.

Evidence: the actual original JAR reproduced the fallback defect in D:/hmcwraps-lore-fix-20261031/baseline.txt. An unmodified full build of the baseline has 180 business classes byte-identical to the supplied JAR. The final repaired JAR changes only LoreModifier and its nested enum (debug line information), and excludes four mclogs Java source resources.

Design: use native PDC string lists for new backups under the existing keys. This preserves raw legacy strings, literal ampersands, delimiters and empty lines without an external codec. Read existing string backups using their old color conversion plus recovery of the leading eco &z marker. Old storage conflated literal & with section signs, so original intent cannot be reconstructed for ambiguous old values. Keep the current removal branch ordering and defaults unchanged. Do not promise recovery of historical unmarked lore or preservation of component features already lost through the existing Bukkit legacy lore API.

Alternatives: blanket ampersand replacement corrupts ordinary text; continuing the separator encoding still loses line boundaries; adding a general serialization dependency is unnecessary.

Execution: inline under the user's existing authorization to verify, fork, repair and test; no additional design approval is required by the developer's authorization rule. Existing isolated clone and codex branch are used.

- [x] Identify supplied artifacts, compare relevant source, reproduce original defect.
- [x] Fork to heoduling/HMCWraps, create codex/lore-preservation-fix.
- [x] Add regression assertions and observe original failure before product edits.
- [x] Update only LoreModifier storage/read helpers; retain existing business branches.
- [x] Run clean check/build, project tests, artifact checks and source/binary comparison.
- [x] Run original/fixed contrast in an isolated server using the supplied bundler and production config copy; exercise fallback, eco cleanup, current-lore updates, APPEND/PREPEND, legacy records and repeated wrapping.
- [x] Inspect changes; package JAR/source and record evidence in the delivery directory.

Build rulings: Java needed the environment's HTTPS proxy to resolve the exact declared dependencies; no dependency version was changed. Gradle test workers could not find classes under the original non-ASCII build output path; directing build output to an ASCII directory fixed execution. The independent JAR check rejected four Java source resources inherited from mclogs, so Shadow now excludes **/*.java. The final clean build passed 8 JUnit tests. The same new tests on the original source failed 3 of 8, as expected.

Runtime evidence: final original run has 36 passing and 8 failing assertions; final fixed run has 44 passing and 0 failing assertions, with no probe errors. Both normal shutdowns exited 0. Startup uses the exact supplied core (SHA256 68d1333f78af3d0d68b9093dbf80d05e5ff9f4aa2d4b1111f992cf58fa431217). Independent read-only review found no blocking issue.
