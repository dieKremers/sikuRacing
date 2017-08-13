package application;

import org.opencv.core.Mat;

public class Picture 
{
		public Picture(Mat _mat, Double _time) {
			super();
			mat = _mat;
			time = _time;
		}
		private Mat mat = null;
		private Double time = 0.0;
		public Mat getMat() {
			Mat _mat = new Mat();
			_mat = mat;
			return _mat;
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
