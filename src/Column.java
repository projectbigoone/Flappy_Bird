public class Column {
    
    int columnPosX;
    int upperPipePosY;
    int lowerPipePosY;
    int upperPipeHeight;
    int lowerPipeHeight;

    // Each column contains an upper and lower pipe with their corresponding x & y position
    Column(int columnPosX, int upperPipePosY, int lowerPipePosY, int upperPipeHeight, int lowerPipeHeight) {
        this.columnPosX = columnPosX;
        this.upperPipePosY = upperPipePosY;
        this.lowerPipePosY = lowerPipePosY;
        this.upperPipeHeight = upperPipeHeight;
        this.lowerPipeHeight = lowerPipeHeight;
    }

}
