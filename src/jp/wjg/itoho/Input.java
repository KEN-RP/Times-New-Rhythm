package jp.wjg.itoho;

class Input {
    static int status1 = 0;
    static int status2 = 0;
    static int status3 = 0;
    static int status4 = 0;

    public static int PanelInput = 0;

    //判定
    public static void decision() {
        int normal = -300;//-3
        int attack = 400;//10
        switch (PanelInput) {
        case 49:
            if (status1 <= 0) {
                status1 = normal;
            }
            break;
        case 50:
            if (status2 <= 0) {
                status2 = normal;
            }
            break;
        case 51:
            if (status3 <= 0) {
                status3 = normal;
            }
            break;
        case 52:
            if (status4 <= 0) {
                status4 = normal;
            }
            break;
        case 53:
            status1 = attack;
            break;
        case 54:
            status2 = attack;
            break;
        case 55:
            status3 = attack;
            break;
        case 56:
            status4 = attack;
            break;
        }
        PanelInput = 0;

        if (status1 > 0) {
            status1--;
            Main3D.status[0] = 2;
            Main2D.status[0] = 2;
        } else if (status1 < 0) {
            status1++;
            Main3D.status[0] = 1;
            Main2D.status[0] = 1;
        } else {
            Main3D.status[0] = 0;
            Main2D.status[0] = 0;
        }

        if (status2 > 0) {
            status2--;
            Main3D.status[1] = 2;
            Main2D.status[1] = 2;
        } else if (status2 < 0) {
            status2++;
            Main3D.status[1] = 1;
            Main2D.status[1] = 1;
        } else {
            Main3D.status[1] = 0;
            Main2D.status[1] = 0;
        }

        if (status3 > 0) {
            status3--;
            Main3D.status[2] = 2;
            Main2D.status[2] = 2;
        } else if (status3 < 0) {
            status3++;
            Main3D.status[2] = 1;
            Main2D.status[2] = 1;
        } else {
            Main3D.status[2] = 0;
            Main2D.status[2] = 0;
        }

        if (status4 > 0) {
            status4--;
            Main3D.status[3] = 2;
            Main2D.status[3] = 2;
        } else if (status4 < 0) {
            status4++;
            Main3D.status[3] = 1;
            Main2D.status[3] = 1;
        } else {
            Main3D.status[3] = 0;
            Main2D.status[3] = 0;
        }
        //System.out.printf("%d,%d,%d,%d\r\n", status1, status2, status3, status4);
    }
}
