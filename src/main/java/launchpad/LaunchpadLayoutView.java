//        return String.format("%02X ", GetPositionByte(x, y));

package launchpad;


// TODO: Layout grid
//      - Layout chance tracking
public class LaunchpadLayoutView {

    private static final int PadWidth = 8;

    private static final int PadHeight = 8;

    private final byte activationButton;

    private final Tile[][] gird = new Tile[PadWidth][PadHeight];

    public LaunchpadLayoutView(int color, byte activationButton) {
        this.activationButton = activationButton;
        for (int i = 0; i < PadWidth; i++) {
            for (int j = 0; j < PadHeight; j++) {
                this.gird[i][j] = new Tile(i + 1, j + 1, (byte) color, LaunchpadColorType.Static);
            }
        }
    }

    public void onShow() {
    }

    public void onHide() {
    }

    public void setTileColor(int x, int y, LaunchpadColorType color) {
        if (!this.isInRange(x, y)) {
            return;
        }

        gird[x - 1][y - 1].setColorType(color);
    }

    public void onButtonPressed(int x, int y) {
        this.setTileColor(x, y, LaunchpadColorType.Pulsing);
    }

    public void drawGrid(LaunchpadInstance instance, boolean doFullRedraw) {
        ColorSpecMsg msg = null;
        for (Tile[] tiles : this.gird) {
            for (Tile tile : tiles) {
                byte pos = instance.positionToByte(tile.getX(), tile.getY());
                if (doFullRedraw) {
                    if (msg == null) {
                        msg = new ColorSpecMsg(null);
                    }

                    msg = msg.linkMessage(tile.getColorType(), pos, tile.getColor());
                    continue;
                }

                if (!tile.hasChanged())
                {
                    continue;
                }

                if (msg == null) {
                    msg = new ColorSpecMsg(null);
                }

                msg = msg.linkMessage(tile.getColorType(), pos, tile.getColor());
                // Old way...
                 //instance.setColor(tile.getX(), tile.getY(), tile.getColor(), tile.getColorType());
            }
        }

        if (msg != null) {
            instance.fireColorSpecMsg(msg);
        }
    }

    public byte getActivationButton() {
        return activationButton;
    }

    private boolean isInRange(int x, int y) {
        return x <= PadWidth && x > 0 && y <= PadHeight && y > 0;
    }
}

class Tile {
    private int x;

    private int y;

    private LaunchpadColorType colorType;

    private byte color;

    private boolean hasChanged;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
        this.hasChanged = true;
    }

    public Tile(int x, int y, byte color, LaunchpadColorType colorType) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.hasChanged = true;
        this.colorType = colorType;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        this.hasChanged = true;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        this.hasChanged = true;
    }

    public LaunchpadColorType getColorType() {
        return colorType;
    }

    public void setColorType(LaunchpadColorType colorType) {
        this.colorType = colorType;
        this.hasChanged = true;
    }

    public boolean hasChanged() {
        return hasChanged;
    }

    public void setHasChanged(boolean hasChanged) {
        this.hasChanged = hasChanged;
    }

    public byte getColor() {
        return color;
    }

    public void setColor(byte color) {
        this.color = color;
    }

    public boolean isHasChanged() {
        return hasChanged;
    }
}