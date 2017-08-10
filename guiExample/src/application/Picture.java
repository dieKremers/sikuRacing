package application;

import org.opencv.core.Mat;

public class Picture 
{
		public Picture(Mat mat, Double time) {
			super();
			this.mat = mat;
			this.time = time;
		}
		private Mat mat = null;
		private Double time = 0.0;
		public Mat getMat() {
			return mat;
		}
		public void setMat(Mat mat) {
			this.mat = mat;
		}
		public Double getTime() {
			return time;
		}
		public void setTime(Double time) {
			this.time = time;
		}		
}
