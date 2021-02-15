package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

/**
 * Project name: Pong/sample
 * Filename:
 * Created:  15/02/2021 / 11:09
 * Description:
 * Revision:
 *
 * @Author: Ismael - fmartin@nigul.cide.es
 * @Version:
 */
public class Pong2 extends Application {
    //Attriubutes
    private static final int ANCHURA=800;
    private static final int ALTURA=600;
    private static final int JUGADOR_ANCHURA=15;
    private static final int JUGADOR_ALTURA=100;
    private static final double R_BOLA=15;
    private int bolaXVel=1;
    private int bolaYVel=1;
    private double j1YPos=ALTURA/2;
    private double j2YPos=ALTURA/2;
    private double bolaXPos=ANCHURA/2;
    private double bolaYPos=ANCHURA/2;
    private int contadorJ1=0;
    private int contadorJ2=0;
    private boolean start;
    private int j1XPos=0;
    private double j2XPos=ANCHURA-JUGADOR_ANCHURA;
    //Other Methods

    public void start(Stage stage) throws Exception{
        stage.setTitle("PONG");
        Canvas panel=new Canvas(ANCHURA,ALTURA);
        GraphicsContext gc=panel.getGraphicsContext2D();
        Timeline tl=new Timeline(new KeyFrame(Duration.millis(10),e->game(gc)));
        tl.setCycleCount(Timeline.INDEFINITE);

        //Start con Space
        panel.setOnMouseMoved(e->j1YPos=e.getY());
        panel.setOnMouseClicked(e->start=true);
        stage.setScene(new Scene(new StackPane(panel)));
        stage.show();
        tl.play();
    }
    private void game(GraphicsContext gc){
        //Color de fondo
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,ANCHURA,ALTURA);

        //Color de las letras
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(25));

        if (start){
            //Movimiento de la bola
            bolaXPos+=bolaXVel;
            bolaYPos+=bolaYVel;

            //IA
            if (bolaXPos<ANCHURA-ALTURA/4){
                j2YPos=bolaYPos-JUGADOR_ALTURA/2;
            }else {
                j2YPos=bolaYPos> j2YPos+JUGADOR_ALTURA/2 ?j2YPos+=1: j2YPos-1;
            }

            //Dibujo la bola
            gc.fillOval(bolaXPos,bolaYPos, R_BOLA,R_BOLA);
        }else {
            //Texto inicial
            gc.setStroke(Color.WHITE);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.strokeText("Space to Start",ANCHURA/2,ALTURA/2);

            //Reset de la bola
            bolaXPos=ANCHURA/2;
            bolaYPos=ALTURA/2;

            //Reset de la velocidad y dirección
            bolaXVel=new Random().nextInt(2)==0 ? 1: -1;
            bolaYVel=new Random().nextInt(2)==0 ? 1: -1;
        }
        if (bolaYPos>ALTURA||bolaYPos<0) bolaYVel*=-1;
        //Punto de la IA
        if (bolaXPos<j1XPos-JUGADOR_ANCHURA){
            contadorJ2++;
            start=false;
        }
        //Puntos jugador
        if (bolaXPos>j2XPos+JUGADOR_ANCHURA){
            contadorJ1++;
            start=false;
        }

        //Incremento de la velocidad
        if( ((bolaXPos + R_BOLA > j2XPos) && bolaYPos >= j2YPos && bolaYPos <= j2YPos + JUGADOR_ALTURA) ||
                ((bolaXPos < j1XPos + JUGADOR_ANCHURA) && bolaYPos >= j1YPos && bolaYPos<= j1YPos + JUGADOR_ALTURA)){
            bolaYVel+=1*Math.signum(bolaYVel);
            bolaXVel+=1*Math.signum(bolaXVel);
            bolaYVel*=-1;
            bolaXVel*=-1;
        }
        //Dibujo puntos
        gc.fillText(contadorJ1+"\t\t\t\t\t\t\t\t"+contadorJ2,ANCHURA/2,ALTURA/2);
        //Dibujamos a los jugadores
        gc.fillRect(j2XPos,j2YPos,JUGADOR_ANCHURA,JUGADOR_ALTURA);
        gc.fillRect(j1XPos,j1YPos,JUGADOR_ANCHURA,JUGADOR_ALTURA);
    }
    public static void main(String[] args) {
        launch(args);
    }
}