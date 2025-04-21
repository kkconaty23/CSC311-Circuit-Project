/**
 * Represents a visual and interactive component (sprite) within the circuit sandbox.
 */
public class Sprite extends ImageView{
    private String id; // Unique identifier for sprite instance
    private double dragOffsetX, dragOffsetY;

    public Sprite(Image image, String id){
        super(image);
        this.id = id;
        initDrag();
    }

    private void initDrag(){
        setOnMousePressed(e -> {
            dragOffsetX = e.getSceneX() - getLayoutX();
            dragOffsetY = e.getSceneY() - getLayoutY();
        });

        setOnMouseDragged(e -> {
            setLayoutX(e.getSceneX() - dragOffsetX);
            setLayoutY(e.getSceneY() - dragOffsetY);
        });
    }

    /**
     *
     * @return
     */
    public String getSpriteId(){
        return id;
    }
}