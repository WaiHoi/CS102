# Determine the classpath separator based on OS
if [[ "$OSTYPE" == "msys"* || "$OSTYPE" == "cygwin"* ]]; then
    # Windows (Git Bash, Cygwin)
    SEPARATOR=";"
else
    # macOS, Linux, WSL
    SEPARATOR=":"
fi

# Compile all Java files
javac -d bin -cp "lib/*${SEPARATOR}src" src/cardgame/network/ParadeServer.java
javac -d bin -cp "lib/*${SEPARATOR}src" src/cardgame/network/ParadeClient.java
javac -d bin -cp "lib/*${SEPARATOR}src" src/cardgame/utility/UsernameValidator.java
javac -d bin -cp "lib/*${SEPARATOR}src" src/cardgame/GameMenu.java