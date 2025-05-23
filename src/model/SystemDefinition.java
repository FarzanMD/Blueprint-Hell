package model;

import java.util.List;

public class SystemDefinition {
    public final int x, y, width, height;
    public final List<Port.Type> inputTypes;
    public final List<Port.Type> outputTypes;

    public SystemDefinition(int x, int y, int width, int height,
                            List<Port.Type> inputTypes,
                            List<Port.Type> outputTypes) {
        this.x = x; this.y = y;
        this.width = width; this.height = height;
        this.inputTypes = inputTypes;
        this.outputTypes = outputTypes;
    }
}
