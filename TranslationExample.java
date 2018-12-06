 
import java.util.Random;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
 
public class TranslationExample extends Application {
 
    @Override
    public void start(Stage stage) {
 
        final int x = 10;
        final int y = 20;
        final int width = 100;
        final int height = 130;
 
        // Rectangle1
        ConvexPolygon x1 = new ConvexPolygon();
        x1.addPoint(0,0);
        x1.addPoint(10,60);
        x1.addPoint(80,40);
        x1.setFill(Color.BLUEVIOLET);
        x1.setOpacity(1.1);
        // Rectangle2 (Same position and size to rectangle1)
        ConvexPolygon x2 =new ConvexPolygon();
        x2.addPoint(0,0);
        x2.addPoint(10,60);
        x2.addPoint(80,40);
        x2.setFill(Color.GREEN);
        x2.setOpacity(1.1);
       
        Random random= new Random();
        
        // Set arguments for translation
        //Rotate rotate= new Rotate( 10, x2.getPoints().get(0), x2.getPoints().get(1));
        
 
        // Adding transformation to rectangle2
        System.out.println(x2.getPoints());
        x2.setRotate(240);
        //x2.getTransforms().addAll(rotate);
        System.out.println(x2.getPoints());
        Group root = new Group(x1, x2);
 
        Scene scene = new Scene(root, 450, 250);
 
        stage.setTitle("JavaFX Translation Transformation (o7planning.org)");
        stage.setScene(scene);
        stage.show();
    }
 
    public static void main(String args[]) {
        launch(args);
    }
}
