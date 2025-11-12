package org.example;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Combo implements Listener {
    private final JavaPlugin plugin;
    private List<String> clickSequence = new ArrayList<>();
    private long lastClickTime = 0;

    // Khai báo các biến để lưu trữ các giá trị cấu hình
    private final boolean requireSneaking;
    private final long comboDuration;
    private final int maxCombo;
    private final long doubleClickThreshold;
    private final String comboCompletedMessage;
    private final String comboResetMessage;
    private final String notSneakingMessage;

    // Các hằng số này không cần lấy từ config vì ít khi thay đổi
    private static final int TICKS_DISPLAY = 15; // Thời gian hiển thị subtitle (ticks)
    private static final int TICKS_DELAY = 20; // Độ trễ kiểm tra xóa combo (1 giây)

    private static final String PENDING_SLOT = ChatColor.GRAY + "?";
    private static final String LEFT_CLICK_TEXT = ChatColor.RED + "TRÁI";
    private static final String RIGHT_CLICK_TEXT = ChatColor.GREEN + "PHẢI";

    // Constructor mới nhận các tham số cấu hình
    public Combo(JavaPlugin plugin, boolean requireSneaking, long comboDuration, int maxCombo,
                 long doubleClickThreshold, String comboCompletedMessage,
                 String comboResetMessage, String notSneakingMessage) {
        this.plugin = plugin;
        this.requireSneaking = requireSneaking;
        this.comboDuration = comboDuration;
        this.maxCombo = maxCombo;
        this.doubleClickThreshold = doubleClickThreshold;
        // Chuyển đổi mã màu Minecraft (&) sang màu thực tế
        this.comboCompletedMessage = ChatColor.translateAlternateColorCodes('&', comboCompletedMessage);
        this.comboResetMessage = ChatColor.translateAlternateColorCodes('&', comboResetMessage);
        this.notSneakingMessage = ChatColor.translateAlternateColorCodes('&', notSneakingMessage);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() != Action.LEFT_CLICK_AIR &&
                event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        // Sử dụng giá trị requireSneaking từ config
        if (requireSneaking && !player.isSneaking()) {
            player.sendMessage(notSneakingMessage); // Gửi tin nhắn từ config
            return;
        }

        long currentTime = System.currentTimeMillis();

        // Sử dụng doubleClickThreshold từ config
        if (currentTime - lastClickTime < doubleClickThreshold) {
            return;
        }

        // Sử dụng comboDuration từ config
        if (currentTime - lastClickTime >= comboDuration) {
            if (!clickSequence.isEmpty()) { // Chỉ gửi tin nhắn reset nếu có combo đang diễn ra
                player.sendMessage(comboResetMessage); // Gửi tin nhắn reset từ config
            }
            clickSequence.clear();
        }

        String clickText = null;

        if (event.getAction() == Action.LEFT_CLICK_AIR) {
            clickText = LEFT_CLICK_TEXT;
        } else if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            clickText = RIGHT_CLICK_TEXT;
        }

        // Sử dụng maxCombo từ config
        if (clickText != null && clickSequence.size() < maxCombo) {
            clickSequence.add(clickText);
            lastClickTime = currentTime;

            StringBuilder comboDisplay = new StringBuilder();

            for (int i = 0; i < clickSequence.size(); i++) {
                comboDisplay.append(clickSequence.get(i));
                if (i < maxCombo - 1) { // Sử dụng maxCombo từ config
                    comboDisplay.append(ChatColor.RESET + "-" + ChatColor.RESET);
                }
            }

            for (int i = clickSequence.size(); i < maxCombo; i++) { // Sử dụng maxCombo từ config
                comboDisplay.append(PENDING_SLOT);
                if (i < maxCombo - 1) { // Sử dụng maxCombo từ config
                    comboDisplay.append(ChatColor.RESET + "-" + ChatColor.RESET);
                }
            }

            String subtitleText = comboDisplay.toString();

            // Sử dụng maxCombo từ config
            if (clickSequence.size() >= maxCombo) {
                player.sendTitle("", subtitleText, 0, 1, 0);
                player.sendMessage(comboCompletedMessage); // Gửi tin nhắn hoàn thành từ config
                clickSequence.clear();
            } else {
                player.sendTitle("", subtitleText, 0, TICKS_DISPLAY, 0);
            }
        }

        // Lên lịch xóa chuỗi combo sau thời gian comboDuration nếu chưa đạt maxCombo
        // Sử dụng maxCombo và comboDuration từ config
        if (clickSequence.size() > 0 && clickSequence.size() < maxCombo) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (System.currentTimeMillis() - lastClickTime >= comboDuration) {
                        if (!clickSequence.isEmpty()) {
                            player.sendMessage(comboResetMessage); // Gửi tin nhắn reset từ config
                            clickSequence.clear();
                        }
                    }
                }
            }.runTaskLater(plugin, TICKS_DELAY);
        }
    }
        }
