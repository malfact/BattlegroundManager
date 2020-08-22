package net.malfact.bgmanager;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class ProgressBar {

    private final BossBar bar;

    private  String title;
    private TitleFlag titleFlag;

    private int maxValue = 0;
    private int currentValue = 0;

    public ProgressBar(String title, BarColor color, BarStyle style){
        this(title, color, style, TitleFlag.NONE);
    }

    public ProgressBar(String title, BarColor color, BarStyle style, TitleFlag titleFlag){
        this.bar = Bukkit.createBossBar(title, color, style);

        this.title = title;
        this.titleFlag = titleFlag;
    }

    public ProgressBar addPlayer(Player player){
        bar.addPlayer(player);
        return this;
    }

    public ProgressBar removePlayer(Player player){
        bar.removePlayer(player);
        return this;
    }

    public ProgressBar removeAllPlayers(){
        bar.removeAll();
        return this;
    }

    public ProgressBar setBarColor(BarColor color){
        bar.setColor(color);
        return this;
    }

    public ProgressBar setBarStyle(BarStyle style){
        bar.setStyle(style);
        return this;
    }

    public ProgressBar setTitle(String title) {
        this.title = title;
        updateTitle();
        return this;
    }

    public ProgressBar setTitleFlag(TitleFlag titleFlag) {
        this.titleFlag = titleFlag;
        updateTitle();
        return this;
    }

    public ProgressBar setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        updateProgress();
        return this;
    }

    public ProgressBar setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
        updateProgress();
        return this;
    }

    public double getProgress(){
        return bar.getProgress();
    }

    private void updateProgress(){
        double progress = (maxValue == 0) ? 0 : ((double) currentValue / (double) maxValue);
        progress = Math.min(progress, 1.0);
        bar.setProgress(progress);

        if (titleFlag != TitleFlag.NONE){
            updateTitle();
        }
    }

    private void updateTitle(){
        final String outputTime = String.format("%02d m %02d s", currentValue / 60, currentValue % 60);


        if (titleFlag == TitleFlag.NONE)
            bar.setTitle(title);
        else if (titleFlag == TitleFlag.TIME)
            bar.setTitle(title + " " + outputTime);
        else if (titleFlag == TitleFlag.PERCENTAGE)
            bar.setTitle(title + " " + bar.getProgress()*100 + "%");
        else
            bar.setTitle(title + " " + outputTime + bar.getProgress()*100 + "%");
    }

    public enum TitleFlag{
        NONE,
        TIME,
        PERCENTAGE,
        BOTH
    }
}
