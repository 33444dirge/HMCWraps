# HMCWraps 1.8.2 悬浮预览修复日志（2026-10-11）

## 故障与修复

原始报错为玩家潜行取消预览时，`FloatingPreview.cancel()` 对尚未赋值的任务调用 `Optional.of(...)`，导致 `NullPointerException`。此前已将 `FloatingPreview` 和 `HandPreview` 的任务取消改为 `Optional.ofNullable(...)`，保留非空任务的正常取消行为。

针对《HMCWraps-悬浮预览修复审计报告-2026-10-11.md》指出的两项遗漏，本次又完成以下修复：

1. **HMC-PREVIEW-01：生成包实体类型兼容。**现代版本先从 `EntityType.ARMOR_STAND` 取值；该字段不存在时，改从 `EntityTypes.ARMOR_STAND` 取值。生成包构造器仍使用 `EntityType` 参数类型，旧版的 `EntityTypes.d` 分支不变。
2. **HMC-PREVIEW-02：初始化失败回滚。**`PreviewManager` 在 `preview()` 抛出异常时，只撤销本次预览登记，调用 `cancel(false)` 取消已创建任务并安排客户端展示清理，不重新打开菜单；清理自身失败时，原始异常仍保留，清理异常作为附加异常。正常移除预览时先从登记表取出旧预览，再执行关闭回调，避免回调中建立的新预览被误删。

## 构建与验证

- 执行 `gradlew.bat build --offline --no-daemon`，构建成功；项目的 Gradle 测试任务显示 `NO-SOURCE`。
- 检查最终 JAR：包含 `plugin.yml`、预览管理器及两种预览类；字节码包含 `EntityTypes.ARMOR_STAND` 回退查找和失败登记回滚。
- 使用 Java 26 对本机两份 Shiroha 26.2 核心进行受控反射检查：`EntityTypes.ARMOR_STAND` 的类型可作为生成包构造器的 `EntityType` 参数，生成、元数据、传送、装备、销毁、旋转和发包方法的相关签名均可解析。两份本机核心的 SHA-256 分别为 `4DF70D87B413890868A9731A1BC8B9C1673E8F8D244CB11D446A596C8536106C` 和 `08BA1EC1A8EFD14F8FE43972F3872012550B61BA1CC808DFC8F483B6D7BE7035`。
- 对最终 JAR 执行受控预览管理器测试：启动异常会撤销登记并以 `open=false` 清理；清理异常不会覆盖原始异常；关闭回调中登记的新预览不会被误删。

上述本机核心与审计报告中指定核心的哈希不同；反射检查没有实际发送数据包，也未在游戏服务器完成展示、取消、重复预览或 reload 验收。

## 成品

- JAR：`build/libs/HMCWraps-1.8.2.jar`（3,153,781 字节）
- SHA-256：`A92CB43688F4CAE0ECD0727EF60B26936D66AB15A8AB70B08140988A8741AAFA`

该 JAR 按当前工作区构建，也包含工作区内其他尚未提交的代码改动；版本号仍为 `1.8.2`。部署时应停服替换旧 JAR，再按审计报告的验收步骤复测。
