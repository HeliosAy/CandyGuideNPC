package com.heliosay.candyGuideNPC.music;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.FadeType;
import com.xxmicloxx.NoteBlockAPI.model.playmode.MonoStereoMode;
import com.xxmicloxx.NoteBlockAPI.model.playmode.StereoMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class MusicManager {
    private final JavaPlugin plugin;
    private final Map<Player, RadioSongPlayer> activePlayers;
    private final Map<String, Song> loadedSongs;
    private final Map<Player, String> playerCurrentSongs;
    private boolean isNoteBlockAPIAvailable;

    private static final int FADE_OUT_DURATION = 60;
    private static final int FADE_IN_DURATION = 40;

    public MusicManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.activePlayers = new HashMap<>();
        this.loadedSongs = new HashMap<>();
        this.playerCurrentSongs = new HashMap<>();
        this.isNoteBlockAPIAvailable = checkNoteBlockAPI();

        if (isNoteBlockAPIAvailable) {
            plugin.getLogger().log(Level.INFO, "NoteBlockAPI Bulundu");
            createMusicFolder();
        } else {
            plugin.getLogger().log(Level.INFO, "NoteBlockAPI Bulunamadı");
        }
    }

    private boolean checkNoteBlockAPI() {
        try {
            Class.forName("com.xxmicloxx.NoteBlockAPI.NoteBlockAPI");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private void createMusicFolder() {
        File musicFolder = new File(plugin.getDataFolder(), "music");
        if (!musicFolder.exists()) {
            musicFolder.mkdirs();
            plugin.getLogger().log(Level.WARNING, "Müzik klasoru oluşturuldu: " + musicFolder.getPath());
        }
    }

    /**
     * Belirtilen dosya adından şarkıyı yükler
     * @param fileName .nbs uzantısı olmadan dosya adı
     * @return Başarılı ise true
     */
    public boolean loadSong(String fileName) {
        if (!isNoteBlockAPIAvailable) {
            return false;
        }

        try {
            File songFile = new File(plugin.getDataFolder(), "music/" + fileName + ".nbs");

            if (!songFile.exists()) {
                plugin.getLogger().log(Level.WARNING, "Şarkı dosyası bulunamadı: " +songFile.getPath());
                return false;
            }

            Song song = NBSDecoder.parse(songFile);
            if (song == null) {
                plugin.getLogger().log(Level.WARNING, "Şarkı dosyası okunamadı: " + fileName);
                return false;
            }

            loadedSongs.put(fileName.toLowerCase(), song);
            plugin.getLogger().log(Level.WARNING, "Şarkı yüklendi: " + fileName);
            return true;

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Şarkı yüklenirken hata oluştu: " + fileName, e);
            return false;
        }
    }

    /**
     * Bir haritadan (bölge adı -> şarkı adı) birden fazla şarkıyı yükler.
     * @param regionSongMap Bölge adı ile şarkı adının eşleştiği harita.
     */
    public void loadSongsFromConfig(Map<String, String> regionSongMap) {
        if (!isNoteBlockAPIAvailable) {
            plugin.getLogger().log(Level.WARNING, "NoteBlockAPI aktif değil. Müzikler iptal edilecek");
            return;
        }

        if (regionSongMap == null || regionSongMap.isEmpty()) {
            plugin.getLogger().log(Level.WARNING, "Yüklenicek bölge müziği bulunamadı");
            return;
        }
        plugin.getLogger().log(Level.WARNING,"Config dosyasından müzikler yükleniyor...");
        for (Map.Entry<String, String> entry : regionSongMap.entrySet()) {
            String songName = entry.getValue();
            if (!loadedSongs.containsKey(songName.toLowerCase())) {
                loadSong(songName);
            }
        }
        plugin.getLogger().log(Level.WARNING, loadedSongs.size() + "Tane şarkı yüklendi");
    }

    /**
     * Oyuncuya müzik çalar - fade in ile smooth başlangıç
     * @param player Hedef oyuncu
     * @param songName Şarkı adı (.nbs uzantısı olmadan)
     * @return Başarılı ise true
     */
    public boolean playMusicToPlayer(Player player, String songName) {
        if (!isNoteBlockAPIAvailable) {
            return false;
        }

        String currentSong = playerCurrentSongs.get(player);

        if (songName.equalsIgnoreCase(currentSong)) {
            return true;
        }

        if (currentSong != null && isMusicPlaying(player)) {
            stopMusicForPlayer(player);
        }

        return startNewSong(player, songName);
    }

    /**
     * Yeni bir şarkı başlatır (fade in ile)
     */
    private boolean startNewSong(Player player, String songName) {
        try {
            String songKey = songName.toLowerCase();
            Song song = loadedSongs.get(songKey);

            if (song == null) {
                if (!loadSong(songName)) {
                    return false;
                }
                song = loadedSongs.get(songKey);
            }

            if (song == null) {
                plugin.getLogger().log(Level.WARNING, "Şarkı bulunamadı: " + songName);
                return false;
            }

            RadioSongPlayer radioPlayer = new RadioSongPlayer(song);

            StereoMode stereoMode = new StereoMode();
            stereoMode.setFallbackChannelMode(new MonoStereoMode());
            radioPlayer.setChannelMode(stereoMode);

            radioPlayer.getFadeIn().setType(FadeType.LINEAR);
            radioPlayer.getFadeIn().setFadeDuration(FADE_IN_DURATION);

            radioPlayer.getFadeOut().setType(FadeType.LINEAR);
            radioPlayer.getFadeOut().setFadeDuration(FADE_OUT_DURATION);

            radioPlayer.setRepeatMode(RepeatMode.ONE);

            radioPlayer.addPlayer(player);

            radioPlayer.setPlaying(true);

            activePlayers.put(player, radioPlayer);
            playerCurrentSongs.put(player, songName);

            plugin.getLogger().log(Level.WARNING, "Müzik Fade in ile başlatıldı: " + player.getName() + " Şarkı: " + songName);
            return true;

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Müzik çalarken hata oluştu: " + songName, e);
            return false;
        }
    }

    /**
     * Belirtilen oyuncu için müziği fade out ile durdurur
     * @param player Hedef oyuncu
     */
    public void stopMusicForPlayer(Player player) {
        if (!isNoteBlockAPIAvailable) {
            return;
        }

        RadioSongPlayer activePlayer = activePlayers.get(player);
        if (activePlayer != null) {
            try {
                activePlayer.setPlaying(false);

                plugin.getLogger().log(Level.WARNING, "Müzik Fade out ile durduruldu: " + player.getName());
                // API fade out'u tamamladıktan sonra player kaldırılır ve destroy edilir
                activePlayer.removePlayer(player);
                activePlayer.destroy();

                activePlayers.remove(player);
                playerCurrentSongs.remove(player);

            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Müzik durdurulurken hata oluştu: " + player.getName(), e);
            }
        }
    }

    /**
     * Müziği aniden durdurur (fade olmadan) - acil durumlar için
     * @param player Hedef oyuncu
     */
    public void forceStopMusicForPlayer(Player player) {
        if (!isNoteBlockAPIAvailable) {
            return;
        }

        RadioSongPlayer activePlayer = activePlayers.get(player);
        if (activePlayer != null) {
            try {
                activePlayer.getFadeOut().setType(FadeType.NONE);

                activePlayer.setPlaying(false);
                activePlayer.removePlayer(player);
                activePlayer.destroy();

                activePlayers.remove(player);
                playerCurrentSongs.remove(player);
                plugin.getLogger().log(Level.WARNING, "Müzik anında durduruldu: " + player.getName());
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Müzik zorla durdurulurken hata oluştu: " + player.getName(), e);
            }
        }
    }

    /**
     * Oyuncunun müzik çalıp çalmadığını kontrol eder
     * @param player Hedef oyuncu
     * @return Müzik çalıyorsa true
     */
    public boolean isMusicPlaying(Player player) {
        if (!isNoteBlockAPIAvailable) {
            return false;
        }

        RadioSongPlayer activePlayer = activePlayers.get(player);
        return activePlayer != null && activePlayer.isPlaying();
    }

    /**
     * Oyuncunun mevcut çalan şarkısını döndürür
     * @param player Hedef oyuncu
     * @return Şarkı adı veya null
     */
    public String getCurrentSong(Player player) {
        return playerCurrentSongs.get(player);
    }

    /**
     * NoteBlockAPI'nin kullanılabilir olup olmadığını döndürür
     */
    public boolean isAvailable() {
        return isNoteBlockAPIAvailable;
    }

    /**
     * Yüklü şarkı sayısını döndürür
     */
    public int getLoadedSongsCount() {
        return loadedSongs.size();
    }


    /**
     * Tüm aktif müzikleri durdurur ve temizler (fade out ile)
     */
    public void cleanup() {
        if (!isNoteBlockAPIAvailable) {
            return;
        }

        try {
            for (Player player : new HashMap<>(activePlayers).keySet()) {
                stopMusicForPlayer(player);
            }

            activePlayers.clear();
            playerCurrentSongs.clear();
            loadedSongs.clear();

            plugin.getLogger().log(Level.WARNING, "Müzik sistemi temizlendi");
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Müzik sistemi temizlenirken hata oluştu", e);
        }
    }
}