package com.hooklite.endshop.data.rewards;

import org.bukkit.entity.Player;

public class EBalanceReward implements EReward {
    private double reward;
    private RewardAction action;

    @Override
    public void executeReward(Player player) {
        // TODO: Set the reward action

    }

    @Override
    public RewardAction getRewardAction() {
        return action;
    }

    @Override
    public void setRewardAction(RewardAction action) {
        this.action = action;
    }

    @Override
    public void setReward(String reward) {
        this.reward = Double.parseDouble(reward);
    }
}
