# HMCWraps 性能优化更新

## 问题描述

HMCWraps插件在Folia服务器上出现了性能问题，导致服务器超时。错误日志显示主要问题出现在以下几个位置：

1. `PermissionUtil.loopThroughInventory`方法
2. `WrapperImpl.getWrap`方法
3. `PlayerPickupListener`中的事件处理

## 解决方案

### 1. 添加事件检测配置开关

创建了一个新的配置类`EventSettings`，允许管理员控制哪些事件应该被处理：

```yaml
events:
  # 如果插件应该在玩家拾取物品时检查包装
  player-pickup: true
  # 如果插件应该在玩家丢弃物品时检查包装
  player-drop: true
  # 如果插件应该在玩家加入服务器时检查包装
  player-join: true
  # 如果插件应该在玩家点击库存时检查包装
  inventory-click: true
  # 如果插件应该在从发射器装备盔甲时检查包装
  dispenser-armor: true
  # 事件后检查库存前的最大延迟（以帮助提高性能）
  max-inventory-check-delay: 5
```

### 2. 优化PermissionUtil.loopThroughInventory方法

重写了`loopThroughInventory`方法，使其更加高效：

- 减少对`inventory.getContents()`的调用次数
- 添加空值检查以防止NPE
- 使用批处理方式处理物品，减少性能影响
- 移除了可能导致主线程阻塞的延迟

### 3. 更新事件监听器

修改了所有相关的事件监听器，使其使用新的配置设置：

- `PlayerPickupListener`
- `PlayerJoinListener`
- `PlayerDropListener`
- `InventoryClickListener`
- `DispenserArmorListener`

### 4. 添加可配置的延迟

为所有事件处理添加了可配置的延迟，以减少对服务器性能的影响：

```java
// 使用可配置的延迟来帮助在Folia服务器上提高性能
int delay = Math.min(plugin.getConfiguration().getEvents().getMaxInventoryCheckDelay(), 1);
```

## 使用方法

1. 更新插件后，编辑`config.yml`文件中的`events`部分
2. 根据服务器性能需求，禁用不必要的事件处理
3. 调整`max-inventory-check-delay`值以找到性能和功能之间的平衡

## 性能建议

1. 如果服务器性能仍然有问题，可以尝试将`max-inventory-check-delay`增加到更高的值
2. 对于大型服务器，考虑禁用`player-pickup`和`inventory-click`事件，因为这些是最频繁触发的事件
3. 定期检查服务器日志，确保没有其他性能问题

## 注意事项

- 这些更改主要是为了解决Folia服务器上的性能问题
- 在非Folia服务器上，这些更改仍然有效，但可能不会产生显著的性能提升
- 禁用某些事件可能会影响插件的部分功能，请根据实际需求进行配置