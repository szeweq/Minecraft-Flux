package szewek.mcflux.gui;

public class GuiRect {
	public final int x, y, width, height, x2, y2;

	public GuiRect(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		width = w;
		height = h;
		x2 = x + width;
		y2 = y + height;
	}

	public boolean contains(int px, int py) {
		return !(px < x || py < y) && !(x2 < px || y2 < py);
	}
}
