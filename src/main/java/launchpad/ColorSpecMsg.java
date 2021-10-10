package launchpad;

public class ColorSpecMsg {

    private ColorSpecMsg parent;

    private final byte[] message;

    public ColorSpecMsg(byte[] message) {
        this.message = message;
    }

    public ColorSpecMsg(byte[] message, ColorSpecMsg parent) {
        this.message = message;
        this.parent = parent;
    }

    public ColorSpecMsg linkMessage(LaunchpadColorType type, byte position, byte color) {
        return this.linkMessage(type, position, color, color);
    }

    public ColorSpecMsg linkMessage(LaunchpadColorType type, byte position, byte colorA, byte colorB) {
        ColorSpecMsg newMsg = null;
        switch (type) {
            case Pulsing:
                newMsg = new ColorSpecMsg(new byte[]{0x02, position, colorA});
                break;
            case Flashing:
                newMsg = new ColorSpecMsg(new byte[]{0x01, position, colorA, colorB});
                break;
            default:
                // Static and other types
                newMsg = new ColorSpecMsg(new byte[]{0x00, position, colorA});
                break;
        }

        if (this.message != null) {
            newMsg.parent = this;
        }

        return newMsg;
    }

    public byte[] getMessage() {
        return message;
    }

    public ColorSpecMsg getParent() {
        return parent;
    }
}
