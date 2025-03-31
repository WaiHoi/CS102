# Determine the classpath separator based on OS
if [[ "$OSTYPE" == "msys"* || "$OSTYPE" == "cygwin"* ]]; then
    # Windows (Git Bash, Cygwin)
    SEPARATOR=";"
else
    # macOS, Linux, WSL
    SEPARATOR=":"
fi

java -cp "bin${SEPARATOR}lib/*" cardgame.GameMenu