# Multiplayer 4-Player Card Game

## Overview
This project is a 4-player multiplayer card game implemented using a client-server architecture in Java.
The server manages the game flow, while clients connect to play in real time using socket programming.

## Gameplay

- The server starts first and waits for 4 players to connect.  
- Each client connects using the IP address and player name.  
- Once all players are connected:
  - The server selects a trump suit which is the same during whole the game.  

- The game consists of several rounds.  
- At the start of each round:
  - The server asks each player to commit scores, indicating how many sub-rounds they expect to win out of 13.
  - The server deals 13 cards to each player.  

### Playing Phase
- Each round consists of 13 sub-rounds.  
- For each sub-round:
  - The server randomly selects the turn order among the clients.  
  - The client whose turn it is sees "Your Turn".  
  - The player selects a card by entering a number from 0 to (remaining cards - 1).  
  - Once all 4 players have played a card in that sub-round:
     - The server calculates sub-round scores based on the cards played.  
     - The server determines the winner of the sub-round based on  scores. 

### Round Evaluation
- After all 13 sub-rounds in a round are completed:
  - The server calculates points for each player based on their committed scores and actual wins.  

- The Playing Phase and Round Evaluation repeat for each round in the game until all rounds are completed.


**Note :** For testing purposes, the current implementation uses 1 round with 2 sub-rounds but actual game contains multiple rounds and each round consists of 13 sub-rounds.  