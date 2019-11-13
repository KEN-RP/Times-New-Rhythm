package jp.wjg.itoho;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;

public class Fileimport {
    public static int[][] FileImport(String adress) {
        LineNumberReader fin = null;
        try {
            fin = new LineNumberReader(new FileReader(adress));
        } catch (FileNotFoundException e) {}
        @SuppressWarnings("unused")
        String aLine;
        int lines = 0;

        try {
            while (null != (aLine = fin.readLine())) {
                ;
            }
        } catch (IOException e) {}
        lines = fin.getLineNumber();
        try {
            fin.close();
        } catch (IOException e) {}

        /* adress = 読み込みたいテキストファイルがある場所。
         */

        //配列[ノード番号][ノードの要素]の配列を準備
        int Index[][] = new int[lines][];

        //ファイルの読み込み
        try {
            //ファイルのアドレスよりbrにファイルの情報を格納
            FileReader filereader = new FileReader(adress);
            BufferedReader br = new BufferedReader(filereader);

            //一行ずつ読み込んで
            String str = br.readLine();
            int count = 0;

            while (str != null) {
                //strlist.add(str.split(","));
                //一行の内容を','で分割してそれぞれを[count=ノード番号]の２次元目の配列の要素として格納
                try {
                    Index[count] = parseInts(str.split("[,:]"), count);
                } catch (Exception e) {
                    Index[count] = new int[] {1, 0, 5, 5, 0};
                }
                //次の行を読み込み
                str = br.readLine();
                count++;
            }

            br.close();

        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }


        return Index;

    }


    public static Map<String, String> PropertyImport(String adress) {
        Map<String, String> returnmap = new HashMap<>();
        LineNumberReader fin = null;
        try {
            fin = new LineNumberReader(new FileReader(adress));
        } catch (FileNotFoundException e) {}
        @SuppressWarnings("unused")
        String aLine;
        int lines = 0;

        try {
            while (null != (aLine = fin.readLine())) {
                ;
            }
        } catch (IOException e) {}
        lines = fin.getLineNumber();
        try {
            fin.close();
        } catch (IOException e) {}

        /* adress = 読み込みたいテキストファイルがある場所。
         */

        //配列[ノード番号][ノードの要素]の配列を準備
        String Index[][] = new String[lines][];

        //ファイルの読み込み
        try {
            //ファイルのアドレスよりbrにファイルの情報を格納
            FileReader filereader = new FileReader(adress);
            BufferedReader br = new BufferedReader(filereader);

            //一行ずつ読み込んで
            String str = br.readLine();
            int count = 0;

            while (str != null) {
                str = str.replaceAll(" ", "");
                //strlist.add(str.split(","));
                //一行の内容を','で分割してそれぞれを[count=ノード番号]の２次元目の配列の要素として格納
                try {
                    Index[count] = str.split("=");
                    returnmap.put(Index[count][0], Index[count][1]);
                } catch (Exception e) {}
                //次の行を読み込み
                str = br.readLine();
                count++;
            }

            br.close();

        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }
        return returnmap;

    }

    public static int[] parseInts(String[] s, int line) {

        /* s[] = intに変換したいストリングを収めた配列
         */

        int[] x = new int[s.length];
        for (int i = 0; i < s.length; i++) {
            if ( !s[i].substring(0, 1).equals("#")) {
                s[i] = s[i].replaceAll(" ", "");
                x[i] = Integer.parseInt(s[i]);
            } else {
                if (s[i].substring(0, 1).equals("#") || s[i].substring(0, 1).equals("/")) {
                    switch (s[i].substring(1)) {
                    case "BPMChange":
                    case "BPMchange":
                    case "BPMCHANGE":
                    case "BPM":
                    case "bpm":
                        if (s.length < 3) {
                            x = new int[] {1, 0, 5, 4, Integer.parseInt(s[1])};
                        } else {
                            x = new int[] {Integer.parseInt(s[1]), Integer.parseInt(s[2]), 5, 4, Integer
                                    .parseInt(s[3])};
                        }
                        x = new int[] {Integer.parseInt(s[1]), Integer.parseInt(s[2]), 5, 4, Integer.parseInt(s[3])};
                        break;

                    case "delay":
                    case "Delay":
                    case "DELAY":
                    case "wait":
                    case "Wait":
                    case "WAIT":
                        if (s.length < 3) {
                            x = new int[] {1, 0, 5, 5, Integer.parseInt(s[1])};
                        } else {
                            x = new int[] {Integer.parseInt(s[1]), Integer.parseInt(s[2]), 5, 5, Integer
                                    .parseInt(s[3])};
                        }
                        break;
                    case "speed":
                    case "Speed":
                    case "SPEED":
                    case "scroll":
                    case "Scroll":
                    case "SCROLL":
                        if (s.length < 3) {
                            x = new int[] {1, 0, 5, 6, Integer.parseInt(s[1])};
                        } else {
                            x = new int[] {Integer.parseInt(s[1]), Integer.parseInt(s[2]), 5, 6, Integer
                                    .parseInt(s[3])};
                        }
                        break;
                    case "measure":
                    case "Measure":
                    case "MEASURE":
                    case "mea":
                    case "MEA":
                    case "m":
                    case "M":
                        if (s.length < 3) {
                            x = new int[] {1, 0, 5, 7, Integer.parseInt(s[1])};
                        } else {
                            x = new int[] {Integer.parseInt(s[1]), Integer.parseInt(s[2]), 5, 7, Integer
                                    .parseInt(s[3])};
                        }
                        break;
                    case "quantize":
                    case "Quantize":
                    case "QUANTIZE":
                    case "qt":
                    case "QT":
                    case "q":
                    case "Q":
                        if (s.length < 3) {
                            x = new int[] {1, 0, 5, 8, Integer.parseInt(s[1])};
                        } else {
                            x = new int[] {Integer.parseInt(s[1]), Integer.parseInt(s[2]), 5, 8, Integer
                                    .parseInt(s[3])};
                        }
                        break;
                    case "easy":
                    case "Easy":
                    case "EASY":
                    case "e":
                    case "E":
                        x = new int[] {1, 0, 5, 9, 1};
                        if (s.length == 1) {
                            Main3D.difbuf[0] = 0;
                            Main2D.difbuf[0] = 0;
                        } else {
                            Main3D.difbuf[0] = Integer.parseInt(s[1]);
                            Main2D.difbuf[0] = Integer.parseInt(s[1]);
                        }
                        break;
                    case "normal":
                    case "Normal":
                    case "NORMAL":
                    case "n":
                    case "N":
                        x = new int[] {1, 0, 5, 9, 2};
                        if (s.length == 1) {
                            Main3D.difbuf[1] = 0;
                            Main2D.difbuf[1] = 0;
                        } else {
                            Main3D.difbuf[1] = Integer.parseInt(s[1]);
                            Main2D.difbuf[1] = Integer.parseInt(s[1]);
                        }
                        break;
                    case "hard":
                    case "Hard":
                    case "HARD":
                    case "h":
                    case "H":
                        x = new int[] {1, 0, 5, 9, 3};
                        if (s.length == 1) {
                            Main3D.difbuf[2] = 0;
                            Main2D.difbuf[2] = 0;
                        } else {
                            Main3D.difbuf[2] = Integer.parseInt(s[1]);
                            Main2D.difbuf[2] = Integer.parseInt(s[1]);
                        }
                        break;
                    case "lunatic":
                    case "Lunatic":
                    case "LUNATIC":
                    case "luna":
                    case "Luna":
                    case "LUNA":
                    case "l":
                    case "L":
                        x = new int[] {1, 0, 5, 9, 4};
                        if (s.length == 1) {
                            Main3D.difbuf[3] = 0;
                            Main2D.difbuf[3] = 0;
                        } else {
                            Main3D.difbuf[3] = Integer.parseInt(s[1]);
                            Main2D.difbuf[3] = Integer.parseInt(s[1]);
                        }
                        break;
                    case "name":
                    case "Name":
                    case "songname":
                    case "NAME":
                    case "SongName":
                    case "songName":
                    case "SONGNAME":
                        Main3D.songname = s[1];
                        Main2D.songname = s[1];
                        x = new int[] {1, 0, 5, 10, 0};
                        break;
                    case "demo":
                    case "demoplay":
                    case "demostart":
                    case "preview":
                    case "DEMO":
                    case "DEMOSTART":
                    case "DEMOPLAY":
                    case "PREVIEW":
                    case "Demo":
                    case "Demoplay":
                    case "Demostart":
                    case "Preview":
                        Main3D.pretime = Integer.parseInt(s[1]);
                        Main2D.pretime = Integer.parseInt(s[1]);
                        x = new int[] {1, 0, 5, 10, 0};
                        break;
                    case "composer":
                    case "compose":
                    case "composed":
                    case "COMPOSER":
                    case "COMPOSE":
                    case "Composer":
                    case "Compose":
                    case "music":
                    case "Music":
                    case "MUSIC":
                        Main3D.composer = s[1];
                        Main2D.composer = s[1];
                        x = new int[] {1, 0, 5, 10, 0};
                    default:
                        System.out.println("コマンドの書式に誤りがあります. line " + line);
                        break;
                    }
                } else if (s[i].substring(0, 1).equals(".")) {
                    x = new int[] {1, 0, 5, 5, 0};
                }
                break;
            }
        }
        return x;
    }
}
