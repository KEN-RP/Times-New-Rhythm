package jp.wjg.itoho;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class Serial {
    public Serial(){//コンストラクタ
        super();//スーパークラスのコンストラクタ呼び出し(=デフォルトコンストラクタ)
    }//デフォルトコンストラクタなので書かなくてもよいが明示的に

    void connect(String portName) throws Exception {//例外発生時、呼び出し元メソッドに丸投げ
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        //portIdentifierをシリアルポートのオブジェクトとして定義
        if (portIdentifier.isCurrentlyOwned()) {//ポートが使用されていたら
            System.out.println("Error: Port is currently in use");//エラーメッセージ
        } else {//ポートが使われていない(自由に使える)状態であれば
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
            //2000ms待機してここでポートオープン
            if (commPort instanceof SerialPort) {//状態が正常なら(よく分からん)
                SerialPort serialPort = (SerialPort) commPort;//commPortをSerialPort型にキャストした物をserialPortと定義
                serialPort.setSerialPortParams(2000000, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);
                //38400bps,データビット8(1文字あたり8bit),ストップビット1(終止の合図が1bit),パリティチェックなし
                InputStream inst = serialPort.getInputStream();
                //inにInputStreamを代入
                OutputStream outst = serialPort.getOutputStream();
                //outにOutputStreamを代入
                (new Thread(new SerialReader(inst))).start();
                (new Thread(new SerialWriter(outst))).start();
                //SerialReaderとSerialWriterをマルチスレッドでスタート
            } else {
                System.out.println("Error: Only serial ports are handled by this example.");
                //エラーメッセージ
            }
        }
    }

    /** */
    public static class SerialReader implements Runnable {//マルチスレッドインターフェースであるRunnableをimplementsする
        InputStream in;

        public SerialReader(InputStream input){
            this.in = input;
        }//引数1つのコンストラクタ、このクラスのフィールドinにもらった引数inputを収める

        public void run() {
            //byte[] buffer = new byte[2];//●
            byte[] buffer = new byte[1];//▼
            int len = -1;
            try {
                while ((len = this.in.read(buffer))> -1) {//読んだ分はbufferに格納される
                    //lenにはreadの戻り値である読み込まれたバッファーの合計数が代入される
                    //readの戻り値はストリームの終わりで-1になるので、終わりまで繰り返すという意味
                    if (len>0) {//ストリームが途切れていないならば
                        Input.PanelInput = buffer[0];
                        Input.decision();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();//コンソールにエラー表示
            }
        }

        public static String toHex(byte[] digest) {
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%1$02X", b));
            }
            return sb.toString();
        }
    }

    /** */
    public static class SerialWriter implements Runnable {
        OutputStream out;

        public SerialWriter(OutputStream output){
            this.out = output;
        }

        public void run() {
            try {
                int c = 0;
                while ((c = System.in.read())> -1) {
                    this.out.write(c);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void main() throws Exception {
        (new Serial()).connect("COM3");//COMの設定はここで、Serialの引数として渡す
        System.out.println("mode:panel");
    }
}
