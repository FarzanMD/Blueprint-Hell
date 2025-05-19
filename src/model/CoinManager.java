package model;

public class CoinManager {
    private int coins = 0;

    public void addCoin() {
        coins++;
    }

    public int getCoins() {
        return coins;
    }

    public void reset() {
        coins = 0;
    }
}
