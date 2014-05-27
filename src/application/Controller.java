package application;

import java.awt.Checkbox;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class Controller {



	

	
	Point p1;
	Point p2;
	Point rectP1, rectP2;
	@FXML
	private ImageView imgView;
	@FXML
	private TextField txtRadius;
	@FXML
	private TextField txtLine;
	@FXML
	private Button btnResize;
	@FXML
	private Button btnClear;
	@FXML
	private TextField txtX;
	@FXML
	private TextField txtY;
	@FXML
	private TextField txtP1X;
	@FXML
	private TextField txtP1Y;
	@FXML
	private TextField txtP2X;
	@FXML
	private TextField txtP2Y;
	@FXML
	private RadioButton radioCircle;
	@FXML
	private RadioButton radioLine;
	@FXML
	private RadioButton radioFill;
	@FXML
	private CheckBox checkClip;

	@FXML
	private void handleCheckbox() {
		if (checkClip.isSelected()) {
			clearImage();
			radioCircle.disableProperty().set(true);
			radioFill.disableProperty().set(true);
			radioLine.selectedProperty().set(true);
			rectP1 = new Point(300, 200);
			rectP2 = new Point(600, 400);
			drawRect(rectP1, rectP2);
		} else {
			clearImage();
			radioCircle.disableProperty().set(false);
			radioFill.disableProperty().set(false);
			rectP1 = null;
			rectP2 = null;
		}
		// draw
	}

	@FXML
	private void btClear() {
		clearImage();
		if (checkClip.isSelected()) {
			rectP1 = new Point(300, 200);
			rectP2 = new Point(600, 400);
			drawRect(rectP1, rectP2);

		}
	}

	@FXML
	private void btResize() {

		Image src = imgView.getImage();
		if (src == null) {
			BufferedImage img = Draw.setImg((int) imgView.getFitWidth(),
					(int) imgView.getFitHeight(), 255, 255, 255);
			Image image = SwingFXUtils.toFXImage(img, null);
			imgView.setImage(image);

		}
		src = imgView.getImage();

	}

	@FXML
	private void msImgViewClicked(MouseEvent e) {
		if (p1 == null) {
			Image src = imgView.getImage();
			if (src == null) {
				BufferedImage img = Draw.setImg((int) imgView.getFitWidth(),
						(int) imgView.getFitHeight(), 255, 255, 255);
				Image image = SwingFXUtils.toFXImage(img, null);
				imgView.setImage(image);
				src = imgView.getImage();
			}
			p1 = new Point(e.getX(), e.getY());
			if (radioFill.isSelected() == true) {
				BufferedImage img = Draw.fill(
						javafx.embed.swing.SwingFXUtils.fromFXImage(src, null),
						p1);
				Image image = SwingFXUtils.toFXImage(img, null);
				imgView.setImage(image);

			}
			return;
		}
		Image src = imgView.getImage();
		if (src == null) {
			BufferedImage img = Draw.setImg((int) imgView.getFitWidth(),
					(int) imgView.getFitHeight(), 255, 255, 255);
			Image image = SwingFXUtils.toFXImage(img, null);
			imgView.setImage(image);
			src = imgView.getImage();

		}
		p2 = new Point(e.getX(), e.getY());
		Draw.printPoint(p1);
		Draw.printPoint(p2);
		// p1.setX(Integer.parseInt(txtP1X.getText()));
		// p1.setY(Integer.parseInt(txtP1Y.getText()));
		BufferedImage img = null;
		if (radioLine.isSelected() == true) {
			if (checkClip.isSelected()) {
				img = Draw.drawClipLine(
						javafx.embed.swing.SwingFXUtils.fromFXImage(src, null),
						p1, p2, rectP1, rectP2,
						Integer.parseInt(txtLine.getText()));
			
			} else {
				img = Draw.drawLine(
						javafx.embed.swing.SwingFXUtils.fromFXImage(src, null),
						p1, p2, Integer.parseInt(txtLine.getText()));
			}
		} else if (radioCircle.isSelected() == true) {
			img = Draw.drawCircle(
					javafx.embed.swing.SwingFXUtils.fromFXImage(src, null), p1,
					p2, Integer.parseInt(txtLine.getText()));
			if (img == null) {
				System.out.print("radius too large \n");
				p1 = null;
				p2 = null;
				return;
			} else if (radioFill.isSelected() == true) {
				img = Draw.fill(
						javafx.embed.swing.SwingFXUtils.fromFXImage(src, null),
						p2);

			}
			Image image = SwingFXUtils.toFXImage(img, null);
			imgView.setImage(image);
		}

		p1 = null;
		p2 = null;

		if (img == null)
			return;
		Image image = SwingFXUtils.toFXImage(img, null);
		imgView.setImage(image);

	}

	private void clearImage() {
		Image src = imgView.getImage();
		if (src == null) {
			BufferedImage img = Draw.setImg((int) imgView.getFitWidth(),
					(int) imgView.getFitHeight(), 0, 0, 0);
			Image image = SwingFXUtils.toFXImage(img, null);
			imgView.setImage(image);

		}
		src = imgView.getImage();
		BufferedImage img = Draw.clear(javafx.embed.swing.SwingFXUtils
				.fromFXImage(src, null));
		Image image = SwingFXUtils.toFXImage(img, null);
		imgView.setImage(image);
	}

	private void drawRect(Point p1, Point p2) {
		Image src = imgView.getImage();
		BufferedImage img = null;

		Point temp1 = new Point(p1.getX(), p2.getY());
		Point temp2 = new Point(p2.getX(), p1.getY());

		img = Draw.drawLine(
				javafx.embed.swing.SwingFXUtils.fromFXImage(src, null), p1,
				temp1, Integer.parseInt(txtLine.getText()));
		Image image = SwingFXUtils.toFXImage(img, null);
		imgView.setImage(image);
		src = imgView.getImage();

		img = Draw.drawLine(
				javafx.embed.swing.SwingFXUtils.fromFXImage(src, null), temp1,
				p2, Integer.parseInt(txtLine.getText()));
		image = SwingFXUtils.toFXImage(img, null);
		imgView.setImage(image);
		src = imgView.getImage();

		img = Draw.drawLine(
				javafx.embed.swing.SwingFXUtils.fromFXImage(src, null), p2,
				temp2, Integer.parseInt(txtLine.getText()));
		image = SwingFXUtils.toFXImage(img, null);
		imgView.setImage(image);
		src = imgView.getImage();

		img = Draw.drawLine(
				javafx.embed.swing.SwingFXUtils.fromFXImage(src, null), temp2,
				p1, Integer.parseInt(txtLine.getText()));
		image = SwingFXUtils.toFXImage(img, null);
		imgView.setImage(image);

	}
}
