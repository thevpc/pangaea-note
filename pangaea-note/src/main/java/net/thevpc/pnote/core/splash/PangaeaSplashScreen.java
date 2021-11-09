/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.splash;


/**
 *
 * @author thevpc
 */
public class PangaeaSplashScreen {

    private static PangaeaSplashScreen curr;

    public static PangaeaSplashScreen get() {
        if (curr == null) {
            curr = new PangaeaSplashScreen();
        }
        return curr;
    }

//    private JSplashScreen ss;
    private int _progressIndex;
    private int _maxProgress = 34;

    public PangaeaSplashScreen() {
//        ss = new JSplashScreen(new ImageIcon(PangaeaSplashScreen.class.getResource("/net/thevpc/pnote/splash-screen.png")), null);
//        ss.setProgressLineColor(new Color(11, 31, 30));
//        ss.setForegroundColor(new Color(11, 31, 30));
//        ss.setRainbowColor(Color.WHITE);
//        ss.setRainbowColor2(new Color(7, 64, 61));
//        ss.animateText();
    }

    public void tic() {
        if (_progressIndex < _maxProgress) {
            _progressIndex++;
        }
//        ss.setProgress(_progressIndex / 1f / _maxProgress);
    }

    public void closeSplash() {
//        ss.closeSplash();
    }

}
