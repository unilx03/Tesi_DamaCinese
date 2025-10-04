public enum Configuration{
        BASE(0),
        TABLES(1),
        SPECIAL_RULES(2),
        MOVE_ORDERING(3); //fullest implementation

        private final Integer intId; 

        private Configuration(Integer intId) {
            this.intId = intId;
        }

        public Integer toInt() {
            return this.intId;
        }

        public static Configuration fromInt(Integer intId) {
            for(Configuration e : Configuration.values()) {
                if(e.intId == intId)
                    return e;
            }
            return Configuration.BASE;
        }
}
