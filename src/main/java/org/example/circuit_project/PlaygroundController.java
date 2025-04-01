package org.example.circuit_project;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.Node;

public class PlaygroundController {
    private double startX, startY, endX, endY;

    @FXML
    private Line wire;

    @FXML
    private Pane panePlayground; //place to drag and drop pieces

    /**
     * adds event handlers for when the fxml is loaded
     */
    @FXML
    public void initialize() {
        //event handlers
        wire.setOnMousePressed(event -> {
            startX = wire.getStartX();
            startY = wire.getStartY();
            endX = wire.getEndX();
            endY = wire.getEndY();
        });

        wire.setOnMouseDragged(event -> {
            if (event.isPrimaryButtonDown()) {
                //move wire dynamically
                wire.setEndX(event.getX());
                wire.setEndY(event.getY());
            }
        });
    }

    /**
     * handles what happens when a pane is clicked on from the select bar
     * @param event
     */
    @FXML
    public void onSpriteClicked(MouseEvent event) {
        if (event.getEventType() != MouseEvent.MOUSE_CLICKED) {
            return; //only triggers on click not drag
        }

        Node clickedNode = (Node) event.getSource();
        System.out.println("Clicked on: " + clickedNode.getClass().getName());

        Pane originalPane = null;

        // decide if clicked on a pane or on a child inside a Pane
        if (clickedNode instanceof Pane) {
            originalPane = (Pane) clickedNode;
        } else if (clickedNode.getParent() instanceof Pane) {
            originalPane = (Pane) clickedNode.getParent();
        }

        if (originalPane == null) {
            System.out.println("No Pane found.");
            return; // if no pane was found
        }

        //makes a new pane and copy it
        Pane newSprite = new Pane();
        newSprite.setPrefSize(originalPane.getPrefWidth(), originalPane.getPrefHeight());

        //copy each child
        for (Node child : originalPane.getChildren()) {
            Node clonedChild = cloneNode(child); //clone the child
            if (clonedChild != null) {
                newSprite.getChildren().add(clonedChild);
            }
        }

        // move the new sprite where the user clicked
        newSprite.setLayoutX(event.getSceneX());
        newSprite.setLayoutY(event.getSceneY());

        // enable dragging
        enableDragging(newSprite);

        // add new sprite to the playground
        panePlayground.getChildren().add(newSprite);
        System.out.println("New sprite added!");
    }

    /**
     * clones all pieces inside a pane
     * @param node
     * @return
     */
    private Node cloneNode(Node node) {
        if (node instanceof Rectangle) {
            Rectangle rect = (Rectangle) node;
            Rectangle copy = new Rectangle(rect.getWidth(), rect.getHeight(), rect.getFill());
            copy.setArcWidth(rect.getArcWidth());
            copy.setArcHeight(rect.getArcHeight());
            copy.setStroke(rect.getStroke());
            copy.setStrokeWidth(rect.getStrokeWidth());
            copy.setLayoutX(rect.getLayoutX());
            copy.setLayoutY(rect.getLayoutY());
            return copy;
        } else if (node instanceof Ellipse) {
            Ellipse ellipse = (Ellipse) node;
            Ellipse copy = new Ellipse(ellipse.getCenterX(), ellipse.getCenterY(), ellipse.getRadiusX(), ellipse.getRadiusY());
            copy.setFill(ellipse.getFill());
            copy.setStroke(ellipse.getStroke());
            copy.setStrokeWidth(ellipse.getStrokeWidth());
            copy.setLayoutX(ellipse.getLayoutX());
            copy.setLayoutY(ellipse.getLayoutY());
            return copy;
        } else if (node instanceof Line) {
            Line line = (Line) node;
            Line copy = new Line(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
            copy.setStroke(line.getStroke());
            copy.setStrokeWidth(line.getStrokeWidth());
            copy.setLayoutX(line.getLayoutX());
            copy.setLayoutY(line.getLayoutY());
            return copy;
        }
        return null;
    }

    /**
     * allows moving of the pane selected by a user
     * @param sprite
     */
    private void enableDragging(Pane sprite) {
        final double[] offsetX = {0};
        final double[] offsetY = {0};

        sprite.setOnMousePressed(event -> {
            offsetX[0] = event.getSceneX() - sprite.getLayoutX();
            offsetY[0] = event.getSceneY() - sprite.getLayoutY();
        });

        sprite.setOnMouseDragged(event -> {
            sprite.setLayoutX(event.getSceneX() - offsetX[0]);
            sprite.setLayoutY(event.getSceneY() - offsetY[0]);
        });

        System.out.println("Dragging enabled!");
    }
}


