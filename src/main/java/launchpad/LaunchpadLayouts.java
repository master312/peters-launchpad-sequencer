package launchpad;


public enum LaunchpadLayouts {
    DawSession((byte) 0x00, (byte) 95),
    DrumRack((byte) 0x04, (byte) 96),
    Keys((byte) 0x05, (byte) 97),
    UserMode((byte) 0x06, (byte) 98),    // LightingModeInDramRack
    DawFaders((byte) 0x0D, (byte) 0x00),
    ProgrammersMode((byte) 0x7F, (byte) 0x00);

    private final byte opCode;

    // Button index that activates this layout.
    private final byte activeButton;

    LaunchpadLayouts(byte opCode, byte activeButton) {
        this.opCode = opCode;
        this.activeButton = activeButton;
    }

    public byte getOpCode() {
        return this.opCode;
    }

    public byte getActiveButton() {
        return activeButton;
    }
}