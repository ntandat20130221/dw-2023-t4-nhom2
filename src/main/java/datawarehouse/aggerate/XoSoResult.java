package datawarehouse.aggerate;

import java.math.BigDecimal;
import java.util.Date;

public class XoSoResult {
    private String provinceName;
    private Date resultDate;
    private String prizeName;
    private int numberOpen;
    private String winningNumbers;
    private BigDecimal prizeAmount;
    private int winnerCount;

    public XoSoResult(String provinceName, Date resultDate, String prizeName, String winningNumbers, BigDecimal prizeAmount, int winnerCount) {
        this.provinceName = provinceName;
        this.resultDate = resultDate;
        this.prizeName = prizeName;
        this.winningNumbers = winningNumbers;
        this.prizeAmount = prizeAmount;
        this.winnerCount = winnerCount;
    }

    public void setNumberOpen(int numberOpen) {
        this.numberOpen = numberOpen;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public Date getResultDate() {
        return resultDate;
    }

    public String getPrizeName() {
        return prizeName;
    }

    public int getNumberOpen() {
        return numberOpen;
    }

    public String getWinningNumbers() {
        return winningNumbers;
    }

    public BigDecimal getPrizeAmount() {
        return prizeAmount;
    }

    public int getWinnerCount() {
        return winnerCount;
    }
}

