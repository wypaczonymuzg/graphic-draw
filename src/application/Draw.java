package application;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritablePixelFormat;

public class Draw {

	public static void printPoint(Point p) {
		System.out.print("point : " + p.getX() + "\t " + p.getY() + "\n");
	}

	private static enum Outcodes {
		Inside(0), Left(1), Right(2), Bottom(4), Top(8);
		private final int value;

		private Outcodes(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	static int ComputeOutcode(Point p, Point rectP1, Point rectP2) {
		Outcodes outcode = Outcodes.Inside;
		int out1=0;
		
		if (p.getX() > rectP2.getX())
			out1 = (out1 | Outcodes.Right.value);
		else if (p.getX() < rectP1.getX())
			out1 =(out1 | Outcodes.Left.value);
		if (p.getY() > rectP2.getY())
			out1 = (out1 | Outcodes.Top.value);
		else if (p.getY() < rectP1.getY())
			out1 = (out1 | Outcodes.Bottom.value);
		
		System.out.println("out1  = "+out1);
		
		return out1;
	}

	static BufferedImage CohenSutherlandLineClipAndDraw(BufferedImage fromFXImage,
			Point p1, Point p2, Point rectP1, Point rectP2, int line) {
		// compute outcodes for P0, P1, and whatever point lies outside the clip
		// rectangle
		//Outcodes outcode0 = ComputeOutcode(p1, rectP1, rectP2);
		
		//Outcodes outcode1 = ComputeOutcode(p2, rectP1, rectP2);
		int outcode0 = ComputeOutcode(p1, rectP1, rectP2);
		int outcode1 = ComputeOutcode(p2, rectP1, rectP2);
		System.out.println("outcode values after computing\notcode0.value = "+outcode0+"\noutcode1.value = "+outcode1);	
		boolean accept = false;

		while (true) {
			if ((outcode0 | outcode1) == 0) { // Bitwise OR is 0.
															// Trivially accept
															// and get out of
															// loop
				accept = true;
				System.out.println("accepted");
				break;
			} else if ((outcode0 & outcode1) != 0) { // Bitwise AND
																	// is not 0.
																	// Trivially
																	// reject
																	// and get
																	// out of
				System.out.println("rejected\notcode0.value = "+outcode0+"\noutcode1.value = "+outcode1);													// loop
				break;
			} else {
				// failed both tests, so calculate the line segment to clip
				// from an outside point to an intersection with clip edge
				double x, y;

				// At least one endpoint is outside the clip rectangle; pick it.
				int outcodeOut;
				if (outcode0!= 0)
					outcodeOut = outcode0;
				else
					outcodeOut = outcode1;

				// Now find the intersection point;
				// use formulas y = y0 + slope * (x - x0), x = x0 + (1 / slope)
				// * (y - y0)
				if ((outcodeOut & Outcodes.Top.value) != 0) { // point is
																	// above the
																	// clip
																	// rectangle
					x = p1.getX() + (p2.getX() - p1.getX())
							* (rectP2.getY() - p1.getY())
							/ (p2.getY() - p1.getY());
					y = rectP2.getY();
				} else if ((outcodeOut & Outcodes.Bottom.value) != 0) { // point
																				// is
																				// below
																				// the
																				// clip
					// rectangle
					x = p1.getX() + (p2.getX() - p1.getX())
							* (rectP1.getY() - p1.getY())
							/ (p2.getY() - p1.getY());
					y = rectP1.getY();
				} else if ((outcodeOut & Outcodes.Right.value) != 0) { // point
																				// is
																				// to
																				// the
																				// right
																				// of
					// clip rectangle
					y = p1.getY() + (p2.getY() - p1.getY())
							* (rectP2.getX() - p1.getX())
							/ (p2.getX() - p1.getX());
					x = rectP2.getX();
				} else if ((outcodeOut & Outcodes.Left.value) != 0) { // point
																			// is
																			// to
																			// the
																			// left
																			// of
																			// clip
					// rectangle
					y = p1.getY() + (p2.getY() - p1.getY())
							* (rectP1.getX() - p1.getX())
							/ (p2.getX() - p1.getX());
					x = rectP1.getX();
				} else {
					break;
				}
				// Now we move outside point to intersection point to clip
				// and get ready for next pass.
				if (outcodeOut == outcode0) {
					p1.setX(x);
					p1.setY(y);
					outcode0 = ComputeOutcode(p1, rectP1, rectP2);
				} else {
					p2.setX(x);
					p2.setY(y);
					outcode1 = ComputeOutcode(p2, rectP1, rectP2);
				}
			}
		}
		if (accept) {
			// Following functions are left for implementation by user based on
			// their platform (OpenGL/graphics.h etc.)
			return drawLine(fromFXImage, p1, p2, line);

		}
		return null;
	}

	
	
	/*
	
		System.out.println("outcode values after computing\notcode0.value = "+outcode0.value+"\noutcode1.value = "+outcode1.value);	



		boolean accept = false;

		while (true) {
			if ((outcode0.value | outcode1.value) == 0) { // Bitwise OR is 0.
															// Trivially accept
															// and get out of
															// loop
				accept = true;
				System.out.println("accepted");
				break;
			} else if ((outcode0.value & outcode1.value) != 0) { // Bitwise AND
																	// is not 0.
																	// Trivially
																	// reject
																	// and get
																	// out of
				System.out.println("rejected\notcode0.value = "+outcode0.value+"\noutcode1.value = "+outcode1.value);													// loop
				break;
			} else {
				// failed both tests, so calculate the line segment to clip
				// from an outside point to an intersection with clip edge
				double x, y;

				// At least one endpoint is outside the clip rectangle; pick it.
				Outcodes outcodeOut;
				if (outcode0.value != 0)
					outcodeOut = outcode0;
				else
					outcodeOut = outcode1;

				// Now find the intersection point;
				// use formulas y = y0 + slope * (x - x0), x = x0 + (1 / slope)
				// * (y - y0)
				if ((outcodeOut.value & Outcodes.Top.value) != 0) { // point is
																	// above the
																	// clip
																	// rectangle
					x = p1.getX() + (p2.getX() - p1.getX())
							* (rectP1.getY() - p1.getY())
							/ (p2.getY() - p1.getY());
					y = rectP1.getY();
				} else if ((outcodeOut.value & Outcodes.Bottom.value) != 0) { // point
																				// is
																				// below
																				// the
																				// clip
					// rectangle
					x = p1.getX() + (p2.getX() - p1.getX())
							* (rectP2.getY() - p1.getY())
							/ (p2.getY() - p1.getY());
					y = rectP2.getY();
				} else if ((outcodeOut.value & Outcodes.Right.value) != 0) { // point
																				// is
																				// to
																				// the
																				// right
																				// of
					// clip rectangle
					y = p1.getY() + (p2.getY() - p1.getY())
							* (rectP1.getX() - p1.getX())
							/ (p2.getX() - p1.getX());
					x = rectP1.getX();
				} else if ((outcodeOut.value & Outcodes.Left.value) != 0) { // point
																			// is
																			// to
																			// the
																			// left
																			// of
																			// clip
					// rectangle
					y = p1.getY() + (p2.getY() - p1.getY())
							* (rectP2.getX() - p1.getX())
							/ (p2.getX() - p1.getX());
					x = rectP2.getX();
				} else {
					break;
				}
				// Now we move outside point to intersection point to clip
				// and get ready for next pass.
				if (outcodeOut == outcode0) {
					p1.setX(x);
					p1.setY(y);
					outcode0 = ComputeOutcode(p1, rectP1, rectP2);
				} else {
					p2.setX(x);
					p2.setY(y);
					outcode1 = ComputeOutcode(p2, rectP1, rectP2);
				}
			}
		}
		if (accept) {
			// Following functions are left for implementation by user based on
			// their platform (OpenGL/graphics.h etc.)
			return drawLine(fromFXImage, p1, p2, line);

		}
		return null;
	}

	
	
	*/
	
	
	
	public static BufferedImage drawClipLine(BufferedImage fromFXImage,
			Point p1, Point p2, Point rectP1, Point rectP2, int parseInt) {
		
		BufferedImage img = CohenSutherlandLineClipAndDraw(fromFXImage,p1,p2,rectP1,rectP2,parseInt);
		if(img==null){
			img = fromFXImage;
		}
		return img;
		
	}

	public static BufferedImage clear(BufferedImage image) {
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);
		// int curPixel;
		int idx;
		int A, R, G, B;
		int[] srcPixels = new int[imageWidth * imageHeight];
		int[] rtPixels = new int[imageWidth * imageHeight];

		image.getRGB(0, 0, imageWidth, imageHeight, srcPixels, 0, imageWidth);

		BufferedImage dest = new BufferedImage(imageWidth, imageHeight,
				image.getType());

		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				idx = (x) + (y) * imageWidth;
				// curPixel = srcPixels[x + y * imageWidth];
				//
				A = 255;
				R = G = B = 255;

				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
				// curPixel = srcPixels[x + y * imageWidth];

			}
		}
		dest.setRGB(0, 0, imageWidth, imageHeight, rtPixels, 0, imageWidth);
		return dest;
	}

	public static BufferedImage drawCircle(BufferedImage image,
			Point p1/* x0 */, Point p2/* y0 */, int line) {
		int x0 = (int) p1.getX();
		int y0 = (int) p1.getY();
		int xd = (int) (p2.getX() - p1.getX());
		int yd = (int) (p2.getY() - p1.getY());

		int Rad = (int) Math.sqrt(xd * xd + yd * yd);
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);
		int curPixel;
		int idx;
		int A, R, G, B;
		int[] srcPixels = new int[imageWidth * imageHeight];
		int[] rtPixels = new int[imageWidth * imageHeight];

		image.getRGB(0, 0, imageWidth, imageHeight, srcPixels, 0, imageWidth);

		BufferedImage dest = new BufferedImage(imageWidth, imageHeight,
				image.getType());

		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				idx = (x) + (y) * imageWidth;
				curPixel = srcPixels[x + y * imageWidth];

				A = ((curPixel >> 24) & 0x000000FF);
				R = ((curPixel >> 16) & 0x000000FF);
				G = ((curPixel >> 8) & 0x000000FF);
				B = ((curPixel >> 0) & 0x000000FF);

				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
				curPixel = srcPixels[x + y * imageWidth];

			}
		}
		R = G = B = 0;
		A = 255;

		int x = Rad, y = 0;
		int radiusError = 1 - x;
		line++;
		if (p1.getY() - Rad - line / 2 + 1 < 0
				|| p1.getY() + Rad + line / 2 - 1 > imageHeight
				|| p1.getX() - Rad - line / 2 + 1 < 0
				|| p1.getX() + Rad + line / 2 - 1 > imageWidth) {
			System.out.print("circle going out of imageview\n");

			return null;
		}
		while (x >= y) {

			for (int i = 0; i < line / 2; i++) {
				// right pixel
				// DrawPixel(x + x0, y + y0); octant 3
				idx = (x + x0 + i) + (y + y0) * imageWidth;
				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
				// DrawPixel(-x + x0, y + y0);octant 6

				idx = (-x + x0 + i) + (y + y0) * imageWidth;
				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
				// DrawPixel(-x + x0, -y + y0); octant 7

				idx = (-x + x0 + i) + (-y + y0) * imageWidth;
				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
				// DrawPixel(x + x0, -y + y0); octant 2

				idx = (x + x0 + i) + (-y + y0) * imageWidth;
				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
				// left pixel
				// DrawPixel(x + x0, y + y0); octant 3
				idx = (x + x0 - i) + (y + y0) * imageWidth;
				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
				// DrawPixel(-x + x0, y + y0);octant 6

				idx = (-x + x0 - i) + (y + y0) * imageWidth;
				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
				// DrawPixel(-x + x0, -y + y0); octant 7

				idx = (-x + x0 - i) + (-y + y0) * imageWidth;
				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
				// DrawPixel(x + x0, -y + y0); octant 2

				idx = (x + x0 - i) + (-y + y0) * imageWidth;
				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
			}

			for (int i = 0; i < line / 2; i++) {
				// above pixel
				// DrawPixel(y + x0, x + y0); octant 4

				idx = (y + x0) + (x + y0 + i) * imageWidth;
				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);

				// DrawPixel(-y + x0, x + y0); octant 5

				idx = (-y + x0) + (x + y0 + i) * imageWidth;
				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);

				// DrawPixel(-y + x0, -x + y0); octant 8

				idx = (-y + x0) + (-x + y0 + i) * imageWidth;
				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);

				// DrawPixel(y + x0, -x + y0); octant 1

				idx = (y + x0) + (-x + y0 + i) * imageWidth;
				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
				// below pixel
				// DrawPixel(y + x0, x + y0); octant 4

				idx = (y + x0) + (x + y0 - i) * imageWidth;
				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);

				// DrawPixel(-y + x0, x + y0); octant 5

				idx = (-y + x0) + (x + y0 - i) * imageWidth;
				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);

				// DrawPixel(-y + x0, -x + y0); octant 8

				idx = (-y + x0) + (-x + y0 - i) * imageWidth;
				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);

				// DrawPixel(y + x0, -x + y0); octant 1

				idx = (y + x0) + (-x + y0 - i) * imageWidth;
				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
			}
			y++;
			if (radiusError < 0) {
				radiusError += 2 * y + 1;
			} else {
				x--;
				radiusError += 2 * (y - x + 1);
			}
		}

		dest.setRGB(0, 0, imageWidth, imageHeight, rtPixels, 0, imageWidth);
		return dest;
	}

	public static BufferedImage setImg(int imageWidth, int imageHeight, int R,
			int G, int B) {
		int idx;
		int A;
		int[] rtPixels = new int[imageWidth * imageHeight];
		BufferedImage dest = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_INT_ARGB);

		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				idx = (x) + (y) * imageWidth;
				A = 255;
				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);

			}
		}
		dest.setRGB(0, 0, imageWidth, imageHeight, rtPixels, 0, imageWidth);
		return dest;

	}

	public static BufferedImage fill(BufferedImage image, Point p1) {
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);
		int curPixel, fillPixel;
		int idx;
		int A, R, G, B;
		int[] srcPixels = new int[imageWidth * imageHeight];
		int[] rtPixels = new int[imageWidth * imageHeight];

		image.getRGB(0, 0, imageWidth, imageHeight, srcPixels, 0, imageWidth);

		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				idx = (x) + (y) * imageWidth;
				curPixel = srcPixels[x + y * imageWidth];

				A = ((curPixel >> 24) & 0x000000FF);
				R = ((curPixel >> 16) & 0x000000FF);
				G = ((curPixel >> 8) & 0x000000FF);
				B = ((curPixel >> 0) & 0x000000FF);

				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
				curPixel = srcPixels[x + y * imageWidth];

			}
		}

		BufferedImage dest = new BufferedImage(imageWidth, imageHeight,
				image.getType());
		idx = (int) p1.getX() + (int) p1.getY() * imageWidth;
		curPixel = rtPixels[idx];
		A = ((curPixel >> 24) & 0x000000FF);
		R = ((curPixel >> 16) & 0x000000FF);
		G = ((curPixel >> 8) & 0x000000FF);
		B = ((curPixel >> 0) & 0x000000FF);
		// System.out.print("fill pixel" + fillPixel + "\n");
		rtPixels = privateFill(rtPixels, imageHeight, imageWidth, p1, A, R, G,
				B);

		dest.setRGB(0, 0, imageWidth, imageHeight, rtPixels, 0, imageWidth);

		return dest;

	}

	private static int[] privateFill(int[] srcPixels, int imageHeight,
			int imageWidth, Point p1, int sA, int sR, int sG, int sB) {
		int[] rtPixels = new int[imageWidth * imageHeight];
		int idx;
		int A, R, G, B;
		Queue<Point> lista = new LinkedList<Point>();
		int fillPixel = ((sA << 24) | (sR << 16) | (sG << 8) | sB);
		System.out.print("sA : " + sA + "\tsR : " + sR + "\tsG : " + sG
				+ "\tsB : " + sB + "\n");
		System.out.print("fill pixel" + fillPixel + "\n");

		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				idx = (x) + (y) * imageWidth;
				int curPixel = srcPixels[x + y * imageWidth];

				A = ((curPixel >> 24) & 0x000000FF);
				R = ((curPixel >> 16) & 0x000000FF);
				G = ((curPixel >> 8) & 0x000000FF);
				B = ((curPixel >> 0) & 0x000000FF);

				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
				curPixel = srcPixels[x + y * imageWidth];

			}
		}

		R = G = B = 0;
		A = 128;

		idx = (int) (((p1.getX()) + (p1.getY()) * imageWidth));
		fillPixel = rtPixels[idx];
		rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
		lista.add(p1);

		while (lista.size() != 0) {
			Point p = lista.remove();
			int px = (int) p.getX();
			int py = (int) p.getY();

			System.out.print("x : " + px + "\ty : " + py + "\n");
			if ((px < imageWidth - 1)
					&& rtPixels[px + 1 + py * imageWidth] == fillPixel) {
				System.out.print("right\n");
				idx = px + 1 + py * imageWidth;
				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
				lista.add(new Point(px + 1, py));
			}
			if ((py < imageHeight - 1)
					&& rtPixels[px + (py + 1) * imageWidth] == fillPixel) {
				System.out.print("up\n");
				idx = px + (py + 1) * imageWidth;
				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
				lista.add(new Point(px, py + 1));
			}
			if ((px > 1) && (rtPixels[px - 1 + py * imageWidth] == fillPixel)) {
				System.out.print("left\n");
				idx = (px - 1) + py * imageWidth;
				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
				lista.add(new Point(px - 1, py));
			}

			if ((py > 1) && (rtPixels[px + (py - 1) * imageWidth] == fillPixel)) {
				System.out.print("down\n");
				idx = px + (py - 1) * imageWidth;

				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
				lista.add(new Point(px, py - 1));
			}

		}
		System.out.print("retuuuuuuuuuurn\n");
		return rtPixels;
	}

	public static BufferedImage drawLine(BufferedImage image, Point p1,
			Point p2, int line) {
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);
		int curPixel;
		int idx;
		int A, R, G, B;
		int[] srcPixels = new int[imageWidth * imageHeight];
		int[] rtPixels = new int[imageWidth * imageHeight];

		image.getRGB(0, 0, imageWidth, imageHeight, srcPixels, 0, imageWidth);

		BufferedImage dest = new BufferedImage(imageWidth, imageHeight,
				image.getType());

		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				idx = (x) + (y) * imageWidth;
				curPixel = srcPixels[x + y * imageWidth];

				A = ((curPixel >> 24) & 0x000000FF);
				R = ((curPixel >> 16) & 0x000000FF);
				G = ((curPixel >> 8) & 0x000000FF);
				B = ((curPixel >> 0) & 0x000000FF);

				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
				curPixel = srcPixels[x + y * imageWidth];

			}
		}
		if (p1.getX() > p2.getX()) {
			Point temp = p2;
			p2 = p1;
			p1 = temp;
		}
		R = G = B = 0;
		A = 255;
		if (p1 == null || p2 == null)
			return null;
		int x1 = (int) (p1.getX());
		int x2 = (int) (p2.getX());
		int y1 = (int) (p1.getY());
		int y2 = (int) (p2.getY());

		int dy, dx, sx, sy;

		dx = x2 - x1;
		dy = y2 - y1;

		sx = dx < 0 ? -1 : (dx > 0 ? 1 : 0);
		sy = dy < 0 ? -1 : (dy > 0 ? 1 : 0);

		dx = Math.abs(dx);
		dy = Math.abs(dy);
		line++;
		if (dy < dx) {
			int dE = 2 * dy;
			int dNE = 2 * (dy - dx);
			int d = 2 * dy - dx;

			while ((x1 - x2) * sx <= 0) {
				for (int i = 0; i < line / 2; i++) {
					idx = (x1) + (y1 + i) * imageWidth;
					rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
					idx = (x2) + (y2 + i) * imageWidth;
					rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
					idx = (x1) + (y1 - i) * imageWidth;
					rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
					idx = (x2) + (y2 - i) * imageWidth;
					rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
				}
				x1 += sx;
				x2 -= sx;
				if (d < 0)
					d += dE;
				else {
					d += dNE;
					y1 += sy;
					y2 -= sy;
				}
			}
		} else {
			int dE = 2 * dx;
			int dNE = 2 * (dx - dy);
			int d = 2 * dx - dy;
			while ((y1 - y2) * sy <= 0) {
				for (int i = 0; i < line / 2; i++) {
					idx = (x1 + i) + (y1) * imageWidth;
					rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
					idx = (x2 + i) + (y2) * imageWidth;
					rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
					idx = (x1 - i) + (y1) * imageWidth;
					rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
					idx = (x2 - i) + (y2) * imageWidth;
					rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
				}
				y1 += sy;
				y2 -= sy;
				if (d < 0)
					d += dE;
				else {
					d += dNE;
					x1 += sx;
					x2 -= sx;
				}
			}
		}
		dest.setRGB(0, 0, imageWidth, imageHeight, rtPixels, 0, imageWidth);

		// scale

		return dest;

	}

}
