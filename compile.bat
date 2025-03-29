# Compile dependencies 
javac -d bin -cp "lib/*;src" src/cardgame/network/ParadeServer.java
javac -d bin -cp "lib/*;src" src/cardgame/network/ParadeClient.java
javac -d bin -cp "lib/*;src" src/cardgame/utility/UsernameValidator.java

# Compile GameMenu.java
javac -d bin -cp "lib/*;src" src/cardgame/GameMenu.java
