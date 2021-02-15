package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Pong_Ismael_MartiGonzalez extends Application {
    /**Clase que se encarga de almacenar valores en los ejes X e Y.*/
    public class Posicion {
        int posX;
        int posY;
        public Posicion(int x,int y) {
            this.posX=x;
            this.posY=y;
        }
    }
    /**Clase que se encarga de generar y posicionar las barras de los jugadores.*/
    public class Barra {
        //Attriubutes
        private Posicion posicion;
        private int vel;
        private Pane panel;
        private Node barra;
        private int altura;

        //Builder
        public Barra(int anchura, int altura, int vel, int pos, Pane panel) {
            this.posicion = new Posicion(anchura, altura);
            this.vel = vel;
            this.panel = panel;
            this.altura = altura;
            this.barra = new Rectangle(anchura, altura, Color.WHITE);
            try {
                colocar(pos, anchura, altura);
            } catch (Exception e) {
                System.out.println(e);
            }
            barra.setLayoutX(posicion.posX);
            barra.setLayoutY(posicion.posY);
        }

        //Getters/Setters
        public int getAltura() {
            return altura;
        }

        public void setAltura(int altura) {
            this.altura = altura;
        }

        public int getVel() {
            return vel;
        }

        public void setVel(int vel) {
            this.vel = vel;
        }

        public Pane getPanel() {
            return panel;
        }

        public void setPanel(Pane panel) {
            this.panel = panel;
        }

        public Node getBarra() {
            return barra;
        }

        public void setBarra(Node barra) {
            this.barra = barra;
        }

        public Posicion getPosicion() {
            return posicion;
        }

        public void setPosicion(Posicion posicion) {
            this.posicion = posicion;
        }

        //Other Methods
        /**Reposicionar coloca las barras en el eje Y*/
        private void reposiciona() {
            this.barra.setLayoutY(posicion.posY);
        }
        /**Arriba se encarga del movimiento hacia arriba de la paleta del jugador*/
        public void arriba() {
            posicion.posY = posicion.posY - this.vel;
            this.reposiciona();
        }
        /**Abajo se encarga del movimiento hacia abajo de la paleta del jugador*/
        public void abajo() {
            posicion.posY = posicion.posY + this.vel;
            this.reposiciona();
        }
        /**Colocar se encarga de posicionar las paletas de los jugadores según un valor numerico entre -1 y 1:
         * -1 Situa la barra en el centro de la pantalla
         * 0 Situa la barra en el lado izquierdo de la pantalla
         * 1 Situa la barra en el lado derecho de la pantalla*/
        private void colocar(int numero, int anchura, int altura) throws Exception {
            final Bounds limites = panel.getBoundsInLocal();
            if (numero == 0) {
                posicion.posX = (int) limites.getMinX() + anchura;
                posicion.posY = (int) limites.getMaxY() / 2 - altura / 2;
            } else if (numero == 1) {
                posicion.posX = (int) limites.getMaxX() - (anchura * 2);
                posicion.posY = (int) limites.getMaxY() / 2 - (altura / 2);
            } else if (numero == -1) {
                posicion.posX = ANCHURA/2;
                posicion.posY = 0;
            } else {
                throw new Exception("Error. 0 es izquierda, 1 es derecha y -1 es central");
            }
        }
    }

    /**Inicializamos los objetos del juego*/
    public static final int ANCHURA=800;
    public static final int ALTURA=600;
    public static Pane panel;
    public static Circle bola;
    public static Barra player1;
    public static Barra player2;
    public static Barra malla;
    public static int contador2=0;
    public static int contador1=0;

    /**inicializamos las variables de texto*/
    public static Text playerContador;
    public static Text player2Contador;
    public static Text start;
    public static Text end;
    public static Font fontContador=new Font("Action ManAction Man",50);

    @Override
    public void start(Stage primaryStage) throws Exception{
        /**Preparamos el entorno de juego con Pane y Scene para poder colocar todos los elemtos del juego*/
        panel=new Pane();
        final Scene escena=new Scene(panel,ANCHURA,ALTURA, Color.BLACK);
        primaryStage.setTitle("Pong");
        primaryStage.setScene(escena);
        primaryStage.show();

        //Creamos a los jugadores y la malla
        /**Para Crear a los jugadores y la barra usamos la clase barra que se encarga de crear y colocar los objetos en el panel*/
        player1= new Barra(10,80,30,1,panel);
        player2=new Barra(10,80,30,0,panel);
        malla=new Barra(10,ALTURA,0,-1,panel);

        /**Creamos la bola y la situamos inialmente en el centro de la pantalla*/
        int radio=15;
        bola = new Circle(radio, Color.WHITE);
        bola.setLayoutX(ANCHURA/2);
        bola.setLayoutY(ALTURA/2);
        /**Llamamos ametodos para crear y colocar los textos correspondientes a los contadores de los jugadores y
         * al texto inicial de Start*/
        textoInicial();
        textoJugador(player1);
        textoJugador(player2);

        /**Añadimos todos los objetos al panel de juego para poder visualizarlos*/
        panel.getChildren().addAll(start);
        panel.getChildren().addAll(bola);
        panel.getChildren().addAll(malla.getBarra());
        panel.getChildren().addAll(player1.getBarra());
        panel.getChildren().addAll(player2.getBarra());
        panel.getChildren().addAll(playerContador);
        panel.getChildren().addAll(player2Contador);

        /**Seleccionamos el panel como foco de la escena*/
        panel.requestFocus();
        /**Al pulsar la tecla Space el juego se inicia*/
        panel.setOnKeyPressed(e->{
            if (e.getCode().equals(KeyCode.SPACE)){
                juego();
                panel.getChildren().removeAll(start);
            }
        });
    }

    /**Metodo que se encarga de ejecutar y gestionar nuestro juego*/
    public void juego(){
        /**Llamamos a las variables arriba y abajo en función de la tecla que se pulse y de esta forma dotamos
         * de movimiento a las paletas de los jugadores*/
        panel.setOnKeyPressed(e->{
            switch (e.getCode()){
                case W: player2.arriba(); break;
                case S: player2.abajo(); break;
                case UP:player1.arriba();break;
                case DOWN:player1.abajo();break;
            }
        });

        /**Icializamos un TimeLine Que ejecutara las instrucciónes del juego hasta que algun jugador llegue a 15*/
        final Timeline loop = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {

            /**Definimos la formula de rebote de la pelota que sera de forma fija 30 grados, tambien le añadimos una velocidad fija*/
            double angulo_en_radians =Math.toRadians(30);
            int velocidad =3;
            double deltaX = velocidad *Math.cos(angulo_en_radians);
            double deltaY = velocidad *Math.sin(angulo_en_radians);
            /**Definimos los limites de la ventana para posteriormente definir el comportamiento de la bola*/
            final Bounds limits = panel.getBoundsInLocal();
            @Override
            public void handle(final ActionEvent t) {
                /**Al llegar a 15 uno de los dos jugadores el evento se rompe mostrando un mensaje de Game Over*/
                if (contador1==15||contador2==15){
                    try {
                        if (contador1==15){
                            end=new Text("Game Over!");
                            end.setFont(new Font("Action ManAction Man",30));
                            end.setFill(Color.WHITE);
                            end.setLayoutX(ANCHURA/5);
                            end.setLayoutY(ALTURA/4);
                            panel.getChildren().addAll(end);
                        }else if (contador2==15){
                            end=new Text("Game Over!");
                            end.setFont(new Font("Action ManAction Man",30));
                            end.setFill(Color.WHITE);
                            end.setLayoutX(ANCHURA-250);
                            end.setLayoutY(ALTURA/4);
                            panel.getChildren().addAll(end);
                        }
                    }catch (Exception e){
                       return;
                    }
                }else {

                    /**Definimos el movimiento de la bola en cada ciclo*/
                    bola.setLayoutX(bola.getLayoutX() + deltaX);
                    bola.setLayoutY(bola.getLayoutY() + deltaY);

                    /**Definimos el comportamiento de la bola en funcion los limites de la ventana.
                     * Tambien definimos los mites de la paletas para que no sobrepasen la ventana de juego*/
                    final boolean alLimitDerecho = bola.getLayoutX() >= (limits.getMaxX() - bola.getRadius());
                    final boolean alLimitIzquierdo = bola.getLayoutX() <= (limits.getMinX() + bola.getRadius());
                    final boolean alLimitInferior = bola.getLayoutY() >= (limits.getMaxY() - bola.getRadius());
                    final boolean alLimitSuperior = bola.getLayoutY() <= (limits.getMinY() + bola.getRadius());
                    final boolean playerLimiteIn= player1.getBarra().getLayoutY()> limits.getMaxY()-player1.getAltura();
                    final boolean player2LimiteIn= player2.getBarra().getLayoutY()> limits.getMaxY()-player2.getAltura();
                    final boolean playerLimiteSu=player1.getBarra().getLayoutY()<=limits.getMinY();
                    final boolean player2LimiteSu=player2.getBarra().getLayoutY()<=limits.getMinY();

                    /**Capturamos la diferentes casuisticas de la bola y las paletas*/
                    /**Si la bola toca alguna de las dos paletas, esta cambia su balor y se dirije hacia el lado contrario*/
                    if(bola.getBoundsInParent().intersects(player1.getBarra().getBoundsInParent())||
                            bola.getBoundsInParent().intersects(player2.getBarra().getBoundsInParent())) {
                        deltaX *= -1;
                    }

                    /**Si la bola toca los limites derecho o izquierdo se sumara un punto al jugador correspondiente y
                     * reseteara la posición de la bola en un lugar aleatorio en el centro.
                     * Si toca los limites inferior o superior la bola cambiara su dirección  hacia el lado contrario*/
                    if (alLimitDerecho || alLimitIzquierdo) {
                        if (alLimitDerecho){
                            contador1++;
                            playerContador.setText(contador1+"");
                        }else if (alLimitIzquierdo){
                            contador2++;
                            player2Contador.setText(contador2+"");
                        }else {
                            return;
                        }
                        deltaY*=-1;
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        bola.setLayoutX(ANCHURA/2);
                        bola.setLayoutY(ALTURA/(Math.random()*5)+1);

                    }
                    if (alLimitInferior || alLimitSuperior) {
                        // Multiplicam pel signe de deltaX per mantenir la trajectoria
                        deltaY *= -1;
                    }

                    /**Si alguno de los jugadores llega a los limites de la pantalla de forma automatica se invertira la
                     * posición para no sobre pasar el limite*/
                    if(playerLimiteIn){
                        player1.arriba();
                    }
                    if (player2LimiteIn){
                        player2.arriba();
                    }
                    if (player2LimiteSu){
                        player2.abajo();
                    }
                    if (playerLimiteSu){
                        player1.abajo();
                    }
                }
            }
        }));
        loop.setCycleCount(Timeline.INDEFINITE);
        loop.play();
    }

    /**Definimos los contadores de los jugadores, pasando como parametro al jugador*/
    public void textoJugador(Barra player){
        if (player.equals(player1)){
            //Definimos el contador del jugador 1
            playerContador=new Text("0");
            playerContador.setFont(fontContador);
            playerContador.setFill(Color.WHITE);
            playerContador.setLayoutX(ANCHURA/4);
            playerContador.setLayoutY(ALTURA/7);
        }else if (player.equals(player2)){
            //Definimos el contador del jugador 2
            player2Contador=new Text("0");
            player2Contador.setFont(fontContador);
            player2Contador.setFill(Color.WHITE);
            player2Contador.setLayoutX(ANCHURA-200);
            player2Contador.setLayoutY(ALTURA/7);
        }else {
            return;
        }
    }
    /**Definimos el texto de Start de nuestro juego*/
    public void textoInicial(){
        start =new Text("Pulsa Space");
        start.setFont(fontContador);
        start.setFill(Color.WHITE);
        start.setLayoutX(ANCHURA/2.83);
        start.setLayoutY(ALTURA/4);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
