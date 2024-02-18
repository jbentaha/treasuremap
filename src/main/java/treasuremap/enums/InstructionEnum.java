package treasuremap.enums;

public enum InstructionEnum {

    A("ADVANCE"), D("RIGHT"), G("LEFT");

    private String value;

    InstructionEnum(String value) {
        this.value = value;
    }
}
