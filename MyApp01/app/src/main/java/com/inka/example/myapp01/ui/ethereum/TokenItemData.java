package com.inka.example.myapp01.ui.ethereum;

import java.math.BigInteger;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TokenItemData extends RealmObject implements Comparable<TokenItemData> {
    @PrimaryKey
    public String contract;         // 코인 컨트렉트 주소

    public String tokenName;        // 토큰명
    public String symbol;           // 코인 심볼명
    public String quantity;     // 개수 ( * 1000000000000000000 ) 0이 18개
    public long blockNumber;        // 블럭 넘버

    public TokenItemData(){}
    public TokenItemData(String tokenName) {
        this.tokenName = tokenName;
    }

    public TokenItemData(String tokenName, String symbol, String contract) {
        this.tokenName = tokenName;
        this.symbol = symbol;
        this.contract = contract;
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    @Override
    public int compareTo(TokenItemData o) {
        return tokenName.compareTo( o.tokenName);
    }

    public void copyObject(TokenItemData itemData) {
        this.contract       = itemData.contract;
        this.quantity       = itemData.quantity;
        this.tokenName      = itemData.tokenName;
        this.symbol         = itemData.symbol;
        this.blockNumber    = itemData.blockNumber;
    }
}
