package org.delaunay;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.delaunay.algorithm.Triangulation;
import org.delaunay.algorithm.Triangulation.DebugLogger;
import org.delaunay.algorithm.Triangulation.InvalidVertexException;
import org.delaunay.algorithm.Triangulations;
import org.delaunay.dtfe.BasicDensityModel;
import org.delaunay.dtfe.ColorScales;
import org.delaunay.dtfe.DensityModel;
import org.delaunay.dtfe.DtfeTriangulationMap;
import org.delaunay.dtfe.interpolation.InterpolationStrategies;
import org.delaunay.dtfe.painters.DtfePainter;
import org.delaunay.dtfe.painters.DtfePainterModel;
import org.delaunay.dtfe.painters.TriangulationPainter;
import org.delaunay.dtfe.painters.TriangulationPainterModel;
import org.delaunay.model.Vertex;

public class Demo {
	private static final int WIDTH = 800;
	private static final int HEIGHT = 800;

	public static void drawTriangulation(Triangulation t, int w, int h, String filename)
			throws IOException {
		
		TriangulationPainter painter = new TriangulationPainter(new TriangulationPainterModel()
				.setEdgeColor(new Color(0x2222AA))
				.setEdgeStrokeWidth(1.5f));
		
		Rectangle rect = new Rectangle(0, 0, w, h);
		BufferedImage img = painter.paint(t, rect.getSize(), rect);
		ImageIO.write(img, "png", new File(filename));
	}

	public static void drawDtfe(DtfeTriangulationMap<? extends DensityModel> dtfe, int w, int h, String filename)
			throws IOException {
		
		DtfePainter painter = new DtfePainter(new DtfePainterModel()
							.setInterpolationStrategy(InterpolationStrategies.createNaturalNeighbor())
							.setDensityScalar(50)
							.setEdgeColor(new Color(0x10000000, true))
							.setColorScale(ColorScales.PURPLE_TO_GREEN_LINEAR));
		
		Rectangle rect = new Rectangle(0, 0, w, h);
		BufferedImage img = painter.paint(dtfe, rect.getSize(), rect);
		ImageIO.write(img, "png", new File(filename));
	}
	
	public static void threeLiner() throws Exception {
		Triangulation t = new Triangulation();
		t.triangulate(Triangulations.randomVertices(1000, 400, 400));
		Demo.drawTriangulation(t, 400, 400, "triangulation.png");
	}

	public static void createTriangulationAndDtfeDemo() throws InvalidVertexException, IOException {
		// Generate vertices
		long start = System.nanoTime();
		int n = 10000;
		List<Vertex> verts = Triangulations.randomVertices(n, WIDTH, HEIGHT);
		System.out.println(String.format("Time to create %,d vertices: %d msec.", n,
				TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS)));
		
		// Triangulate
		start = System.nanoTime();
		Triangulation t = new Triangulation();
		t.setDebugLogger(new DebugLogger() {
			public void debug(String str) {
				System.out.println(str);
			}
		});
		t.triangulate(verts);
		System.out.println(String.format("Time to triangulate %,d vertices: %d msec.", n,
				TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS)));
		System.out.println(String.format("Average hops per locate: %.2f", (float)t.getHopCount() / t.getLocateCount()));

		// DTFE
		start = System.nanoTime();
		DtfeTriangulationMap<BasicDensityModel> dtfe = new DtfeTriangulationMap<BasicDensityModel>();
		for (Vertex v : Triangulations.randomGaussian(1000, WIDTH, HEIGHT)) {
			dtfe.put(v.x, v.y, new BasicDensityModel());
		}
		dtfe.triangulate();
		System.out.println(String.format("Time to create DTFE: %d msec.", 
				TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS)));
		
		// Draw Results
		System.out.println("Creating images");
		drawTriangulation(t, WIDTH, HEIGHT, "triangulation.png");
		start = System.nanoTime();
		drawDtfe(dtfe, WIDTH, HEIGHT, "dtfe.png");
		System.out.println(String.format("Time to draw DTFE: %d msec.", 
				TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS)));
		System.out.println("Done");
	}

	public static void main(String[] args) throws Exception {
		//threeLiner();
		createTriangulationAndDtfeDemo();
	}
}
