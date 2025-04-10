# Parade Card Game
A digital adaptation of the strategic card game **Parade**, where the goal is to achieve the lowest score! This version supports up to 6 players, allowing you to compete against others or bots through a terminal-based interface.

## About The Project
This project is coded in **Java** and can be played using a terminal or any IDE of your choice. Players must strategically play their cards while avoiding collecting cards from the parade. The game is designed to simulate the original tabletop experience with automated scoring and rule enforcement. 

## Original Parade Board Game
- [Parade Board Game Rules](https://cdn.1j1ju.com/medias/8f/7e/8f-parade-rulebook.pdf)
- [Parade Board Game Video](https://www.youtube.com/watch?v=ETdenvOhrBk)

## Rules Of The Game
### Setup
- The deck is automatically shuffled
- Each player is dealt **5 cards**, which are placed in their **closed deck** (hidden from other players)
- **6 cards** are placed into the parade (visible to all players)

### Gameplay
Each round, players take turns playing a card from their hand into the parade. Depending on the card played, some cards may need to be removed from the parade based on these rules:

### Card Removal Rules
1. Remove all cards with the **same colour** as the played card
2. Remove all cards with a value **less than or equal to** the value of the played card 

Removed cards are added to the player's **open deck** (visible to other players) and grouped by color. The remaining cards in the parade move forward.

### Endgame Conditions
The program automatically checks for endgame triggers after each round: 

Condition 1: A player has collected cards of **all 6 colours**
- Once a player collects their 6th color, every player gets one final turn (without drawing new cards). The game ends.

Condition 2: If all cards from the deck are drawn
- When the draw pile is empty, every player gets one final turn. The game ends when each player has **only 4 cards left** in their hand.

### Scoring
The score for each player is then calculated. Based on the number of players, the scores will be calculated automatically by the program.

Two Players
1. Determine who has the the majority in each colour.
2. If one player has at least 2 more cards of a color than another player, their cards for that color are "flipped."
    - Flipped cards adds **1 point** to the total score
    - Non-flipped cards adds their **face value** to the total score
3. If both players have an equal number of cards for a colour, no flipping occurs 

Three or More Players
1. Determine who has the the majority in each colour.
2. The player with the most cards of a color has their cards "flipped."
    - If multiple players tie for majority, all tied players' cards are flipped.
3. The scoring is similar to a two player game
    - Flipped cards adds **1 point** to the total score
    - Non-flipped cards adds their **face value** to the total score

The winner is the player with the lowest score!

## Getting Started
### Prerequisites
<!-- Software required for the program to run -->
To run this project, ensure you have:
    - Java Development Kit (JDK) installed
        - e.g. Oracle, Eclipse, OpenJDK, IntelliJ, jGRASP, etc.
    - Terminal or IDE capable of running Java programs
        - e.g. cmd, shell

### Installion
<!-- Instructions on setting up the project locally -->
1. Clone this repository
```bash
git clone <repository-url>
```

2. Navigate to the project directory
```bash
cd <directory>
```

3. Compile the program
```bash
# windows 
./compile

# mac/linux
bash compile.sh
```

### Executing Program
<!-- How to run the program -->
To run the program
```bash
# windows 
./run

# mac/linux
bash run.sh
```

## Authors 
<!-- Contributors name and info-->

## Acknowledgement
<!-- 
*** Any code snippets or examples
*** format: [text](link)
-->