package application;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;

public final class OpenCvUtils 
{
	/**
	 * 
	 * @param image: The Image that should be trasferred to a Matrix
	 * @parame toGreyscale: If set to True the matrix will be returned in Greyscale
	 * @return Matrix of Image in Greyscale
	 */
	public static Mat imageToMat(Image image, boolean toGreyscale ) 
	{
	    int width = (int) image.getWidth();
	    int height = (int) image.getHeight();
	    byte[] buffer = new byte[width * height * 4];

	    PixelReader reader = image.getPixelReader();
	    WritablePixelFormat<ByteBuffer> format = WritablePixelFormat.getByteBgraInstance();
	    reader.getPixels(0, 0, width, height, format, buffer, 0, width * 4);

	    Mat mat = new Mat(height, width, CvType.CV_8UC4);
	    mat.put(0, 0, buffer);
	    if( toGreyscale ) {
	    	Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
	    }
	    return mat;
	}
    
	/**
     * Convert a Mat object (OpenCV) in the corresponding Image for JavaFX
     *
     * @param frame
     *            the {@link Mat} representing the current frame
     * @return the {@link Image} to show
     */
    public static Image mat2Image(Mat frame) {
        // create a temporary buffer
        MatOfByte buffer = new MatOfByte();
        // encode the frame in the buffer, according to the PNG format
        Imgcodecs.imencode(".png", frame, buffer);
        // build and return an Image created from the image encoded in the
        // buffer
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }
    
    public static Mat rotate(Mat src, double angle )
    {
        int cols = src.cols();
        int rows = src.rows();
        Point pt = new Point(cols/2, rows/2);
        Mat r = Imgproc.getRotationMatrix2D(pt, angle, 1.0);
        Mat dst = new Mat();
        Imgproc.warpAffine(src, dst, r, new Size(cols, rows));
        return dst;
    }
}
