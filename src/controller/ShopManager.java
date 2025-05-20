package controller;

import model.CoinManager;
import view.ShopWindow;

public class ShopManager {
    private final CoinManager coinManager;

    private boolean atarActive = false;
    private boolean airyamanActive = false;

    private long atarEndTime = 0;
    private long airyamanEndTime = 0;

    public ShopManager(CoinManager coinManager) {
        this.coinManager = coinManager;
    }
    public boolean isAtarActive() {
        return atarActive && System.currentTimeMillis() < atarEndTime;
    }

    public boolean isAiryamanActive() {
        return airyamanActive && System.currentTimeMillis() < airyamanEndTime;
    }

    public boolean tryBuyAtar() {
        if (coinManager.getCoins() >= 3) {
            coinManager.spendCoins(3);

            atarActive = true;
            atarEndTime = System.currentTimeMillis() + 10_000; // 10 seconds


            return true;
        }
        return false;

    }

    public boolean tryBuyAiryaman() {
        if (coinManager.getCoins() >= 4) {
            coinManager.spendCoins(4);
            airyamanActive = true;
            airyamanEndTime = System.currentTimeMillis() + 5_000; // 5 seconds
            return true;
        }
        return false;
    }

    public boolean tryBuyAnahita(PacketManager packetManager) {
        if (coinManager.getCoins() >= 5) {
            coinManager.spendCoins(5);
            packetManager.restoreAllPacketHP();
            return true;
        }
        return false;
    }

    public void update() {
        // Clear expired effects
        if (atarActive && System.currentTimeMillis() > atarEndTime) {
            atarActive = false;
        }
        if (airyamanActive && System.currentTimeMillis() > airyamanEndTime) {
            airyamanActive = false;
        }
    }
}
