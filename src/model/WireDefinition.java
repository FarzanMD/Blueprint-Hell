package model;

public class WireDefinition {
    public final int fromSystem, fromPortIndex;
    public final int toSystem, toPortIndex;

    public WireDefinition(int fromSystem, int fromPortIndex,
                          int toSystem, int toPortIndex) {
        this.fromSystem = fromSystem;
        this.fromPortIndex = fromPortIndex;
        this.toSystem = toSystem;
        this.toPortIndex = toPortIndex;
    }
}
