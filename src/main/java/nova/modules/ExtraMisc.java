package nova.modules;

import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import nova.core.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Misc modules ported from the Anubis module list. */
public final class ExtraMisc {
    private ExtraMisc() {}

    public static class PlayerDetection extends Mod {
        Setting.Num range = num("Range", 128, 16, 512, 16);
        Setting.Bool chat = bool("Chat alert", true), sound = bool("Sound", true), leaving = bool("Leave alert", false);
        final Set<UUID> inRange = new HashSet<>();
        final Map<UUID, String> names = new HashMap<>();
        boolean first = true;
        int t;

        public PlayerDetection() { super("PlayerDetection", Category.MISC, "Tells you when another player comes into range."); }

        @Override public void onEnable() { inRange.clear(); names.clear(); first = true; t = 0; }

        @Override public void onTick() {
            if (t++ % 5 != 0) return;
            Set<UUID> now = new HashSet<>();
            for (PlayerEntity pl : mc.world.getPlayers()) {
                if (pl == mc.player || DefenseModules.AntiBot.isBot(pl) || mc.player.distanceTo(pl) > range.get()) continue;
                now.add(pl.getUuid());
                names.put(pl.getUuid(), pl.getName().getString());
                if (!first && !inRange.contains(pl.getUuid())) {
                    if (chat.get()) U.chat(pl.getName().getString() + " is " + (int) mc.player.distanceTo(pl) + " blocks away.");
                    if (sound.get()) mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_PLING, 1.0f));
                }
            }
            if (leaving.get() && !first) {
                for (UUID id : inRange) if (!now.contains(id)) {
                    U.chat(names.getOrDefault(id, "A player") + " left your range.");
                }
            }
            inRange.clear();
            inRange.addAll(now);
            first = false;
        }
    }

    public static class WeatherNotifier extends Mod {
        boolean rain, thunder, init;

        public WeatherNotifier() { super("WeatherNotifier", Category.MISC, "Chat message when rain or thunder starts or stops."); }

        @Override public void onEnable() { init = false; }

        @Override public void onTick() {
            boolean r = mc.world.isRaining(), th = mc.world.isThundering();
            if (init) {
                if (th != thunder) U.chat(th ? "Thunderstorm started." : "Thunderstorm ended.");
                else if (r != rain && !th) U.chat(r ? "It started raining." : "The rain stopped.");
            }
            rain = r; thunder = th; init = true;
        }
    }

    /** Copies your coordinates to the clipboard, then switches itself off. */
    public static class CoordSnapper extends Mod {
        public CoordSnapper() { super("CoordSnapper", Category.MISC, "Copies your X Y Z to the clipboard, then turns itself off."); }

        @Override public void onEnable() {
            enabled = false;
            var p = mc.player;
            if (p == null) return;
            String s = (int) Math.floor(p.getX()) + " " + (int) Math.floor(p.getY()) + " " + (int) Math.floor(p.getZ());
            mc.keyboard.setClipboard(s);
            U.chat("Copied " + s);
        }
    }

    /** Switches between your normal view distance and a low one (less lag in fights / farms). */
    public static class RenderDistanceToggle extends Mod {
        Setting.Num low = num("Low distance (chunks)", 4, 2, 16, 1);
        int old = 12;

        public RenderDistanceToggle() { super("RenderDistanceToggle", Category.MISC, "While on, uses the low view distance; off restores yours."); }

        @Override public void onEnable() {
            old = mc.options.getViewDistance().getValue();
            mc.options.getViewDistance().setValue(low.getInt());
        }

        @Override public void onTick() {
            if (mc.options.getViewDistance().getValue() != low.getInt()) mc.options.getViewDistance().setValue(low.getInt());
        }

        @Override public void onDisable() { mc.options.getViewDistance().setValue(old); }
    }
}
