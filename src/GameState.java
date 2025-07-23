public enum GameState {
        OPEN(null),
        DRAW(0),
        WIN1(1),
        WIN2(-1);


        private final Integer intId; 

        private GameState(Integer intId) {
            this.intId = intId;
        }

        public Integer toInt() {
            return this.intId;
        }

        public static GameState fromInt(Integer intId) {
            for(GameState e : GameState.values()) {
                if(e.intId == intId)
                    return e;
            }
            return GameState.OPEN;
        }

        /*
        public static void main(String args[]) {
            System.out.println(GameState.OPEN + " " + GameState.OPEN.ordinal() + " " + GameState.OPEN.toInt() + " " + GameState.fromInt(null));
            System.out.println(GameState.DRAW + " " + GameState.DRAW.ordinal() + " " + GameState.DRAW.toInt() + " " + GameState.fromInt(0));
            System.out.println(GameState.WIN1 + " " + GameState.WIN1.ordinal() + " " + GameState.WIN1.toInt() + " " + GameState.fromInt(1));
            System.out.println(GameState.WIN2 + " " + GameState.WIN2.ordinal() + " " + GameState.WIN2.toInt() + " " + GameState.fromInt(-1));
            
        }
        */
}
