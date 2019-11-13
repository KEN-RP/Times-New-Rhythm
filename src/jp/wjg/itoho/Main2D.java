package jp.wjg.itoho;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Main2D extends Application {

    Map<String, String> property = Fileimport.PropertyImport("game.properties");

    final int windowWidth = Integer.parseInt(property.get("X"));
    final int windowHeight = Integer.parseInt(property.get("Y"));
    final int judgeLine = Integer.parseInt(property.get("judgeLine"));
    final int noteSize = Integer.parseInt(property.get("noteSize")) / 2;
    final int slideSize = Integer.parseInt(property.get("slideSize"));
    final int comboSize = Integer.parseInt(property.get("comboZoneWidth")) / 2;
    final int effectMax = Integer.parseInt(property.get("maxDrawEffects"));
    final double searchWidth = Double.parseDouble(property.get("searchWidth"));
    final double masterSpeed = Double.parseDouble(property.get("masterSpeed"));
    final boolean drawJudgeLine = Boolean.parseBoolean(property.get("drawJudgeLine"));
    final boolean DSWC = Boolean.parseBoolean(property.get("difficultySelectWithCharacter"));
    final boolean fullScreen = Boolean.parseBoolean(property.get("fullScreen"));
    final boolean hits = Boolean.parseBoolean(property.get("hit"));
    final boolean DM = Boolean.parseBoolean(property.get("developmentMode"));
    int height, width;
    int laneHeight;
    int frame;
    int AFrame;
    int SFrame;
    boolean select = false;
    int currentSong = 0;
    double alpha = 1;
    String mode = property.get("inputMode");
    String clearStatus = "Success";
    String clearRank = "SSS";
    Color rankColor = Color.color(1, 1, 0);
    final int grobalOffset = Integer.parseInt(property.get("grobalOffset"));
    double elapse = 0;
    final String[] difficulties = new String[] {"Easy", "Normal", "Hard", "Lunatic"};
    boolean skipped;
    Image jacket;
    static String composer = "";

    int[] signal = new int[5];
    static int[] status = new int[4];

    final String[] songNames = property.get("songNames").replaceAll(" ", "").split(",");
    final String[] names = new String[songNames.length];
    static String songname = "N/A";
    static int pretime = 0;
    final int[] pretimes = new int[songNames.length];
    final int[] bpms = new int[songNames.length];
    final String[] composers = new String[songNames.length];
    final int[][] difficulty = new int[songNames.length][4];
    final boolean[][] difexist = new boolean[songNames.length][4];
    static int[] difbuf = new int[] { -1, -1, -1, -1};
    int bpmbuf = 0;


    final String dir = property.get("folderName");
    Media media;
    MediaPlayer mp;
    double[][] note;//読み込んだデータを変換、一時保持するための変数
    Map<Integer, double[]> notemap = new HashMap<>();//実際に使用する譜面データ

    int bpm;//曲のBPM
    int offset;//一拍目のTick数
    int quantize;//一拍をいくつに分割するか
    int measure;//何拍で1小節か(≒拍子)
    int speed;
    int currentdif = 1;
    int selecteddif = 1;
    int menumenu = 0;

    final int criteriaP = Integer.parseInt(property.get("cP"));
    final int criteriaG = Integer.parseInt(property.get("G"));
    final int criteriaF = Integer.parseInt(property.get("F"));
    final int criteriaL = Integer.parseInt(property.get("L"));

    double score = 1234567.0;
    int notes = 0;//得点取得可能ポイントの数
    int maxCombo = 4000;
    String hantei = "null";
    int combo = 0;
    int[] breakdown = new int[] {1000, 1000, 1000, 1000};//{P,G,F,L}
    int[] judging = new int[] {0, 0, 0, 0, 0};
    double[] scores = new double[] {0, 0, 0};
    final int[] limits = new int[] {Integer.parseInt(property.get("easyLim")), Integer.parseInt(property
            .get("normalLim")), Integer.parseInt(property.get("hardLim")), Integer.parseInt(property.get("lunaLim"))};

    boolean auto = false;
    boolean cheat = false;
    boolean rokko = false;

    FrameRate fr = new FrameRate();
    static Serial serial = new Serial();

    String menuMode = "start";
    double disp;


    Stop[] stops = new Stop[] {new Stop(0, Color.color(0.45, 0.45, 0.5)), new Stop(1, Color.BLACK)};
    LinearGradient gra = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
    Stop[] stops2 = new Stop[] {new Stop(0, Color.color(0.2, 0.2, 0.2)), new Stop(1, Color.color(0, 0, 0))};
    RadialGradient gra2 = new RadialGradient(0, 0, 0.2, 0.7, 0.7, true, CycleMethod.NO_CYCLE, stops2);
    Stop[] stops3 = new Stop[] {new Stop(0, Color.color(0.4, 0.4, 0.4)), new Stop(1, Color.color(1, 1, 1))};
    LinearGradient gra3 = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops3);
    Stop[] stops4 = new Stop[] {new Stop(0, Color.color(0.2, 0.2, 0)), new Stop(1, Color.BLACK)};
    LinearGradient gra4 = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops4);



    Rectangle2D primaryScreenBounds = Screen.getPrimary().getBounds();
    final Canvas canv1 = new Canvas(fullScreen ? primaryScreenBounds.getWidth() : windowWidth,
            fullScreen ? primaryScreenBounds.getHeight() : windowHeight);
    final Canvas canv2 = new Canvas(fullScreen ? primaryScreenBounds.getWidth() : windowWidth,
            fullScreen ? primaryScreenBounds.getHeight() : windowHeight);
    BorderPane borderPane = new BorderPane();
    GraphicsContext layer2 = canv1.getGraphicsContext2D();
    GraphicsContext layer1 = canv2.getGraphicsContext2D();
    Pane pane = new Pane();
    AudioClip hit = new AudioClip(new File(dir + "/hit.mp3").toURI().toString());



    //Note noteIns = new Note();

    public static void main(String[] args) {
        launch(args);
    }

    //取っ手の取れる～♪
    //    〃 ΛΛ三Λミ
    //`｡･｡･ (ω･≡･ω) ｡･｡･｡
    //ヽﾆフ━oc  ≡oo━ヽﾆフ
    //      ミ三  ＝-彡
    //        ミ≡彡
    //ティファール♪
    //〃 Λ_Λ ミ ﾌﾞﾝ!!
    //  ( ･ω･)
    //  / ニoo━  彡
    //､(  、>
    //) ＼_)  ＼＼
    //⌒

    @Override
    public void start(Stage stage) throws Exception {
        FileOutputStream fos = new FileOutputStream("output.log");
        PrintStream ps = new PrintStream(fos);
        //System.setOut(ps); // 標準出力先の変更

        stage.setTitle("Rithm Game in Java FX!!");
        stage.setFullScreenExitHint("Press [esc] to exit game");
        stage.setWidth(fullScreen ? primaryScreenBounds.getWidth() : windowWidth);
        stage.setHeight(fullScreen ? primaryScreenBounds.getHeight() : windowHeight);
        stage.setFullScreen(fullScreen);
        stage.initStyle(StageStyle.DECORATED);
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false) {
                Platform.exit();
                System.exit(0);
                ps.close();
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Group root = new Group();

        if (mode.equals("panel")) {
            try {
                serial.main();
            } catch (Exception e) {
                mode = "key";
                System.out.println("mode : key");
            }
        }

        for (int i = 0; i < names.length; i++) {
            makeArray(Fileimport.FileImport(dir + "/" + songNames[i] + ".jra"), 1);
            names[i] = songname;
            pretimes[i] = pretime;
            pretime = 0;
            bpms[i] = bpmbuf;
            difficulty[i] = new int[] {difbuf[0], difbuf[1], difbuf[2], difbuf[3]};
            composers[i] = composer;
            for (int j = 0; j < 4; j++) {
                if (difbuf[j] != -1) difexist[i][j] = true;
            }
            Arrays.fill(difbuf, -1);
        }


        layer1.setTextAlign(TextAlignment.CENTER);
        layer1.setStroke(Color.WHITE);

        canv1.setOnKeyPressed(this::onKeyPressed);
        canv1.setOnKeyReleased(this::onKeyReleased);
        canv1.setFocusTraversable(true);
        canv2.setFocusTraversable(false);

        pane.getChildren().add(canv1);
        pane.getChildren().add(canv2);
        canv1.toFront();
        borderPane.setCenter(pane);
        root.getChildren().add(borderPane);


        stage.setScene(new Scene(root));

        layer1.setFont(Font.font("Times New Roman", FontWeight.BOLD, 48));

        media = new Media(new File(dir + "/Title.mp3").toURI().toString());
        mp = new MediaPlayer(media);
        mp.setCycleCount(MediaPlayer.INDEFINITE);
        mp.play();

        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                drawScreen(menuMode);
                frame++;
                fr.count();
                stage.setTitle("Rhythm Game in Java FX!!  frame:" + frame +
                        "  signal:{" + signal[0] + "," + signal[1] + "," + signal[2] + "," + signal[3] + "}" +
                        "  FrameRate:" + String.format("%.2f", fr.getFrameRate()) +
                        "  beats:" + (int) ((((double) frame - (double) offset) / 3600.0) * (double) bpm) +
                        "  point:" + score + "  判定:" + hantei + "  combo:" + combo +
                        "  breakdown:{Perfect,Good,Fair,Lost}={" + breakdown[0] + "," + breakdown[1] + ","
                        + breakdown[2] + "," + breakdown[3] + "}" + "  time:" + elapse);

                //パネルインプット
                if (mode.equals("panel")) {
                    Input.decision();
                }

                //入力配列作成
                makeInputArray();
                //for (int i = 0; i < 5000; i++) {
                //    layer1.setStroke(Color.color(Math.random(), Math.random(), Math.random(), Math.random()));
                //    layer1.strokeLine(0, 0, 10000, i);
                //}
            }
        }
                .start();
        stage.show();
        // クライアント領域の幅と高さ
        height = (int) stage.getScene().getHeight();
        width = (int) stage.getScene().getWidth();
        disp = ((double) width / 1920.0);
        laneHeight = height - judgeLine;
    }

    //内部呼び出し用の描画簡潔化メソッド群
    void rect(GraphicsContext gc, int x, double d, int w, double e, int r, int g, int b, double a) {
        gc.setFill(Color.rgb(r, g, b, a));
        gc.fillRect(x, d, w, e);
    }

    void parallelogram(GraphicsContext gc, double x, double y, double size, boolean left) {
        if (left) gc.fillPolygon(new double[] {x, x + width, x + width, x + (size / 3)},
                new double[] {y, y, y - size, y - size}, 4);
        else gc.fillPolygon(new double[] {x, x - width, x - width, x + (size / 3)},
                new double[] {y, y, y - size, y - size}, 4);
    }

    void parallelogramStroke(GraphicsContext gc, double x, double y, double size, boolean left) {
        if (left) gc.strokePolygon(new double[] {x, x + width, x + width, x + (size / 3)},
                new double[] {y, y, y - size, y - size}, 4);
        else gc.strokePolygon(new double[] {x, x - width, x - width, x + (size / 3)},
                new double[] {y, y, y - size, y - size}, 4);
    }

    void drawNote(int lane, double d, double currentNote, double e, double currentNote2) {
        switch ((int) currentNote) {
        case 0:
            layer2.setFill(Color.rgb(0, 0, 220));
            layer2.fillPolygon(
                    new double[] {lane * (width / 4), lane * (width / 4) + noteSize, (lane + 1) * (width / 4)
                            - noteSize, (lane + 1)
                                    * (width / 4), (lane + 1) * (width / 4) - noteSize, lane * (width / 4) + noteSize},
                    new double[] {d, d - noteSize, d - noteSize, d, d + noteSize, d + noteSize}, 6);
            break;
        case 1:
            layer2.setFill(Color.rgb(0, 220, 220));
            layer2.fillPolygon(
                    new double[] {lane * (width / 4), lane * (width / 4) + (noteSize * 1.5), (lane + 1) * (width / 4)
                            - (noteSize * 1.5), (lane + 1) * (width / 4), (lane + 1) * (width / 4)
                                    - (noteSize * 1.5), lane * (width / 4) + (noteSize * 1.5)},
                    new double[] {d, d - (noteSize * 1.5), d - (noteSize * 1.5), d, d + (noteSize * 1.5), d
                            + (noteSize * 1.5)},
                    6);
            break;
        case 2:
            layer2.setFill(Color.rgb(220, 0, 220));
            layer2.fillPolygon(
                    new double[] {lane * (width / 4), lane * (width / 4) + noteSize, (lane + 1) * (width / 4)
                            - noteSize, (lane + 1)
                                    * (width / 4), (lane + 1) * (width / 4) - noteSize, lane * (width / 4) + noteSize},
                    new double[] {d, d - noteSize, d - noteSize, d, d + noteSize, d + noteSize}, 6);
            layer2.fillPolygon(
                    new double[] {lane * (width / 4), lane * (width / 4) + noteSize, (lane + 1) * (width / 4)
                            - noteSize, (lane + 1)
                                    * (width / 4), (lane + 1) * (width / 4) - noteSize, lane * (width / 4) + noteSize},
                    new double[] {d - (e * currentNote2), d - (e * currentNote2) - noteSize, d - (e * currentNote2)
                            - noteSize, d
                                    - (e * currentNote2), d
                                            - (e * currentNote2) + noteSize, d - (e * currentNote2) + noteSize},
                    6);
            rect(layer2, lane * (width / 4) + 30, d - (e * currentNote2), width / 4 - 60, e * currentNote2, 200, 0, 200,
                    0.5);
            layer2.setStroke(Color.rgb(255, 0, 255));
            layer2.strokeLine(lane * (width / 4) + (width / 8), d - (e * currentNote2),
                    lane * (width / 4) + (width / 8),
                    d);
            break;
        case 3:
            layer2.setFill(Color.rgb(0, 220, 0));
            layer2.fillPolygon(
                    new double[] {lane * (width / 4) - slideSize - noteSize, lane * (width / 4) - slideSize, (lane + 1)
                            * (width / 4) + slideSize, (lane + 1) * (width / 4) + slideSize
                                    + noteSize, (lane + 1) * (width / 4) + slideSize, lane * (width / 4) - slideSize},
                    new double[] {d, d - noteSize, d - noteSize, d, d + noteSize, d + noteSize}, 6);
            break;

        }
    }

    void drawScreen(String condition) {

        switch (condition) {
        case "game":
            layer2.setFill(Color.color(1, 1, 1, 0));
            layer2.clearRect(0, 0, width, height);
            layer1.setLineWidth(3);
            rect(layer1, 0, 0, width, height, 0, 0, 0, 0.15);
            layer1.setFill(Color.BLACK);
            layer1.setStroke(Color.WHITE);
            if (drawJudgeLine) {
                layer1.setLineWidth(1);
                layer1.strokeLine(0, height - judgeLine, width, height - judgeLine);
            }
            for (int lane = 0; lane < 4; lane++) {
                layer1.setLineWidth(3);
                layer1.strokeLine((width / 4) * lane, 0, (width / 4) * lane, height);
                layer1.strokePolygon(
                        new double[] {lane * (width / 4), lane * (width / 4) + noteSize, (lane + 1) * (width / 4)
                                - noteSize, (lane + 1) * (width / 4), (lane + 1) * (width / 4)
                                        - noteSize, lane * (width / 4) + noteSize},
                        new double[] {height - judgeLine, height - judgeLine - noteSize, height - judgeLine
                                - noteSize, height
                                        - judgeLine, height - judgeLine + noteSize, height - judgeLine + noteSize},
                        6);
                layer1.setLineWidth(1);
                layer1.strokeLine((lane * (width / 4)) + (width / 8), 0, (lane * (width / 4)) + (width / 8), height);
            }
            if (auto) {
                layer1.setFill(Color.color(1, 1, 1));
                layer1.setFont(Font.font("Times New Roman", FontWeight.BOLD, 120));
                layer1.fillText("Auto Mode", width / 2, height / 2 - 100);
            }
            if (rokko) {
                if (frame % 10 < 5) {
                    layer2.setTextAlign(TextAlignment.CENTER);
                    layer2.setFont(Font.font("MS Mincho", FontWeight.BOLD, 90));
                    layer2.setFill(Color.rgb(255, 0, 0));
                    layer2.fillText("六甲生は一般の人たちに順番を譲ってください", width / 2, height / 3 * 2);
                }
            }

            //処理

            //if (frame > 60000) elapse = (System.nanoTime() - startNanoTime) / 1000000000.0;//スタートしてからの時間(秒)
            elapse = (Double.parseDouble(mp.getCurrentTime().toString().replaceAll(" ms", "")) + 1000.0) / 1000.0;

            if (frame == 60) {
                mp.play();
                System.out.println("play " + elapse + " " + ((double) frame / 60.0));
            }

            for (int i = 0; i < 4; i++) {
                if (signal[i] == 2 || signal[i] == 1) {
                    drawEffect("press", i);
                    //drawEffect("hit", i);
                }
                if (signal[i] == 5 || signal[i] == 4) {
                    drawEffect("attack", i);
                    //drawEffect("hit", i);
                }
            }

            frameAdjust();

            //ノートを検索、表示、タイミング判定する最強メソッド
            searchNote();

            drawInfo();

            //終了処理
            try {
                if (Double.parseDouble(
                        mp.cycleDurationProperty().get().toString().replaceAll(" ms", "")) < elapse * 1000) {
                    setup("result");
                }
            } catch (NullPointerException e) {}

            break;


        case "start":
            layer2.setTextAlign(TextAlignment.LEFT);
            layer2.setLineWidth(2);
            layer2.setFill(gra);
            layer2.setStroke(Color.color(1, 1, 1, 1));
            layer2.clearRect(0, 0, width, height);
            layer2.fillRect(0, 0, width, height);
            layer2.setFill(Color.color(1, 1, 1));
            layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 82));
            layer2.strokeText("Times New Rhythm 2018", 50, 150);
            layer2.setLineWidth(1.5);
            layer2.strokeLine(width / 15, 200, width, 200);
            layer2.strokeLine(width / 10, 220, width, 220);
            layer2.strokeLine(width / 8, 240, width, 240);
            layer2.strokeLine(width / 5, 260, width, 260);
            layer2.strokeLine(width / 3, 280, width, 280);
            layer2.strokeLine(width / 3 * 2, 200, width / 3 * 2, 280);
            layer2.strokeLine(width - 20, 200, width - 20, 280);
            layer2.setLineWidth(20);
            layer2.strokeLine(width, 210, width, 270);
            layer2.setLineWidth(1);
            if (Math.random() < 0.3 && frame % 2 == 0) {
                layer2.fillOval(width - 35, 225, 10, 10);
                layer2.fillOval(width - 35, 245, 10, 10);
            }
            layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 60));
            layer2.setTextAlign(TextAlignment.CENTER);
            if (frame % 240 < 120) {
                alpha = ((double) frame % 240.0) / 150.0 + 0.2;
            } else {
                alpha = 1.8 - ((double) frame % 240.0) / 150.0;
            }
            layer2.setFill(Color.color(1, 1, 1, alpha));
            layer2.fillText("Touch Any Panel to Start", width / 2, height / 4 * 3);
            layer2.setFill(Color.color(1, 1, 1));
            layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 25));
            layer2.setTextAlign(TextAlignment.RIGHT);
            layer2.fillText("Ver 1.0.0 Released", width - 50, height - 50);
            layer2.setTextAlign(TextAlignment.LEFT);
            layer2.fillText("Using Java SE - 1.8", 50, height - 50);
            for (int i = 0; i < 4; i++) {
                if (signal[i] == 1 || signal[i] == 4) {
                    setup("menu");
                }
            }
            break;


        case "menu":
            AFrame = AFrame > 0 ? AFrame >= 20 ? 0 : AFrame + 1 : AFrame < 0 ? AFrame <= -20 ? 0 : AFrame - 1 : 0;
            SFrame = select ? SFrame < 10 ? SFrame + 1 : SFrame : SFrame > 0 ? SFrame - 1 : SFrame;
            layer2.setTextAlign(TextAlignment.LEFT);
            layer2.clearRect(0, 0, width, height);
            stops2 = new Stop[] {new Stop(0, Color.color(0.2, 0.2, 0.2)), new Stop(1, Color.color(0, 0, 0))};
            gra2 = new RadialGradient(0, 0, 0.2, 0.7, 0.7, true, CycleMethod.NO_CYCLE, stops2);
            layer2.setFill(gra2);
            layer2.fillRect(0, 0, width, height);
            layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 70 * disp));

            for (int i = 0; i < 4; i++) {//難易度選択
                if (select) {
                    if (difexist[currentSong][i]) {
                        stops3 = new Stop[] {new Stop(0, Color.color(0.5, 0.5, 0)), new Stop(1,
                                Color.color(1, 1, 0))};
                        gra3 = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops3);
                        layer2.setFill(gra3);
                        parallelogram(layer2, (680 + i * 40) * disp * 10 / SFrame, (710 + i * 110) * disp * 10 / SFrame,
                                100 * disp, true);
                        layer2.setFill(Color.color(1, 1, 1));
                        layer2.fillText(difficulties[i], (720 + i * 40) * disp * 10 / SFrame,
                                (690 + i * 110) * disp * 10 / SFrame);
                        layer2.setTextAlign(TextAlignment.RIGHT);
                        layer2.fillText(String.valueOf(difficulty[currentSong][i]), width - 50,
                                (690 + i * 110) * disp * 10 / SFrame);
                        layer2.setTextAlign(TextAlignment.LEFT);
                        if (selecteddif - 1 == i && frame > 0) {
                            layer2.setStroke(Color.color(1, 1, 1,
                                    frame % 40 >= 20 ? (double) (20 - frame % 20) / 20.0
                                            : (double) (frame % 20) / 20.0));
                            layer2.setLineWidth(5 * disp);
                            parallelogramStroke(layer2, (680 + i * 40) * disp * 10 / SFrame,
                                    (710 + i * 110) * disp * 10 / SFrame,
                                    100 * disp, true);
                            layer2.setStroke(Color.color(1, 1, 1));
                        }
                    }
                }
            }

            layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 50 * disp));
            for (int i = 0; i < 6; i++) {//選択してるのよりも下
                stops3 = new Stop[] {new Stop(0,
                        Color.color(0.4, 0.4, 0.4,
                                Math.pow(((double) i - (double) AFrame / 20.0 + 1.0) / 7.0, 2))), new Stop(1,
                                        Color.color(1, 1, 1,
                                                Math.pow(((double) i - (double) AFrame / 20.0 + 1.0) / 7.0, 2)))};
                gra3 = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops3);
                layer2.setFill(gra3);
                if (i != 5 || AFrame > 0) {
                    parallelogram(layer2, (950 - i * 30 + AFrame * (30.0 / 20.0)) * disp + SFrame * 15 * disp,
                            (1080 - i * 80 + AFrame * (80.0 / 20.0)) * disp + SFrame * 45 * disp, 70 * disp, true);
                } else if (AFrame <= 0) {
                    parallelogram(layer2, (950 - i * 30 + AFrame * (250.0 / 20.0)) * disp + SFrame * 15 * disp,
                            (1080 - i * 80 + AFrame * (80.0 / 20.0)) * disp + SFrame * 45 * disp,
                            (70 - AFrame * (80.0 / 20.0)) * disp,
                            true);
                }
                layer2.setFill(Color.color(1, 1, 1, Math.pow(((double) i - ((double) AFrame / 20.0)) / 7.0, 2)));
                if (AFrame == 0 && currentSong + (6 - i) < names.length) {
                    layer2.fillText(names[currentSong + (6 - i)],
                            (970 - (i * 30) + AFrame * (30.0 / 20.0)) * disp + SFrame * 15 * disp,
                            (1060 - (i * 80) + AFrame * (80.0 / 20.0)) * disp + SFrame * 45 * disp);
                } else if (AFrame == 0) {
                    layer2.fillText(names[currentSong + (6 - i) - names.length],
                            (970 - (i * 30) + AFrame * (30.0 / 20.0)) * disp + SFrame * 15 * disp,
                            (1060 - (i * 80) + AFrame * (80.0 / 20.0)) * disp + SFrame * 45 * disp);
                }
                if (AFrame > 0) {
                    if (currentSong + (6 - i) + 1 < names.length) {
                        layer2.fillText(names[currentSong + (6 - i) + 1],
                                (970 - (i * 30) + AFrame * (30.0 / 20.0)) * disp + SFrame * 15 * disp,
                                (1060 - (i * 80) + AFrame * (80.0 / 20.0)) * disp + SFrame * 45 * disp);
                    } else {
                        layer2.fillText(names[currentSong + (6 - i) + 1 - names.length],
                                (970 - (i * 30) + AFrame * (30.0 / 20.0)) * disp + SFrame * 15 * disp,
                                (1060 - (i * 80) + AFrame * (80.0 / 20.0)) * disp + SFrame * 45 * disp);
                    }
                }
                if (AFrame < 0) {
                    if (i != 5) {
                        if (currentSong + (6 - i) - 1 < names.length) {
                            layer2.fillText(names[currentSong + (6 - i) - 1],
                                    (970 - (i * 30) + AFrame * (30.0 / 20.0)) * disp + SFrame * 15 * disp,
                                    (1060 - (i * 80) + AFrame * (80.0 / 20.0)) * disp + SFrame * 45 * disp);
                        } else {
                            layer2.fillText(names[currentSong + (6 - i) - 1 - names.length],
                                    (970 - (i * 30) + AFrame * (30.0 / 20.0)) * disp + SFrame * 15 * disp,
                                    (1060 - (i * 80) + AFrame * (80.0 / 20.0)) * disp + SFrame * 45 * disp);
                        }
                    } else {
                        if (currentSong + (6 - i) - 1 < names.length) {
                            layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD,
                                    (50 + AFrame * ( -30.0 / 20.0)) * disp));
                            layer2.fillText(names[currentSong + (6 - i) - 1],
                                    (970 - (i * 30) + AFrame * (250.0 / 20.0)) * disp + SFrame * 15 * disp,
                                    (1060 - (i * 80) + AFrame * (80.0 / 20.0)) * disp + SFrame * 45 * disp);
                        } else {
                            layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD,
                                    (50 + AFrame * ( -30.0 / 20.0)) * disp));
                            layer2.fillText(names[currentSong + (6 - i) - 1 - names.length],
                                    (970 - (i * 30) + AFrame * (250.0 / 20.0)) * disp + SFrame * 15 * disp,
                                    (1060 - (i * 80) + AFrame * (80.0 / 20.0)) * disp + SFrame * 45 * disp);
                        }
                    }
                    layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 50 * disp));
                }

            }

            //選択中のブロック
            if (AFrame == 0) {
                stops3 = new Stop[] {new Stop(0, Color.color(0.4, 0.4, 0.4, 1)), new Stop(1,
                        Color.color(1, 1, 1, 1))};
                gra3 = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops3);
                layer2.setFill(gra3);
                parallelogram(layer2, 550 * disp, 600 * disp, 150 * disp, true);
                layer2.setFill(Color.color(1, 1, 1));
                layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 80 * disp));
                layer2.fillText(names[currentSong], 570 * disp, 580 * disp);
            } else if (AFrame > 0) {
                stops3 = new Stop[] {new Stop(0,
                        Color.color(0.4, 0.4, 0.4,
                                Math.pow(((double) 6 - (double) AFrame / 20.0 + 1.0) / 7.0, 2))), new Stop(1,
                                        Color.color(1, 1, 1,
                                                Math.pow(((double) 6 - (double) AFrame / 20.0 + 1.0) / 7.0, 2)))};
                gra3 = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops3);
                layer2.setFill(gra3);
                parallelogram(layer2, (550 + AFrame * (250.0 / 20.0)) * disp, (600 + AFrame * (80.0 / 20.0)) * disp,
                        (150 - AFrame * (80.0 / 20.0)) * disp, true);
                layer2.setFill(Color.color(1, 1, 1, 1 - (double) AFrame / 50.0));
                layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, (80 - AFrame * (30.0 / 20.0)) * disp));
                try {
                    layer2.fillText(names[currentSong + 1], (570 + AFrame * (250.0 / 20.0)) * disp,
                            (580 + AFrame * (80.0 / 20.0)) * disp);
                } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                    layer2.fillText(names[0], (570 + AFrame * (250.0 / 20.0)) * disp,
                            (580 + AFrame * (80.0 / 20.0)) * disp);
                }

            } else if (AFrame < 0) {
                stops3 = new Stop[] {new Stop(0,
                        Color.color(0.4, 0.4, 0.4,
                                Math.pow(((double) 6 - (double) -AFrame / 20.0 + 1.0) / 7.0, 2))), new Stop(1,
                                        Color.color(1, 1, 1,
                                                Math.pow(((double) 6 - (double) -AFrame / 20.0 + 1.0) / 7.0, 2)))};
                gra3 = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops3);
                layer2.setFill(gra3);
                parallelogram(layer2, (550 - AFrame * (190.0 / 20.0)) * disp, (600 + AFrame * (160.0 / 20.0)) * disp,
                        (150 + AFrame * (80.0 / 20.0)) * disp, true);
                layer2.setFill(Color.color(1, 1, 1, 1 - (double) -AFrame / 40.0));
                layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, (80 + AFrame * (30.0 / 20.0)) * disp));
                try {
                    layer2.fillText(names[currentSong - 1], (570 - AFrame * (190.0 / 20.0)) * disp,
                            (580 + AFrame * (160.0 / 20.0)) * disp);
                } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                    layer2.fillText(names[names.length - 1], (570 - AFrame * (190.0 / 20.0)) * disp,
                            (580 + AFrame * (160.0 / 20.0)) * disp);
                }
            }

            //選択してるのより上
            for (int i = 0; i < 4; i++) {
                stops3 = new Stop[] {new Stop(0,
                        Color.color(0.4, 0.4, 0.4,
                                Math.pow(((double) i + (double) AFrame / 20.0 + 1.0) / 5.0, 2))), new Stop(1,
                                        Color.color(1, 1, 1,
                                                Math.pow(((double) i + (double) AFrame / 20.0 + 1.0) / 5.0, 2)))};
                gra3 = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops3);
                layer2.setFill(gra3);
                if (i != 3 || AFrame < 0) {
                    parallelogram(layer2, (650 + i * 30 + AFrame * (30.0 / 20.0)) * disp,
                            (200 + i * 80 + AFrame * (80.0 / 20.0)) * disp, 70 * disp, true);
                } else if (AFrame >= 0) {
                    parallelogram(layer2, (650 + i * 30 - AFrame * (190.0 / 20.0)) * disp,
                            (200 + i * 80 + AFrame * (160.0 / 20.0)) * disp, (70 + AFrame * (80.0 / 20.0)) * disp,
                            true);
                }
                layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 50 * disp));
                layer2.setFill(Color.color(1, 1, 1, Math.pow(((double) i + ((double) AFrame / 20.0)) / 5.0, 2)));
                if (AFrame == 0 && currentSong + (i - 4) >= 0) {
                    layer2.fillText(names[currentSong + (i - 4)], (670 + (i * 30) + AFrame * (30.0 / 20.0)) * disp,
                            (180 + (i * 80) + AFrame * (80.0 / 20.0)) * disp);
                } else if (AFrame == 0) {
                    layer2.fillText(names[currentSong + (i - 4) + names.length],
                            (670 + (i * 30) + AFrame * (30.0 / 20.0)) * disp,
                            (180 + (i * 80) + AFrame * (80.0 / 20.0)) * disp);
                }
                if (AFrame > 0) {
                    if (i != 3) {
                        if (currentSong + (i - 4) + 1 >= 0) {
                            layer2.fillText(names[currentSong + (i - 4) + 1],
                                    (670 + (i * 30) + AFrame * (30.0 / 20.0)) * disp,
                                    (180 + (i * 80) + AFrame * (80.0 / 20.0)) * disp);
                        } else {
                            layer2.fillText(names[currentSong + (i - 4) + 1 + names.length],
                                    (670 + (i * 30) + AFrame * (30.0 / 20.0)) * disp,
                                    (180 + (i * 80) + AFrame * (80.0 / 20.0)) * disp);
                        }
                    } else {
                        layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD,
                                (50 + AFrame * (30.0 / 20.0)) * disp));
                        if (currentSong + (i - 4) + 1 >= 0) {
                            layer2.fillText(names[currentSong + (i - 4) + 1],
                                    (670 + (i * 30) - AFrame * (190.0 / 20.0)) * disp,
                                    (180 + (i * 80) + AFrame * (160.0 / 20.0)) * disp);
                        } else {
                            layer2.fillText(names[currentSong + (i - 4) + 1 + names.length],
                                    (670 + (i * 30) - AFrame * (190.0 / 20.0)) * disp,
                                    (180 + (i * 80) + AFrame * (160.0 / 20.0)) * disp);
                        }
                    }
                    layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 50 * disp));
                } else if (AFrame < 0) {
                    if (currentSong + (i - 4) - 1 >= 0) {
                        layer2.fillText(names[currentSong + (i - 4) - 1],
                                (670 + (i * 30) + AFrame * (30.0 / 20.0)) * disp,
                                (180 + (i * 80) + AFrame * (80.0 / 20.0)) * disp);
                    } else {
                        layer2.fillText(names[currentSong + (i - 4) - 1 + names.length],
                                (670 + (i * 30) + AFrame * (30.0 / 20.0)) * disp,
                                (180 + (i * 80) + AFrame * (80.0 / 20.0)) * disp);
                    }
                }
            }
            stops3 = new Stop[] {new Stop(0, Color.color(0.4, 0.4, 0.4, 1)), new Stop(1, Color.color(1, 1, 1, 1))};
            gra3 = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops3);
            layer2.setFill(gra3);
            //parallelogram(layer2, 550 * disp, 600 * disp, 150 * disp, true);
            layer2.setFill(Color.color(0.4, 0.4, 0.4, 0.3));
            parallelogram(layer2, 1000 * disp, 150 * disp, 150 * disp, false);
            parallelogram(layer2, 1010 * disp, 150 * disp, 150 * disp, true);
            layer2.setFill(Color.color(1, 1, 1));
            layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 90 * disp));
            layer2.fillText("Music Select", 100 * disp, 100 * disp);
            layer2.fillText("E" + difficulty[currentSong][0] + " N" + difficulty[currentSong][1] + " H"
                    + difficulty[currentSong][2] + " L" + difficulty[currentSong][3], 1100 * disp, 100 * disp);
            layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 80 * disp));
            //layer2.fillText(names[currentSong], 570 * disp, 580 * disp);
            layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 30 * disp));
            layer2.setTextAlign(TextAlignment.LEFT);
            layer2.setFill(Color.color(1, 1, 1));
            try {
                jacket = new Image(new File(dir + "/" + songNames[currentSong] + ".jpg").toURI().toString());
                layer2.drawImage(jacket, 100 * disp, 300 * disp,
                        300 * disp, 300 * disp);
            } catch (Exception e) {
                System.out.println(names[currentSong] + ": Jacket image not found");
            }
            layer2.fillText("BPM:" + bpms[currentSong] + "\r\n  Music:" + composers[currentSong], 100 * disp,
                    635 * disp);
            layer2.setLineWidth(2);
            layer2.strokeRect(100 * disp, 300 * disp, 300 * disp, 300 * disp);
            layer2.setLineWidth(1);
            layer2.strokeLine(100 * disp, 680 * disp, 400 * disp, 680 * disp);
            //layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 70 * disp));
            //layer2.fillText("Difficulty:" + difficulties[selecteddif - 1], 100 * disp, 800 * disp);
            layer2.setLineWidth(2);
            layer2.strokeRect(50 * disp, 850 * disp, 140 * disp, 200 * disp);
            layer2.strokeRect(200 * disp, 850 * disp, 140 * disp, 200 * disp);
            layer2.strokeRect(350 * disp, 850 * disp, 140 * disp, 200 * disp);
            layer2.strokeRect(500 * disp, 850 * disp, 140 * disp, 200 * disp);
            //↑
            layer2.strokeLine(270 * disp, 940 * disp, 250 * disp, 960 * disp);
            layer2.strokeLine(270 * disp, 940 * disp, 290 * disp, 960 * disp);
            layer2.setFont(Font.font("MS Mincho", FontWeight.BOLD, 35 * disp));
            layer2.setTextAlign(TextAlignment.CENTER);
            if ( !select) layer2.fillText("曲選択", 270 * disp, 1010 * disp);
            else layer2.fillText("難易度", 270 * disp, 1010 * disp);
            //↓
            layer2.strokeLine(570 * disp, 960 * disp, 550 * disp, 940 * disp);
            layer2.strokeLine(570 * disp, 960 * disp, 590 * disp, 940 * disp);
            if ( !select) layer2.fillText("曲選択", 570 * disp, 1010 * disp);
            else layer2.fillText("難易度", 570 * disp, 1010 * disp);
            //←
            layer2.strokeLine(130 * disp, 970 * disp, 110 * disp, 950 * disp);
            layer2.strokeLine(130 * disp, 930 * disp, 110 * disp, 950 * disp);
            layer2.fillText("戻る", 120 * disp, 1010 * disp);
            //→
            layer2.strokeLine(410 * disp, 970 * disp, 430 * disp, 950 * disp);
            layer2.strokeLine(410 * disp, 930 * disp, 430 * disp, 950 * disp);
            layer2.fillText("決定", 420 * disp, 1010 * disp);
            //layer2.fillText("D", 270*disp, 1000*disp);
            //layer2.strokeLine(270*disp, 900*disp, 290*disp, 920*disp);
            //layer2.strokeLine(270*disp, 900*disp, 250*disp, 920*disp);
            if ((signal[1] == 1 || signal[1] == 2 || signal[1] == 4 || signal[1] == 5) && AFrame == 0 && frame > 0) {
                if ( !select) {
                    if (currentSong > 0) currentSong--;
                    else currentSong = names.length - 1;
                    try {
                        mp.stop();
                    } catch (Exception e) {}
                    media = new Media(new File(dir + "/" + songNames[currentSong] + ".mp3").toURI().toString());
                    mp = new MediaPlayer(media);
                    mp.setVolume(0.5);
                    mp.setStartTime(new Duration(pretimes[currentSong]));
                    mp.setCycleCount(MediaPlayer.INDEFINITE);
                    mp.play();
                    AFrame++;
                    System.out.println("next");
                } else if (signal[1] == 1 || signal[1] == 4) {
                    for (;;) {
                        selecteddif = selecteddif == 1 ? 4 : selecteddif - 1;
                        if ( !difexist[currentSong][selecteddif - 1]) ;
                        else break;
                    }
                }
            }
            if ((signal[3] == 1 || signal[3] == 2 || signal[3] == 4 || signal[3] == 5) && AFrame == 0 && frame > 0) {
                if ( !select) {
                    if (currentSong < names.length - 1) currentSong++;
                    else currentSong = 0;
                    try {
                        mp.stop();
                    } catch (Exception e) {}
                    media = new Media(new File(dir + "/" + songNames[currentSong] + ".mp3").toURI().toString());
                    mp = new MediaPlayer(media);
                    mp.setVolume(0.5);
                    mp.setStartTime(new Duration(pretimes[currentSong]));
                    mp.setCycleCount(MediaPlayer.INDEFINITE);
                    mp.play();
                    AFrame--;
                } else if (signal[3] == 1 || signal[3] == 4) {
                    for (;;) {
                        selecteddif = selecteddif == 4 ? 1 : selecteddif + 1;
                        if ( !difexist[currentSong][selecteddif - 1]) ;
                        else break;
                    }
                }
            }
            //            if (signal[1]==1) {
            //                selecteddif = selecteddif==4 ? 1 : selecteddif+1;
            //            }
            if (signal[2] == 1 || signal[2] == 4) {
                //gameStart(songNames[currentSong], selecteddif);
                if ( !select) {
                    select = true;
                    for (;;) {
                        if ( !difexist[currentSong][selecteddif - 1]) ;
                        else break;
                        selecteddif = selecteddif == 4 ? 1 : selecteddif + 1;
                    }
                } else {
                    gameStart(songNames[currentSong], selecteddif);
                }
            }
            if (signal[0] == 1 || signal[0] == 4) {
                if (select) select = false;
                else setup("start");
            }
            break;


        case "result":
            for (int i = 0; i < 4; i++) {
                if (signal[i] == 1 || signal[i] == 4) {
                    if ( !skipped && frame < 700) skipped = true;
                    else setup("start");
                }
            }
            stops4 = new Stop[] {new Stop(0, Color.color(0.2, 0.2, 0)), new Stop(1, Color.BLACK)};
            if (clearStatus.equals("Failure")) {
                stops4 = new Stop[] {new Stop(0, Color.color(0, 0, 0.3)), new Stop(1, Color.BLACK)};
            }
            gra4 = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops4);
            layer2.setFill(gra4);
            layer2.clearRect(0, 0, width, height);
            layer2.fillRect(0, 0, width, height);
            layer2.setLineWidth(2);
            layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 120 * disp));
            layer2.setTextAlign(TextAlignment.LEFT);
            if ( !skipped && frame < 60) {
                layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 120 * disp));
                layer2.setFill(Color.color(1, 1, 1, (double) frame / 60.0));
                if ( !cheat) layer2.fillText("Result", 150 * disp,
                        (double) frame * 2.0 * disp);
                else layer2.fillText("Result(Auto Play)", 150 * disp,
                        (double) frame * 2.0 * disp);
                layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 70 * disp));
                layer2.setTextAlign(TextAlignment.CENTER);
                layer2.fillText(songname + "  -" + difficulties[currentdif - 1], width / 2,
                        200 * disp);
                layer2.setTextAlign(TextAlignment.LEFT);
            } else {
                layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 120 * disp));
                layer2.setFill(Color.color(1, 1, 1));
                if ( !cheat)
                    layer2.fillText("Result", 150 * disp, 120 * disp);
                else layer2.fillText("Result(Auto Play)", 150 * disp, 120 * disp);
                layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 70 * disp));
                layer2.setTextAlign(TextAlignment.CENTER);
                layer2.fillText(songname + "  -" + difficulties[currentdif - 1], width / 2, 200 * disp);
                layer2.setTextAlign(TextAlignment.LEFT);
                if ( !skipped && frame < 90) {
                    layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 80 * disp));
                    layer2.setFill(Color.color(1, 1, 1, (double) (frame - 60) / 30.0));
                    layer2.fillText("score:", (double) frame * 4.44 * disp, 400 * disp);
                } else {
                    layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 80 * disp));
                    layer2.setFill(Color.color(1, 1, 1));
                    layer2.fillText("score:", 400 * disp, 400 * disp);
                    layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 150 * disp));
                    if ( !skipped && frame < 120) {
                        layer2.strokeText(String.format("%07d", (int) (Math.random() * 1000000.0)), 600 * disp,
                                400 * disp);
                    } else if ( !skipped && frame < 150) {
                        layer2.strokeText(String.format("%06d", (int) (Math.random() * 100000.0))
                                + String.format("%01d", (int) (score % 10)), 600 * disp, 400 * disp);
                    } else if ( !skipped && frame < 180) {
                        layer2.strokeText(String.format("%05d", (int) (Math.random() * 10000.0))
                                + String.format("%02d", (int) (score % 100)), 600 * disp, 400 * disp);
                    } else if ( !skipped && frame < 210) {
                        layer2.strokeText(String.format("%04d", (int) (Math.random() * 1000.0))
                                + String.format("%03d", (int) (score % 1000)), 600 * disp, 400 * disp);
                    } else if ( !skipped && frame < 240) {
                        layer2.strokeText(String.format("%03d", (int) (Math.random() * 100.0))
                                + String.format("%04d", (int) (score % 10000)), 600 * disp, 400 * disp);
                    } else if ( !skipped && frame < 270) {
                        layer2.strokeText(String.format("%02d", (int) (Math.random() * 10.0))
                                + String.format("%05d", (int) (score % 100000)), 600 * disp, 400 * disp);
                    } else {
                        layer2.strokeText(String.format("%07d", (int) score), 600 * disp, 400 * disp);
                        if (clearStatus.equals("Failure")) {
                            layer2.setFill(Color.rgb(0, 0, 180));
                        } else {
                            layer2.setFill(Color.rgb(180, 180, 0));
                        }
                        if ( !skipped && frame < 420) {
                            if (frame % 20 < 10 && clearStatus.equals("Success")) {
                                layer2.fillText(clearStatus, 1200 * disp, 400 * disp);
                            } else if (clearStatus.equals("Failure")) {
                                layer2.setFill(Color.rgb(0, 0, 180, (double) (frame - 270) / 150.0));
                                layer2.fillText(clearStatus, 1200 * disp, 400 * disp);
                            }
                        } else {
                            layer2.fillText(clearStatus, 1200 * disp, 400 * disp);
                        }
                    }
                }
            }

            if (skipped || frame > 450) {
                layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 70 * disp));
                layer2.setTextAlign(TextAlignment.RIGHT);
                if ( !skipped && frame < 480) {
                    layer2.setFill(Color.color(1, 1, 1, (double) (frame - 450) / 30.0));
                    layer2.fillText("Perfect:\nGood:\nFair:\nLost:", 600 * disp, 600 * disp);
                } else {
                    layer2.setFill(Color.color(1, 1, 1));
                    layer2.fillText("Perfect:\nGood:\nFair:\nLost:", 600 * disp, 600 * disp);
                    layer2.setTextAlign(TextAlignment.CENTER);
                    if ( !skipped && frame < 510) {
                        layer2.setFill(Color.color(1, 1, 1, (double) (frame - 480) / 30.0));
                        layer2.fillText(breakdown[0] + "\n" + breakdown[1] + "\n" + breakdown[2] + "\n" + breakdown[3],
                                680 * disp, 600 * disp);
                    } else {
                        layer2.fillText(breakdown[0] + "\n" + breakdown[1] + "\n" + breakdown[2] + "\n" + breakdown[3],
                                680 * disp, 600 * disp);
                        if ( !skipped && frame < 540) {
                            layer2.setFill(Color.color(1, 1, 1, (double) (frame - 510) / 30.0));
                            layer2.fillText("Max combo:", 1120 * disp, 600 * disp);
                        } else {
                            layer2.setFill(Color.color(1, 1, 1));
                            layer2.fillText("Max combo:", 1120 * disp, 600 * disp);
                            if ( !skipped && frame < 570) {
                                layer2.setTextAlign(TextAlignment.LEFT);
                                layer2.setFill(Color.color(1, 1, 1, (double) (frame - 540) / 30.0));
                                layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 100 * disp));
                                layer2.fillText(maxCombo + "", 1320 * disp, 600 * disp);
                                layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 70 * disp));
                                layer2.setTextAlign(TextAlignment.CENTER);
                            } else {
                                layer2.setTextAlign(TextAlignment.LEFT);
                                layer2.setFill(Color.color(1, 1, 1));
                                layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 100 * disp));
                                layer2.fillText(maxCombo + "", 1320 * disp, 600 * disp);
                                layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 70 * disp));
                                layer2.setTextAlign(TextAlignment.CENTER);
                                if ( !skipped && frame < 600) {
                                    layer2.setFill(Color.color(1, 1, 1, (double) (frame - 570) / 30.0));
                                    layer2.fillText("rate:", 1000 * disp, 850 * disp);
                                } else {
                                    layer2.setFill(Color.color(1, 1, 1));
                                    layer2.fillText("rate:", 1000 * disp, 850 * disp);
                                    layer2.setTextAlign(TextAlignment.LEFT);
                                    layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 210 * disp));
                                    layer2.setFill(rankColor);
                                    if ( !skipped && frame < 720) {
                                        if (frame % 20 < 10 && clearStatus.equals("Success")) {
                                            layer2.fillText(clearRank, 1200 * disp, 850 * disp);
                                        } else if (clearStatus.equals("Failure")) {
                                            layer2.setFill(Color.color(rankColor.getRed(), rankColor.getGreen(),
                                                    rankColor.getBlue(), (double) (frame - 600) / 120));
                                            layer2.fillText(clearRank, 1200 * disp, 850 * disp);
                                        }
                                    } else {
                                        layer2.fillText(clearRank, 1200 * disp, 850 * disp);
                                        if (frame % 120 < 60) {
                                            alpha = ((double) frame % 120.0) / 60.0;
                                        } else {
                                            alpha = 2.0 - ((double) frame % 120.0) / 60.0;
                                        }
                                        layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 60 * disp));
                                        layer2.setFill(Color.color(1, 1, 1, alpha));
                                        layer2.fillText("Touch Any Panel to Return", 1000 * disp, 950 * disp);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            break;
        case "mending":
            layer1.setFill(gra);
            layer1.fillRect(0, 0, width, height);
            layer2.clearRect(0, 0, width, height);
            layer1.setFont(Font.font("MS Mincho", FontWeight.BOLD, 500 * disp));
            layer1.setTextAlign(TextAlignment.CENTER);
            layer1.setFill(Color.color(1, 1, 1));
            layer1.fillText("調整中", width / 2, height / 2);
        }
    }

    void drawInfo() {
        layer2.setLineWidth(2);
        layer2.setStroke(Color.WHITE);
        layer2.setFill(Color.rgb(100, 100, 100, 0.5));
        layer2.fillPolygon(new double[] {0, 0, 200, 235}, new double[] {0, 80, 80, 0}, 4);
        layer2.fillPolygon(new double[] {240, 205, width / 2 - (comboSize + 35) - 5, width / 2 - comboSize - 5},
                new double[] {0, 80, 80, 0}, 4);
        layer2.fillPolygon(new double[] {width, width, width / 2 + (comboSize + 35) + 5, width / 2 + comboSize + 5},
                new double[] {0, 80, 80, 0}, 4);
        layer2.fillPolygon(new double[] {width / 2 - (comboSize + 35), width / 2 - comboSize, width / 2
                + comboSize, width / 2 + (comboSize + 35)}, new double[] {80, 0, 0, 80}, 4);
        layer2.setTextAlign(TextAlignment.CENTER);
        layer2.setFill(Color.color(0, 0.8, 0.8));
        layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 62));
        layer2.fillText("" + combo, width / 2, 48);
        layer2.setTextAlign(TextAlignment.LEFT);
        layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 32));
        layer2.strokeText("score:", 5, 28);
        layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 48));
        layer2.strokeText(String.format("%07d", (int) score), 25, 65);
        layer2.setFill(Color.rgb(255, 255, 255));
        layer2.setTextAlign(TextAlignment.RIGHT);
        layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 28));
        layer2.fillText(songname, width - 30, 28);
        layer2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 22));
        layer2.fillText(difficulties[currentdif - 1], width - 10, 60);
        layer2.setFill(Color.color(0.9, 0.9, 0.9, 0.5));
        try {
            layer2.fillPolygon(new double[] {0, 0, (elapse * 1000)
                    / (Double.parseDouble(mp.cycleDurationProperty().get().toString().replaceAll(" ms", ""))) * 1920
                    * disp
                    + 35, (elapse * 1000)
                            / (Double.parseDouble(mp.cycleDurationProperty().get().toString().replaceAll(" ms", "")))
                            * 1920
                            * disp},
                    new double[] {80, 0, 0, 80}, 4);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        //layer2.fillOval((elapse*1000)/(Double.parseDouble(mp.cycleDurationProperty().get().toString().replaceAll(" ms", "")))*1920*disp, 0, 10, 10);

    }

    void drawEffect(String kind, int lane) {
        switch (kind) {
        case "press":
            stops3 = new Stop[] {new Stop(0, Color.color(0, 0, 0, 0)), new Stop(1,
                    Color.color(0.5, 0.5, 0.5, 0.4))};
            gra3 = new LinearGradient(0.5, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, stops3);
            layer1.setFill(gra3);
            layer1.fillRect(lane * (width / 4), 0, width / 4, height);
            break;
        case "attack":
            stops3 = new Stop[] {new Stop(0, Color.color(0, 0, 0, 0)), new Stop(1,
                    Color.color(0.6, 0.6, 0.4, 0.4))};
            gra3 = new LinearGradient(0.5, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, stops3);
            layer1.setFill(gra3);
            layer1.fillRect(lane * (width / 4), 0, width / 4, height);
            break;
        case "hit":
            //            stops2 = new Stop[] {new Stop(0, Color.color(0, 0, 0, 0)), new Stop(0.5,
            //                    Color.color(0.7, 0.7, 0.7, 0.3)), new Stop(1, Color.color(0, 0, 0, 0))};
            //            gra2 = new RadialGradient(0, 0, 0.5, (double) (height - judgeLine) / (double) height, 0.1, true,
            //                    CycleMethod.NO_CYCLE, stops3);
            //            layer1.setFill(gra2);
            //            layer1.fillRect(lane * (width / 4), 0, width / 4, height);
            break;
        }
    }

    void frameAdjust() {
        if (elapse - ((double) frame / 60.0) > (1.0 / 60.0) && frame > 61) {
            System.out.println("plus  " + (elapse - ((double) frame / 60.0)) + " " + frame);
            frame++;
            if (elapse - ((double) frame / 60.0) > (1.0 / 60.0)) {
                frame += (elapse - ((double) frame / 60.0)) * 60;
            }
        }
        if (elapse - ((double) frame / 60.0) < -(1.0 / 60.0) && frame > 61) {
            System.out.println("minus " + (elapse - ((double) frame / 60.0)) + " " + frame);
            frame--;
        }
    }

    void searchNote() {
        //画面内(+長押し可能ライン)にいるノートについて検査
        for (int i = -(int) (searchWidth * 60.0); i < laneHeight; i++) {
            int loop = i + frame + (currentdif * 1000000);
            for (int lane = 0; lane < 5; lane++) {
                loop += 100000;
                double[] currentNote = notemap.get(loop);
                if (currentNote != null && currentNote[6] == currentdif) {

                    //ノートを表示
                    if (currentNote[3] != 2 && lane != 4) {
                        drawNote(lane,
                                laneHeight - ((currentNote[0] - frame) * currentNote[5]),
                                currentNote[2], currentNote[4] - currentNote[0], currentNote[5]);
                    }

                    if (i == 0 && currentNote[2] == 6) {
                        speed = (int) currentNote[4];
                    }

                    //タイミング判定
                    if ( !auto) {//オートモード
                        if (signal[lane] == 1 || signal[lane] == 4) {//ボタンが押された瞬間であるか？
                            if (i < criteriaL && i > -criteriaL && currentNote[3] == 0) {//判定ライン内にいて判定済みでないか？
                                judging[lane] = loop;
                                if (currentNote[2] != 1) {
                                    judge(currentNote[2], lane, loop, i);
                                } else if (signal[lane] == 1) {
                                    judge(0, lane, loop, i);
                                } else {
                                    judge(currentNote[2], lane, loop, i);
                                }
                                signal[lane] = 2;
                            }
                        }
                        if (signal[lane] == 2 || signal[lane] == 5) {//長押し状態であるか？
                            if (currentNote[2] == 2 && currentNote[3] == 1) {//長押しノートが判定中であるか？
                                if (currentNote[4] > frame) {
                                    judge(currentNote[2] + 2, lane, loop, 0);
                                } else {
                                    judge(currentNote[2] + 3, lane, loop, 0);
                                }
                            }
                            if (currentNote[2] == 3) {//スライドノート
                                if (i < 0 && i > -criteriaL && currentNote[3] == 0) {
                                    judge(currentNote[2], lane, loop, i);
                                }
                            }
                        } else if (signal[lane] == 3 || signal[lane] == 6) {
                            if (currentNote[2] == 2 && currentNote[3] == 1) {//長押し判定中にボタンが離される
                                if (frame > loop % 100000 && currentNote[4] >= frame - 10) {
                                    judge(currentNote[2] + 3, lane, loop, 1);
                                } else if (frame > loop % 100000 && currentNote[4] >= frame) {
                                    judge(currentNote[2] + 3, lane, loop, 0);
                                }
                            }
                        }
                        if (i < -criteriaL && currentNote[3] == 0) {//判定ライン外に未判定のものがあれば
                            judge(currentNote[2], lane, loop, criteriaL);
                        }
                    } else if (lane != 4) {//オートモードでコマンドノートではないなら
                        if ((i == 0 || i < 0) && currentNote[3] == 0) {
                            signal[lane] = 1;
                            judge(currentNote[2], lane, loop, i);
                            if (hits) hit.play();
                        }
                        if (currentNote[0] < frame && frame < currentNote[4]) {
                            signal[lane] = 2;
                            judge(currentNote[2] + 2, lane, loop, 0);
                        }
                        if (frame == currentNote[4]) {
                            judge(currentNote[2] + 3, lane, loop, 0);
                        }
                        if ( !cheat) cheat = true;
                    }
                }
            }
        }
    }

    //判定関数
    void judge(double currentNote, int lane, int id, int lag) {
        switch ((int) currentNote) {
        case 0://通常
            if (Math.abs(lag) > criteriaF) {
                hantei("Lost", 3, false, 2, 0, id, lane, lag);
            } else if (Math.abs(lag) > criteriaG) {
                hantei("Fair", 2, true, 2, scores[2], id, lane, lag);
            } else if (Math.abs(lag) > criteriaP) {
                hantei("Good", 1, true, 2, scores[1], id, lane, lag);
            } else {
                hantei("Perfect", 0, true, 2, scores[0], id, lane, lag);
            }
            break;
        case 1://大音
            if (Math.abs(lag) > criteriaF) {
                hantei("Lost", 3, false, 2, 0, id, lane, lag);
            } else if (Math.abs(lag) > criteriaG) {
                hantei("Fair", 2, true, 2, scores[2] * 2, id, lane, lag);
            } else if (Math.abs(lag) > criteriaP) {
                hantei("Good", 1, true, 2, scores[1] * 2, id, lane, lag);
            } else {
                hantei("Perfect", 0, true, 2, scores[0] * 2, id, lane, lag);
            }
            break;
        case 2://長押し
            if (Math.abs(lag) > criteriaF) {
                hantei("Lost", 3, false, 2, 0, id, lane, lag);
            } else if (Math.abs(lag) > criteriaG) {
                hantei("Fair", 2, true, 1, scores[2], id, lane, lag);
            } else if (Math.abs(lag) > criteriaP) {
                hantei("Good", 1, true, 1, scores[1], id, lane, lag);
            } else {
                hantei("Perfect", 0, true, 1, scores[0], id, lane, lag);
            }
            break;
        case 3://スライド
            if (Math.abs(lag) > criteriaF) {
                hantei("Lost", 3, false, 2, 0, id, lane, lag);
            } else {
                hantei("Perfect", 0, true, 2, scores[0], id, lane, lag);
            }
            break;
        case 4:
            double[] local = notemap.get(id);
            if ((frame - local[0]) % 7 == 0 && frame > local[0])
                hantei("Perfect", 0, true, 1, scores[0] / 10.0, id, lane, 255);
            break;
        case 5:
            switch (lag) {
            case 0:
                hantei("Perfect", 0, true, 2, scores[0], id, lane, 0);
                break;
            case 1:
                hantei("Lost", 3, false, 2, 0, id, lane, 10000);
            }
            break;
        }
    }

    void hantei(String h, int b, boolean com, int l3, double p, int id, int lane, int lag) {
        layer1.setTextAlign(TextAlignment.CENTER);
        double[] local = notemap.get(id);
        hantei = h;
        breakdown[b]++;
        if (com) {
            combo++;
            if (combo > maxCombo) maxCombo = combo;
        } else combo = 0;
        score += p;
        local[3] = l3;
        switch (h) {
        case "Lost":
            rect(layer1, lane * (width / 4), 0, width / 4, height, 255, 0, 0, 0.8);
            break;
        case "Fair":
            rect(layer1, lane * (width / 4), 0, width / 4, height, 30, 30, 255, 0.8);
            break;
        case "Good":
            rect(layer1, lane * (width / 4), 0, width / 4, height, 0, 200, 0, 0.8);
            break;
        case "Perfect":
            if (lag != 256) rect(layer1, lane * (width / 4), 0, width / 4, height, 255, 255, 0, 0.8);
            break;
        }
        System.out.println(h + "    " + id + " " + lag + " " + frame);
        layer1.setFont(Font.font("Times New Roman", FontWeight.BOLD, 48));
        layer1.setLineWidth(2);
        layer1.setStroke(Color.color(0, 0, 0));
        layer1.setFill(Color.rgb(255, 255, 255));
        layer1.fillText(hantei, lane * (width / 4) + (width / 8), height - 300);
        layer1.strokeText(hantei, lane * (width / 4) + (width / 8), height - 300);
        if ( !h.equals("Lost")) {
            //drawEffect("hit", lane);
        }
    }

    //譜面作成関数
    void makeArray(int[][] read, int dif) {
        double changedPosition = 0, changedTick = 0;
        note = new double[read.length][7];//{出てくるTick数(0),出てくるレーン数(1),ノートの種類(2),判定済みか否か(3),各種引数(4),スピード(5),難易度(6)}
        notemap.clear();
        offset += 60;
        offset += grobalOffset;
        int defbpm = 0, defoff = 0, defsp = 0, defmea = 0, defqt = 0;
        for (int h = 0; h < read.length; h++) {
            for (int i = 0; i < 7; i++) {
                switch (i) {
                case 0://第一インデックス、出てくるtick数を保持
                    note[h][i] = (int) (((((read[h][0] - 1) * measure * quantize + read[h][1])
                            - ((read[(int) changedPosition][0] - 1) * measure * quantize
                                    + read[(int) changedPosition][1]))
                            * (3600.0 / ((double) bpm * (double) quantize))) + offset) + changedTick;
                    break;
                case 1://第二インデックス、出てくるレーンを保持
                    note[h][i] = read[h][i + 1] - 1;
                    break;
                case 2://第三インデックス、ノートの種類を保持、指定がない場合は通常ノートになる
                    try {
                        note[h][i] = read[h][i + 1];//指定がある場合は横流しする
                        switch (read[h][i + 1]) {//横流しした上での特殊処理
                        case 2://ノートの種類が長押し(2)
                            note[h][4] = (int) (((((read[h][0] - 1) * measure * quantize + read[h][1])
                                    + read[h][4]) * (3600.0 / ((double) bpm * (double) quantize)))
                                    + offset);//第五インデックスに終点のtickを格納
                            break;
                        case 4://BPM変更コマンドノート
                            bpm = read[h][4];//BPM変更
                            changedPosition = h;
                            changedTick = note[h][0] - offset;
                            if (read[h][0] == 1 && read[h][1] == 0) {
                                defbpm = bpm;
                                bpmbuf = bpm;
                            }
                            break;
                        case 5://delayコマンドノート
                            offset += read[h][4];//時間軸引き延ばし
                            if (read[h][0] == 1 && read[h][1] == 0) defoff = offset;
                            break;
                        case 6://speed変更コマンドノート
                            speed = (int) (read[h][4] * masterSpeed);
                            if (read[h][0] == 1 && read[h][1] == 0) defsp = (int) (speed * masterSpeed);
                            break;
                        case 7://measure変更コマンドノート
                            measure = read[h][4];
                            if (read[h][0] == 1 && read[h][1] == 0) defmea = measure;
                            break;
                        case 8://quantize変更コマンドノート(地味にこれ結構重宝すると思ふ)
                            quantize = read[h][4];
                            if (read[h][0] == 1 && read[h][1] == 0) defqt = quantize;
                            break;
                        case 9:
                            currentdif = read[h][4];
                            changedPosition = 0;
                            changedTick = 0;
                            bpm = defbpm;
                            speed = defsp;
                            offset = defoff;
                            measure = defmea;
                            quantize = defqt;
                        }
                    } catch (Exception e) {
                        note[h][i] = 0;//指定がない場合は通常(0)に
                    }
                    break;
                case 3:
                    note[h][i] = 0;//判定済みか否か(第四インデックス)は初期値で必ず0
                    break;
                case 4:
                    try {
                        if (note[h][2] != 2) note[h][i] = read[h][4];//第五インデックス、特殊処理のある長押し以外は横流し
                    } catch (Exception e) {}
                    break;
                case 5:
                    note[h][i] = speed;//第六インデックス、スピード
                    break;
                case 6:
                    note[h][i] = currentdif;//第七インデックス、難易度
                    break;
                }
                if (note[h][6] == dif) {
                    notemap.put((int) (note[h][0] + (read[h][2]) * 100000 + (currentdif * 1000000)), note[h]);
                    //得点計算
                    switch ((int) note[h][2]) {
                    case 1:
                        notes += 10;
                    case 0:
                    case 3:
                        notes += 10;
                        break;
                    case 2:
                        notes += 10;
                        notes += ((note[h][4] - note[h][0] - 1) / 7);
                        notes += 10;
                        break;
                    }
                }
            }
        }
    }

    //ゲームスタート関数
    void gameStart(String name, int dif) {
        try {
            mp.stop();
        } catch (NullPointerException e) {}
        media = new Media(new File(dir + "/" + name + ".mp3").toURI().toString());
        mp = new MediaPlayer(media);
        System.out.printf(
                "=============================================\r\nGame Start  song:%s  difficulty:%d\r\n=============================================\r\n",
                name, dif);
        //{出てくる拍数,出てくるレーン数,ノートの種類}をまとめた二次元構成
        bpm = 1;//曲のBPM
        offset = 1;//一拍目のTick数
        quantize = 1;//一拍をいくつに分割するか
        measure = 1;//何拍で1小節か(≒拍子)
        speed = 1;
        score = 0.1;
        hantei = "null";
        combo = 0;
        frame = 0;
        notes = 0;
        if ( !auto) cheat = false;
        Arrays.fill(breakdown, 0);
        Arrays.fill(judging, 0);
        menuMode = "game";
        makeArray(Fileimport.FileImport(dir + "/" + name + ".jra"), dif);
        scores = new double[] {(((double) limits[dif - 1] / (double) notes) * 10.0), ((double) limits[dif - 1]
                / (double) notes
                * 7.5), (((double) limits[dif - 1] / (double) notes) * 4.0)};
        if (notes == 0) {
            System.out.printf("楽曲 \"%s\"には指定された難易度(%s)が存在しないようです。残念！\r\n", name, difficulties[dif - 1]);
        }
        currentdif = dif;
        maxCombo = 0;
    }

    void rank() {
        if ((double) score / (double) limits[currentdif - 1] > 0.98) {
            clearStatus = "Success";
            clearRank = "SSS";
            rankColor = Color.rgb(255, 255, 50);
        } else if ((double) score / (double) limits[currentdif - 1] > 0.95) {
            clearStatus = "Success";
            clearRank = "SS";
            rankColor = Color.rgb(220, 220, 50);
        } else if ((double) score / (double) limits[currentdif - 1] > 0.9) {
            clearStatus = "Success";
            clearRank = "S+";
            rankColor = Color.rgb(220, 220, 0);
        } else if ((double) score / (double) limits[currentdif - 1] > 0.85) {
            clearStatus = "Success";
            clearRank = "S";
            rankColor = Color.rgb(200, 200, 0);
        } else if ((double) score / (double) limits[currentdif - 1] > 0.75) {
            clearStatus = "Success";
            clearRank = "A+";
            rankColor = Color.rgb(220, 30, 30);
        } else if ((double) score / (double) limits[currentdif - 1] > 0.65) {
            clearStatus = "Success";
            clearRank = "A";
            rankColor = Color.rgb(220, 0, 0);
        } else if ((double) score / (double) limits[currentdif - 1] > 0.5) {
            clearStatus = "Success";
            clearRank = "B";
            rankColor = Color.rgb(30, 220, 30);
        } else if ((double) score / (double) limits[currentdif - 1] > 0.4) {
            clearStatus = "Failure";
            clearRank = "C";
            rankColor = Color.rgb(0, 0, 220);
        } else {
            clearStatus = "Failure";
            clearRank = "D";
            rankColor = Color.rgb(100, 100, 100);
        }
    }

    void setup(String cond) {
        switch (cond) {
        case "start":
            try {
                mp.stop();
            } catch (Exception e) {}
            media = new Media(new File(dir + "/Title.mp3").toURI().toString());
            mp = new MediaPlayer(media);
            mp.setCycleCount(MediaPlayer.INDEFINITE);
            mp.play();
            break;
        case "menu":
            SFrame = 0;
            AFrame = 0;
            frame = -20;
            select = false;
            try {
                mp.stop();
            } catch (Exception e) {}
            media = new Media(new File(dir + "/" + songNames[currentSong] + ".mp3").toURI().toString());
            mp = new MediaPlayer(media);
            mp.setVolume(0.5);
            mp.setStartTime(new Duration(pretimes[currentSong]));
            mp.setCycleCount(MediaPlayer.INDEFINITE);
            mp.play();
            break;
        case "result":
            skipped = false;
            frame = 0;
            try {
                mp.stop();
            } catch (Exception e) {}
            rank();
            if (clearStatus.equals("Success")) media = new Media(new File(dir + "/RS.mp3").toURI().toString());
            else media = new Media(new File(dir + "/RF.mp3").toURI().toString());
            mp = new MediaPlayer(media);
            mp.setVolume(0.8);
            mp.setCycleCount(MediaPlayer.INDEFINITE);
            mp.play();
            break;
        case "mending":
            try {
                mp.stop();
            } catch (Exception e) {}
            media = new Media(new File(dir + "/Title.mp3").toURI().toString());
            mp = new MediaPlayer(media);
            mp.setCycleCount(MediaPlayer.INDEFINITE);
            mp.play();
            break;
        }
        menuMode = cond;
    }

    // キーが押された時の処理
    void onKeyPressed(KeyEvent event) {
        switch (event.getText()) {
        case "d":
            status[0] = 2;
            break;
        case "f":
            status[1] = 2;
            break;
        case "j":
            status[2] = 2;
            break;
        case "k":
            status[3] = 2;
            break;
        case "e":
            if (DSWC && DM) {
                gameStart(songNames[currentSong], 1);
            }
            break;
        case "1":
            if (DM) gameStart(songNames[currentSong], 1);
            break;
        case "n":
            if (DSWC && DM) {
                gameStart(songNames[currentSong], 2);
            }
            break;
        case "2":
            if (DM) gameStart(songNames[currentSong], 2);
            break;
        case "h":
            if (DSWC && DM) {
                gameStart(songNames[currentSong], 3);
            }
            break;
        case "3":
            if (DM) gameStart(songNames[currentSong], 3);
            break;
        case "l":
            if (DSWC && DM) {
                gameStart(songNames[currentSong], 4);
            }
            break;
        case "4":
            if (DM) gameStart(songNames[currentSong], 4);
            break;
        case "a":
            if (DM) auto = !auto;
            if (auto) cheat = true;
            break;
        case "p":
            if (DM) menuMode = "paused";
            try {
                if (DM) mp.stop();
            } catch (Exception e) {}
            break;
        case "r":
            setup("result");
            break;
        case "m":
            if ( !menuMode.equals("mending")) setup("mending");
            else setup("start");
            break;
        case "o":
            rokko = !rokko;

        }
        switch (event.getCode()) {
        case ESCAPE:
            Platform.exit();
            System.exit(0);
            break;
        case DOWN:
            if (DM) {
                frame -= 60;
                elapse -= 1.0;
                mp.seek(Duration.millis(elapse * 1000 - 1000));
            }
            break;
        case UP:
            if (DM) {
                frame += 60;
                elapse += 1.0;
                mp.seek(Duration.millis(elapse * 1000 - 1000));
            }
            break;
        case LEFT:
            if (DM) {
                frame -= 900;
                elapse -= 15.0;
                mp.seek(Duration.millis(elapse * 1000 - 1000));
            }
            break;

        case RIGHT:
            if (DM) {
                frame += 900;
                elapse += 15.0;
                mp.seek(Duration.millis(elapse * 1000 - 1000));
            }
            break;
        default:
            break;
        }
    }

    void makeInputArray() {
        //タッチ判定
        //0:押されていない 1:小音を押した瞬間 2:小音を押している途中 3:小音を離した瞬間 +3:大音
        for (int i = 0; i < 4; i++) {
            if (status[i] == 1) {
                switch (signal[i]) {
                case 0:
                    signal[i] = 1;
                    if (hits) hit.play();
                    break;
                case 1:
                case 3:
                    signal[i] = 2;
                    break;
                case 4:
                case 6:
                    signal[i] = 5;
                    break;
                }
            } else if (status[i] == 2) {
                switch (signal[i]) {
                case 0:
                    signal[i] = 4;
                    if (hits) hit.play();
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                    signal[i] = 5;
                    break;
                }
            } else {
                switch (signal[i]) {
                case 1:
                case 2:
                case 4:
                case 5:
                    signal[i] = 3;
                    break;
                case 3:
                    signal[i] = 0;
                    break;
                }
            }
        }
    }

    void onKeyReleased(KeyEvent event) {
        switch (event.getText()) {
        case "d":
            status[0] = 0;
            break;
        case "f":
            status[1] = 0;
            break;
        case "j":
            status[2] = 0;
            break;
        case "k":
            status[3] = 0;
            break;
        }
    }

}
//  (´･ω･`)
//＿(つ/￣￣￣/
//  ＼/      /
//    ￣￣￣
//  (´･ω･`)
//＿(  つ  ミ    ﾊﾞﾀﾝｯ
//  ＼￣￣￣＼ミ
//    ￣￣￣￣
//  (´･ω･`)
//＿(      )
//  ＼￣￣￣＼
//    ￣￣￣￣
