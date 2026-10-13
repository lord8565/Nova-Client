# Nova Client (test client for your own server)

Fabric, Minecraft 1.21.11. Open the GUI with **Right Shift**.

## Build
1. https://fabricmc.net/develop -> 1.21.11, copy exact versions into `gradle.properties`.
2. JDK 21, `gradle build` (needs Gradle 9.x; or `./gradlew build` if you have the wrapper). After building, check the jar is not empty: `unzip -l build/libs/nova-client-2.9.0.jar` must list `fabric.mod.json` and `nova/*.class`. Jar: `build/libs/nova-client-2.9.0.jar` (not `-sources`). Put it in `mods/` with Fabric API.

## GUI
Icon tabs on the left, module cards in two columns. Left click = toggle, right click = fold out settings,
middle click = bind key (Esc cancels, Delete clears). Search top right. Config: `config/nova.json`.
Click sounds / accent colour: Client tab (GUI, Theme).

## Schematic builder (World > Schematic)
Easiest way: press "Open builder window" in the Schematic card.
Put `.litematic` files in `.minecraft/schematics` or `config/nova/schematics`. Enable the module, pick the file,
"Load selected file", stand where you want it and press "Origin: my position" (or look at a block + "Origin: block I look at").
It turns smoothly to each face (Aim speed / Max turn), only clicks when the crosshair is on that face, stays in reach.
"Need N item" shows what is missing. In survival it opens chests/barrels within reach and takes the missing items; creative fills from nothing.
"Load into Litematica" (default on) hands the loaded file + origin + rotation to Litematica as a placement, so Litematica renders it (needs the Litematica mod; it follows origin/rotation/offset changes and is removed on Unload).
"Built-in preview" is the old outline renderer, off by default because it is slow; only use it without Litematica.
"Auto walk" walks (W + jump, smooth turning) to the nearest spot where the next block can be placed, then places as usual. "Show path" draws the route.

## Changes in 2.8.0
- **Stand-alone Schematic Builder window.** World > Schematic > "Open builder window" (first button in the card). Same look as the menu (accent, window colour, glow, background image), three tabs:
  - *Build*: clickable list of your `.litematic` files (double click = load, green dot = loaded), overview card with name / size / origin, progress bar, built / total, wrong blocks, blocks per minute + ETA, layer, status and errors; position card with "Origin: my position / crosshair", rotation and origin-anchor switches and X / Y / Z offset steppers (right click = 8 blocks); big Start / Stop button and Unload.
  - *Materials*: every item the schematic still needs with icon, how many you carry, a bar, and what is missing (in stacks).
  - *Settings*: all Schematic settings in foldable groups (Placement, Position, Restock, Walking & filler, Fixing, Display). Anything not in a group lands under "Other", so nothing is hidden.
  - A status pill in the header shows NO FILE / NO ORIGIN / READY / BUILDING / DONE, and the footer tells you the next step. "< Modules" goes back to the normal menu.
- Builder logic is unchanged apart from a small speed tracker (blocks finished per minute) and read-only accessors for the window.

## Changes in 2.9.0
- **26 new modules** (the Anubis idea list, written fresh in Nova's own style; Schematic builder and menu are untouched). All of them appear in their tab automatically, with settings, keybinds and config saving like every other module.
  - *Combat*: **FriendGuard** (middle click a player = friend; KillAura, AimAssist, TriggerBot, AutoClicker, ShieldBreaker and CrystalAura skip friends, ESP/NameTags show them blue), **Mace** (swap + smash while falling), **WTap** (W Tap / S Tap after each hit), **JumpReset**, **ShieldBreaker** (axe swap), **CrystalAura** (place + break end crystals).
  - *Movement*: **ElytraFly** (Control / Boost, auto start), **ElytraSwap**, **BoatFly**, **EntitySpeed**, **ClickTP** (right click a block, sent in 8-block steps), **TridentBoost**, **AirStuck**.
  - *Render*: **Trajectories** (bow, crossbow, trident, pearl, snowball, potion, XP bottle), **PearlTrajectory** (every pearl in the air), **TargetESP** (Ring / Halo / Box), **BlockOverlay**, **JumpCircles**, **HitParticles**, **LowHealthWarning**, **Zoom**.
  - *Misc*: **PlayerDetection**, **WeatherNotifier**, **CoordSnapper**, **RenderDistanceToggle**.
- Not ported (they need mixins, shaders, ImGui or are server-specific): Xray, Freecam, Freelook, NoJumpDelay, FastUse, MotionBlur, ColorGrading, Atmosphere, Character3D, Spotify HUD, NameProtect, the DonutSMP macros and the bypass modules.
- Source only: written without the game libraries, run `./gradlew build` and check the log before using it. Mapping names to look at first if the build complains: `LivingEntity.isGliding` / `isUsingRiptide`, `ClientCommandC2SPacket.Mode.START_FALL_FLYING`, `PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, onGround, horizontalCollision)`, `VehicleEntity`, `World.getOtherEntities`, `ParticleManager.addParticle`, `Keyboard.setClipboard`.

## Changes in 2.8.7
- **Litematica layers (new setting, Display group).** Three options, kept in sync with the builder's layer limit:
  - *All layers*: Litematica renders everything.
  - *One layer*: only one layer is rendered: the one being built in Layer by layer mode, otherwise *First layer*.
  - *Layer range* (default): *First layer* to *Last layer* (a single layer when both are equal). With no limit set, Litematica is left as you had it.
  Unloading, disabling the module, or opening the range again gives Litematica back the render mode it had before. Uses reflection; if your Litematica version has other method names it prints one chat message and leaves Litematica alone.

## Changes in 2.8.6
- **Fixed: wrong block states in the schematic builder.**
  - Note blocks: the note is now part of the state check. A note block with the wrong note is tuned by right-clicking it (want - current) mod 25 times instead of being broken and re-placed. Clicks are counted by the builder (the client does not predict the new note); lost clicks fall back to the real value, and after 4 lost-click rounds the block is given up. Tuning is skipped while sneaking with an item in hand.
  - Pistons, observers, droppers/dispensers, barrels: 6-way facing (up/down included) is compared when checking a block, and the look direction must clearly point the right way (near-ties rejected), both for the rotation the server knows and the new aim, before clicking.
  - Hoppers, end rods, lightning rods, amethyst clusters, shulker boxes: facing is taken from the clicked face, so a neighbour on the right face is required.
- Source only: edited without the game libraries, run `./gradlew build` and check the log before using it. Mapping names (Properties.NOTE, HOPPER_FACING, LightningRodBlock, ...) were not compiled against 1.21.11.

## Changes in 2.8.5
- **Fixed: the builder sometimes skipped blocks within a layer.** Three causes:
  - A block was flagged done as soon as the client showed it, before the server confirmed it. If the server refused it, the block reverted but stayed flagged. Placed blocks now wait ~8 ticks for confirmation, and the layer being built (and the one below) is compared with the real world twice a second; wrong or missing blocks are flagged again and rebuilt.
  - In *Layer by layer* mode, a stuck layer was skipped right after its parked blocks were un-parked, so they were never retried. A stuck layer now gets one real retry first; only a second stuck round skips it (the chat message says how many blocks are left).
  - Nothing checked a layer before leaving it. The builder now verifies the layer against the world before moving up, and verifies every layer once more before it reports "finished" (also before *Disable when finished* acts).
- **Builder window: layer check line** in the Overview card: `Layer 12 complete  340 / 340 verified`, `Layer 12: 337 / 340  3 missing`, or `Final check: layer N / total ...`. "Everything is built." only shows after the final check passes.
- Source only: edited without the game libraries, run `gradle build` and check the log before using it.

## Changes in 2.8.4
- **Choose which layer to build, in the builder window.** Build tab > new *Layers* card: *All*, *One layer* or *Range*.
  - *One layer*: `-` / `+` steps through the layers (right click = 8), *Lowest open* jumps to the lowest layer that is not finished yet.
  - *Range*: separate *From* / *To* steppers.
  - A strip under the buttons shows every layer (bottom = left): the chosen layers are highlighted, the colour turns green as a layer gets built. Click it to pick a layer (in Range mode the nearest end moves, right click moves *To*); hover shows that layer's block count.
  - The info line shows `Layer 5: 120 / 340 blocks (35%)` for the selection. The Overview card and the in-game HUD now say `Layer N only` / `Layers A-B` when only part of the schematic is selected.
  - Changing the layers recounts straight away (progress, materials and the restock list follow at once instead of after ~2 s) and drops targets from the old selection.
  - Same two settings as before (*First layer* / *Last layer*, Settings tab); a First layer above the schematic height or above Last layer can no longer hide the whole schematic.
- Source only: edited without the game libraries, run `gradle build` and check the log before using it.

## Changes in 2.8.3
- **Slow aiming no longer parks blocks.** While the view was still turning towards a block (low "Aim speed" / small "Max turn"), every tick counted as a failed placement, so after 40 ticks the builder gave up on a perfectly good block. Ticks spent turning now have their own, much longer limit (200 ticks); real failures still park after 40.
- Switching the module on no longer prints "N file(s) found" in chat every time (the Refresh button still does).
- A module's HUD code that throws is now logged once instead of being swallowed silently.
- Source only: this version was edited without a compiler, run `gradle build` and check the log before using it.

## Changes in 2.8.2
- **Less robotic aiming.** The view now turns with limited acceleration (speeds up, glides, settles, never overshoots) and a slow drift instead of a fixed share of the angle plus white noise per tick. With nothing to place, the view turns gently towards where you walk (`Look where I walk`).
- **Walking.** Diagonal steps on flat ground (`Diagonal walking`) instead of a left-right staircase. Jumping is a tap with a cooldown instead of a held key. Sprint on long walks (`Sprint on long walks`), sneak-at-edge no longer flickers, and occasional short pauses (`Occasional pauses`).
- **Fixed endless jumping against a wall.** Stuck detection now measures progress towards the waypoint (the old check counted the Y movement of the jumps as movement, so it never fired). Wall-triggered jumps are limited to two per waypoint, then the route is dropped.
- **Chest memory.** The builder remembers what each chest held after it looked inside (`Chest memory (min)`, default 10) and does not walk back to chests that cannot have what is missing; chests known to have it are preferred over unexplored ones. Forgotten on Unload / dimension change.

## Changes in 2.8.1
- **Fixed the empty jar / GUI not opening.** The 2.8.0 jar contained no classes (478 bytes, only a manifest), so Fabric had nothing to load and Right Shift did nothing. Cause: the source files were flat, but Gradle only compiles `src/main/java/<package>/*.java`. This project now has the real folder layout (`src/main/java/nova/{core,gui,modules,schem}`, `src/main/resources/fabric.mod.json`), and `build.gradle` fails the build if `NovaClient.java` is not where it belongs, so an empty jar can no longer be produced silently.
- **Everything from 2.7.5 is back in the source** (the 2.8.0 source was based on 2.7.3): startup splash screen (Client > GUI: "Startup splash", "Splash length", "Preview splash"), **NameTags** and **DropESP** (Render), corner **Shape** Rounded / Squircle / Angular (Client > Layout), and the `Gfx.spanDy` corner maths.
- Builder window: the Start / Stop and Unload buttons are pinned under the scrolling area on the Build tab, so they stay visible at high GUI scales.
- Right Shift now also opens the menu on the title screen (before it only worked inside a world).
- Version in `gradle.properties` is 2.8.1.

## Changes in 2.7.3
- **Fixed the endless chest <-> build walking loop.** A chest trip is only started when a block that still has to be built needs an item you do not carry (or you have no filler at all). Before, "nothing in reach is placeable" was enough, so when the remaining blocks were just unreachable it walked to the chest, came back, found nothing placeable and went to the chest again.
- Restock loop breaker: after two chest trips in a row with no block built in between, restocking pauses for 60 s and says so in chat / the status line.

## Changes in 2.7.2
- **Builder no longer gets stuck on blocks it cannot place or reach.**
  - Blocks the server keeps refusing (clicked 6 times in a row but never staying) are parked instead of being clicked forever.
  - The "can't place" counter is global now; before it restarted whenever the builder switched to another block, so two failing blocks could take turns forever.
  - Parking gets longer every time a block fails (10 s, 20 s, 40 s ... up to ~5 min). Auto unstick and restocking only forget short parks; blocks that failed 3+ times stay parked.
  - New progress watchdog: if no block was finished for 2x "Unstick after (s)" (restock trips excluded) the current target is skipped. Walking back and forth no longer counts as progress.
  - The status line shows "N blocks skipped (cannot reach / place), retrying later".
- **GUI sounds** (Client > GUI): "Sound" list with 23 built-in sounds (note block instruments, bells, buttons, ...), "Play sound" to preview.
  Your own sounds: put `.wav` / `.aiff` / `.au` files in `config/nova/sounds`, press "Refresh sound list" (or just reopen the menu) and pick `File: yourname.wav`.
  "Custom file volume (%)" and "Custom file: pitch follows action" apply to your files. `.mp3` / `.ogg` are not supported, convert them to `.wav`.

## New builder features
- **First layer / Last layer (0 = all)** (Placement): build only a slice of the schematic. Layers are numbered from 1 at the bottom, matching the "Layer N / M" HUD line. Blocks outside the range are ignored, left out of the progress numbers and the material list.
- **Copy material list** (Restock): copies what the schematic still needs to the clipboard, biggest first, with stack counts and a total.
- **Free slots to keep** (Restock, default 0): chest trips leave at least this many empty slots, so there is always room for drops and picked-up items. Topping up a partial stack never counts against the reserve.
- **Pause below health** (Placement, default off): the builder stops walking and placing while your health is under this value and carries on when it recovers.
- **Disable when finished** (Placement, default off): switches the module off when the last block is placed. Either way you now get a chat message with the block count and how long the build took.

## Restock only takes what the layer needs
- New setting **"Restock current layer only"** (World > Schematic > Restock, on by default). Chest trips now fetch only the shortfall of the layer being built (the lowest unfinished layer in continuous mode) instead of up to "Stacks per item" stacks of every material in the whole schematic, so the inventory no longer fills up with things that are not needed yet. Whole stacks are taken, so you can end up with at most one stack more than the layer needs per item. Turn the setting off for the old behaviour.

## Changes in 2.7.1
- **Fixed the restock crash** (`NullPointerException ... Map.get` in handleContainer): after taking a stack out of a chest the game empties the slot on the client, so the item was read as air. It is now read before the click.
- Builder errors in chat now include file, line and method (`BuildModules.java:1529 in handleContainer`).
- "Load into Litematica" tries every `createFromFile` variant of the installed Litematica, so it works with newer versions.

## Changes in 2.7.0
- **Build fix**: the workflow now arranges the files itself. Loose `.java` files in the repo root are moved into `src/main/java/<package>` (by their `package` line) and `fabric.mod.json` into `src/main/resources`, so uploading everything flat no longer fails with "NovaClient.java is missing". A project that was extracted into a sub-folder is picked up too. `build.gradle`, `settings.gradle` and `gradle.properties` still have to be in the repo root.
- **Customisable GUI** (Client tab, all changes are live):
  - *Theme*: accent colour (presets, Rainbow, or Custom RGB) and window colour (Nova, Midnight, Amoled, Slate, Forest, Wine, or Custom RGB). Every shade in the menu is derived from the window colour.
  - *Layout*: window width/height, columns (Auto/1/2/3), corner radius, glow strength, window opacity, gradient + header line on/off.
  - *Background*: your own picture. Drop `.png` / `.jpg` / `.gif` / `.bmp` files into `config/nova/images`, press "Refresh image list" (or just reopen the menu), pick it under "Image". Options: draw on the window or the whole screen, Cover/Stretch, image opacity, dim, pixel size (smaller = sharper, a bit heavier), card opacity.
  - New default look: gradient window with accent tint, header line, rounder corners, soft glow.
- **Schematic builder**
  - Double slabs: a double slab is now built as two slabs (second half clicked onto the first). Before, one half counted as done. Item counts ("Need N ...") count two slabs for it.
  - Blocks that depend on the clicked face are placed against the right face: wall torches, ladders, wall signs/banners/heads, tripwire hooks, coral fans (facing = clicked side), standing torches (top face), levers/buttons/grindstones (floor / wall / ceiling).
  - More block types keep their orientation: beds, repeaters, comparators, ender chests, lecterns, stonecutters, looms, carved pumpkins / jack o'lanterns, anvils.

## Changes in 2.6.4
- **Restock bug fixed (builder stopped after taking items from a chest).** After a restock the chest was flagged "emptied" even though it still had items, and that flag only cleared after ~30 s of idle ticks, so with one chest the builder sat there doing nothing. Now a chest is only skipped when it had nothing you need (and only for 60 s, then it is checked again). Both halves of a double chest count as one chest.
- **Marked restock chests** (World > Schematic): "Mark / unmark chest I look at", "Mark chests near me" (everything within 6 blocks, handy for a wall of double chests), "Clear marked chests". Marked chests are outlined in green ("Show marked chests"). "Restock from": *Marked, else any* (default, same as before when nothing is marked), *Marked only*, *Any nearby*. Chest range still applies to marked chests. Marks are cleared when you change world/dimension.
- "Stacks per item" (default 9): how many stacks of one item it takes per chest visit, so one block type does not fill the whole inventory. It also stops when the inventory is full instead of looping.
- **Build mode**: *Continuous* (as before) or *Layer by layer* (finishes the lowest layer completely, restocking when needed, before it goes up). The HUD shows "Layer N / M".
- **Temporary filler / no more getting stuck**:
  - "Restock filler too": a stack of the filler block is taken from the chests as well (before, filler only worked if you happened to carry some).
  - "Remove filler as I build": temporary blocks are broken again as soon as nothing of the schematic is waiting near them (not only when everything is done).
  - "Auto unstick" / "Unstick after (s)": if nothing was placed, taken or walked for that long, parked blocks and skipped chests are forgotten and it retries; in layer mode it skips a layer it cannot finish and comes back to it later.
- Blocks that have no item (portal, fire, ...) count as done so they can no longer hold up a layer.

## Changes in 2.6.3
- Restock chests are found from far away: "Chest range" (World > Schematic, default 64 blocks, up to 160) scans every loaded chunk in range for chests and barrels (trapped and ender chests are skipped), the list is cached so it costs nothing per tick. Before it only looked about 16 blocks around you.
- Chest trips no longer get stuck: the route search used to be boxed in around you and the schematic, so a far chest was either not found or there was no route back. Chest trips now search a wide area with a bigger node budget, and the route back to the schematic uses one box around the schematic and where you stand.
- After restocking, blocks that were parked while there was no material are tried again straight away (previously they could sit parked for ~10 s, which looked like the builder had switched itself off).
- An error inside the builder no longer switches the module off: it prints "Schematic error (kept running): ..." once in chat and in the progress HUD, closes any open container, and carries on. If you see that message, send it to me.

## Changes in 2.6.2
- Wrong blocks are detected and fixed. A block now only counts as built when it is the right block AND has the right facing (stairs, doors, gates, chests, furnaces, glazed terracotta), axis (logs, pillars), and top/bottom half (slabs, stairs, trapdoors). Before, any block of the same type counted as done, so wrongly rotated or flipped blocks were left alone, and a wrong block in a spot blocked it forever.
- "Fix wrong blocks" (World > Schematic, on): wrong blocks that the builder placed itself (including filler that ended up on a schematic spot) are broken (best tool is picked in survival) and placed again. If one comes out wrong 3 times it is accepted as it is, so it can never loop forever. Walks to wrong blocks that are out of reach; they are outlined in red.
- "Replace other wrong blocks" (off): also break wrong blocks the builder did NOT place (terrain, other builds) inside the schematic. Only on when you want the schematic to replace whatever is there; it never breaks unbreakable blocks or blocks whose replacement item it cannot get.
- Progress HUD shows how many wrong blocks there are.

## Changes in 2.6.1
- GUI glow reworked to match the orange look: soft wide ambient glow around the window (smooth quadratic falloff, no more hard rings), brighter accent rim, enabled cards get a soft glow + accent border + top sheen, toggles, tabs and slider fills glow wider. Default accent is a warmer orange.

## Changes in 2.6.0
- Filler blocks (World > Schematic > "Filler blocks", on by default; pick the block with "Filler block"):
  - The route search can now bridge: where there is nothing to stand on it plans a filler block (flat bridge, or a two-block stair step up). Walking crouches and puts the block down before stepping onto it.
  - If the next pending block has nothing to click against (floating part, schematic starting in the air), the builder walks to the ground under it and builds a filler column up to it, then places the real block on top.
  - Filler blocks are never put on a cell that belongs to the schematic.
  - "Remove filler when done" breaks all filler blocks again once the whole schematic is built.
- The builder tries many targets before it says "No route".

## Changes in 2.5.1
- Route search now prefers the part that is already built: standing on built blocks is cheap, stepping off them is expensive, so the builder stays on the built part whenever a route on it exists.
- Route search tries the 24 nearest placeable blocks at once instead of one block at a time (a single unreachable block no longer stops the build), is limited to the area around the schematic, and has a bigger node budget.
- Walk goals use a 0.6 smaller reach so arriving a little off the spot centre still lets the block be placed.
- Placement: blocks with a facing (stairs, doors, gates, chests, furnaces, glazed terracotta) are only clicked once the server already has the new rotation; slab/stair/trapdoor halves are checked against where the crosshair really is, and faces that would give the wrong half are not used; interactive blocks (chests, doors, buttons, ...) are never used as the block to click against; solid blocks are not placed into the space you stand in.

## Changes in 2.5.0
- Auto walk never turns the camera: it presses forward/back/strafe keys relative to where you look, so it can aim at a block and walk at the same time (no look-forward / look-back cycles). It keeps walking while it places blocks in reach.
- Lava: cells next to lava are not used for routes, and walking stops with "Lava ahead" if the next step or what is under it is lava.
- "Sneak at edges": crouches while walking when the ground ahead is missing, except on steps where the route itself goes down (drops, ladders, water).

## Changes in 2.4.0
- Schematic is now loaded into Litematica automatically (reflection, no hard dependency); the built-in outline preview is off by default (it caused the lag).
- Progress recount runs in slices instead of one big pass, and out-of-reach blocks are rejected cheaply before planning.

## Changes in 2.3.0
- Schematic builder walks to chests/barrels (within ~16 blocks) when it runs out of material, takes what is missing, then goes back to building. Unreachable chests are skipped.
- Path finder now handles doors and fence gates (opens them on the way, not iron doors), ladders and vines (climb up/down) and water (wades/swims, drops into water).
- Blocks that need a facing (stairs, doors, gates, chests, furnaces, glazed terracotta) are now planned from the direction you will be looking when you click: the builder picks a click face and standing spot where that direction matches, instead of failing after turning.

## Changes in 2.2.0
- GUI keeps its tab, scroll position, search text and unfolded cards when you close and reopen it.
- Neon look: pulsing halo around the window, enabled modules, toggles, slider fills, selected tab and the title.
- Schematic builder: ghost preview of the schematic, auto walk with a simple path finder (flat / 1 up / up to 3 down), walk target selection, route preview, and blocks that can't be clicked are parked instead of blocking the build.

## Changes in 2.1.1
- ChunkPos is read through helpers (U.cx / U.cz) so it compiles whether ChunkPos is a record or a class with fields.
- Module errors in onEnable/onDisable are now written to the log instead of being swallowed.
- Right Shift key edge detection no longer goes stale while the GUI is open.
