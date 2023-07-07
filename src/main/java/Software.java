public enum Software {
    INTELLIJ_IDEA("path.intellij"),
    CMD_PROMPT_PROXY("path.proxy"),
    CMD_PROMPT_LOBBY("path.lobby"),
    CMD_PROMPT_INFERRIS("path.inferris"),
    REDIS(null);

    private final String path;
    Software(String path){
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
