package com.ml_hw3.com;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

public class Kmeans {
	static int orig_size;
	static double com_size;
	static double compression_ratio;

	public static void main(String[] args) {
		// Inputs given through cmd are extracted
		if (args.length < 3) {
			System.out
					.println("You need to enter 6 command "
							+ "line arguments.Please refer to the readme file for more information.");
			return;
		}
		try {
			int k = Integer.parseInt(args[1]);

			BufferedImage inputImage = ImageIO.read(new File(args[0]));
			BufferedImage kvalue = kmeans_preprocess(inputImage,
					Integer.parseInt(args[1]));
			ImageIO.write(kvalue, "png", new File(args[2]));
			BufferedImage compressedImage = ImageIO.read(new File(args[2]));
			compute(compressedImage, k);

		}
		// possible exceptions handled
		catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (NumberFormatException e1) {
			System.out.println(e1.getMessage());

		}
	}

	private static boolean check(int[] m1, int[] m2) {
		for (int i = 0; i < m2.length; i++)
			if (m1[i] != m2[i])
				return false;

		return true;
	}

	private static BufferedImage kmeans_preprocess(BufferedImage inputImage,
			int k) {
		int w = inputImage.getWidth();
		int h = inputImage.getHeight();
		BufferedImage newimg = new BufferedImage(w, h, inputImage.getType());
		// used for image processing
		Graphics2D g = newimg.createGraphics();
		g.drawImage(inputImage, 0, 0, w, h, null);
		// computing size of image
		orig_size = w * h;
		// Obtaining color components (RGB) values from the image.
		int[] rgb = new int[(w * h)];
		int count = 0;

		System.out.print((("Original Image size is: " + orig_size)));
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				rgb[count++] = newimg.getRGB(i, j);

			}
		}

		// Procedure call to kmeans algorithm to compress the image
		kmeans(rgb, k);

		// Feeding the new Color components to the image.
		count = 0;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				newimg.setRGB(i, j, rgb[count++]);
			}
		}

		return newimg;
	}

	// k means algorithm implemented using the concept of clustering
	private static void kmeans(int[] rgb, int k) {
		if (rgb.length < k) {
			System.exit(1);
			return;
		}
		double limit = 0.0;
		double distance = 0;
		int center = 0;
		int[] r = new int[k];
		int[] g = new int[k];
		int[] b = new int[k];
		int[] m1 = new int[k];
		int[] m2 = new int[k];
		int[] total = new int[k];
		int[] cluster = new int[rgb.length];
		// according to the algorithm random values of centering is considered
		// initially
		for (int i = 0; i < k; i++) {
			Random random = new Random();
			m2[i] = rgb[random.nextInt(rgb.length)];
		}

		do {
			for (int i = 0; i < m2.length; i++) {
				m1[i] = m2[i];
				total[i] = r[i] = g[i] = b[i] = 0;
			}
			// Finding nearest center
			for (int i = 0; i < rgb.length; i++) {
				limit = Double.MAX_VALUE;
				for (int j = 0; j < m2.length; j++) {
					Color d = new Color(rgb[i]);
					Color e = new Color(m2[j]);
					int diff_r = d.getRed() - e.getRed();
					int diff_g = d.getGreen() - e.getGreen();
					int diff_b = d.getBlue() - e.getBlue();
					distance = Math.sqrt(diff_r * diff_r + diff_g * diff_g
							+ diff_b * diff_b);
					if (distance < limit) {
						limit = distance;
						center = j;
					}
				}
				cluster[i] = center;
				total[center]++;
				Color c = new Color(rgb[i]);
				r[center] += c.getRed();
				g[center] += c.getGreen();
				b[center] += c.getBlue();

			}
			// set center values
			for (int i = 0; i < m2.length; i++) {
				// using procedure to compute average
				int aR = computeAvg(r[i], total[i]);
				int aG = computeAvg(g[i], total[i]);
				int aB = computeAvg(b[i], total[i]);// using hex values here
				m2[i] = ((aR & 0x000000FF) << 16) | ((aG & 0x000000FF) << 8)
						| ((aB & 0x000000FF));
			}
		} while (!check(m1, m2));
		for (int i = 0; i < rgb.length; i++) {
			rgb[i] = m2[cluster[i]];
		}
	}

	private static int computeAvg(double q, double r) {
		int a = (int) (q / r);
		return a;
	}

	private static void compute(BufferedImage compressedImage, int k) {
		int y = compressedImage.getHeight();
		int x = compressedImage.getWidth();
		int z = x * y;
		com_size = ((24 * k) + (z * java.lang.Math.log(k)));
		System.out.println("     ");
		System.out.println("Compressed image size is: " + com_size);
		compression_ratio = (orig_size / com_size);
		System.out.println("Compression Ratio is: " + compression_ratio);
	}

}
