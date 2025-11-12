package org.example;

import org.bukkit.plugin.java.JavaPlugin;

public class ComboPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("ComboPlugin đã được bật!");

        // 1. Lưu cấu hình mặc định nếu chưa tồn tại.
        // Điều này sẽ copy config.yml từ src/main/resources vào thư mục data của plugin
        // (plugins/ComboPlugin/config.yml) nếu nó chưa có.
        saveDefaultConfig();

        // 2. Tải lại cấu hình. Đảm bảo đọc phiên bản mới nhất.
        reloadConfig();

        // 3. Đọc các giá trị từ config.yml.
        // get<Type>("path.to.key", defaultValue)
        boolean requireSneaking = getConfig().getBoolean("settings.require-sneak", true);
        long comboDuration = getConfig().getLong("combo.duration-ms", 1000L);
        int maxClicks = getConfig().getInt("combo.max-clicks", 4);
        long doubleClickThreshold = getConfig().getLong("combo.double-click-threshold-ms", 50L);
        String comboCompletedMessage = getConfig().getString("messages.combo-completed", "&aCOMBO HOÀN THÀNH!");
        String comboResetMessage = getConfig().getString("messages.combo-reset", "&cCombo đã bị reset.");
        String notSneakingMessage = getConfig().getString("messages.not-sneaking", "&eBạn cần phải sneak để thực hiện combo.");


        // 4. Đăng ký Listener, truyền các giá trị cấu hình đã đọc vào constructor của Combo.
        // Điều này đảm bảo lớp Combo sử dụng các cài đặt tùy chỉnh từ config.yml.
        getServer().getPluginManager().registerEvents(
            new Combo(this, requireSneaking, comboDuration, maxClicks, doubleClickThreshold,
                      comboCompletedMessage, comboResetMessage, notSneakingMessage),
            this
        );
    }

    @Override
    public void onDisable() {
        getLogger().info("ComboPlugin đã được tắt!");
    }
}
