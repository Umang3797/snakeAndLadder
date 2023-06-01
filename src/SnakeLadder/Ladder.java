package SnakeLadder;

public class Ladder {
	// Each ladder will have its head at some number and its tail at a smaller number.
		private int start;
		private int end;
		
		
		public Ladder(int start, int end) {
			this.start=start;
			this.end=end;
		}
		public int getStart() {
			return start;
		}
		public int getEnd() {
			return end;
		}
}
