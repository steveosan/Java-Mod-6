package edu.wctc;

import java.util.*;
import java.util.stream.Collectors;

public class DiceGame {
    private final List<Player> players = new ArrayList<>();
    private final List<Die> dice = new ArrayList<>();
    private final int maxRolls;
    private Player currentPlayer;

    public DiceGame(int countPlayers, int countDice, int maxRolls) {
        this.maxRolls = maxRolls;
        if(countPlayers < 2){
            throw new IllegalArgumentException();
        } else {
            for(int i = 0; i < countPlayers; i++){
                players.add(new Player());
            }
            for(int i = 0; i < countDice; i++){
                dice.add(new Die(6));
            }
        }
    }

    private boolean allDiceHeld(){
        return dice.stream().allMatch(Die::isBeingHeld);
    }
    public boolean autoHold(int faceValue){
        boolean output = false;

        for(Die die : dice){
            if(die.getFaceValue() == faceValue){
                if(!isHoldingDie(die.getFaceValue())) {
                    die.holdDie();
                    output = true;
                    break;
                }
                output = true;
                break;
            }else output = false;
        }

        return output;
    }

    public boolean currentPlayerCanRoll(){
        return currentPlayer.getRollsUsed() < maxRolls;
    }

    public int getCurrentPlayerNumber(){
        return currentPlayer.getPlayerNumber();
    }

    public int getCurrentPlayerScore(){
        return currentPlayer.getScore();
    }
    public String getDiceResults(){
        return dice.stream().map(Die::toString).collect(Collectors.joining("\n"));
    }

    public String getFinalWinner(){
        List<Integer> wins = players.stream().map(Player::getWins).sorted().collect(Collectors.toList());

        for (Player player : players){
            if(player.getWins() == wins.get(wins.size()-1)){
                return player.toString();
            }
        }
        return null;
    }
    public String getGameResults(){
        List<Integer> sortedScores = players.stream().map(Player::getScore).sorted(Comparator.reverseOrder()).collect(Collectors.toList());

        List<Player> losingPlayers = new ArrayList<>();
        List<Player> winningPlayers = new ArrayList<>();

        int points = players.stream().mapToInt(Player::getScore).sum();
        for(Player player : players){
            if (points > 0) {
                if (player.getScore() == sortedScores.get(0)) {
                    winningPlayers.add(player);
                } else {
                    losingPlayers.add(player);
                }
            }else{
                losingPlayers.add(player);
            }
        }

        winningPlayers.forEach(Player::addWin);

        losingPlayers.forEach(Player::addLoss);

        return players.stream().map(Player::toString).collect(Collectors.joining("\n"));
    }
    private boolean isHoldingDie(int faceValue){

        List<Die> heldDie = dice.stream().filter(Die::isBeingHeld).collect(Collectors.toList());
        boolean output = false;
        for(Die die : heldDie){
            output = die.getFaceValue() == faceValue;
            if(output) return true; break;
        }
        return false;
    }

    public boolean nextPlayer(){
        if(getCurrentPlayerNumber() < players.size()) {
            currentPlayer = players.get(getCurrentPlayerNumber());
            return true;
        } else return false;
    }

    public void playerHold(char dieNum){
        for(Die die : dice){
            if(die.getDieNum() == dieNum){
                die.holdDie();
            }
        }
    }

    public void resetDice(){
        dice.forEach(Die::resetDie);
    }

    public void resetPlayers(){
        players.forEach(Player::resetPlayer);
    }

    public void rollDice(){
        currentPlayer.roll();
        dice.forEach(Die::rollDie);
    }

    public void scoreCurrentPlayer(){
        List<Die> heldDie = dice.stream().filter(Die::isBeingHeld).collect(Collectors.toList());
        boolean isShip = false;
        boolean isCaptain = false;
        boolean isCrew = false;
        for(Die die : heldDie){
            isShip = die.getFaceValue() == 6;
            if(isShip){break;}
        }
        for(Die die : heldDie){isCaptain = die.getFaceValue() == 5;
            if(isCaptain){break;}
        }
        for(Die die : heldDie) {
            isCrew = die.getFaceValue() == 4;
            if (isCrew) {
                break;
            }
        }

        if (isShip && isCaptain && isCrew) {
            int score = heldDie.stream().mapToInt(Die::getFaceValue).sum();
            score -= (6 + 5 + 4);
            currentPlayer.setScore(currentPlayer.getScore() + score);
        }
    }

    public void startNewGame(){
        currentPlayer = players.get(0);
        resetPlayers();
    }
}
