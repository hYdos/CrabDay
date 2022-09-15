package me.hydos.crabday.window;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

public record Window(WinDef.HWND hwnd, WinDef.RECT rawRect, String title) {
    public void moveToFront() {
        User32.INSTANCE.SetForegroundWindow(hwnd);
    }

    public int getLeft() {
        return (int) (rawRect.left * 0.572);
    }

    public int getTop() {
        return (int) (rawRect.top * 0.572);
    }

    public int getWidth() {
        return (int) ((rawRect.right - rawRect.left) / 1.74);
    }

    public int getHeight() {
        return (int) ((rawRect.bottom - rawRect.top) / 1.68);
    }

    public static Window getWindowInfo(String titleName) {
        var hWnd = User32.INSTANCE.FindWindow(null, titleName);
        var r = new WinDef.RECT();
        var buf = new char[1024];
        User32.INSTANCE.GetWindowRect(hWnd, r);
        User32.INSTANCE.GetWindowText(hWnd, buf, buf.length);
        return new Window(hWnd, r, Native.toString(buf));
    }
}