package com.dmac.game_sales.constant;

public enum GameType {
    Online(1),
    Offline(2);

    public final int value;

    private GameType(int value){
        this.value = value;
    }
}
