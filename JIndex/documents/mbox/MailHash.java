package documents.mbox;
import java.io.Serializable;
 public class MailHash implements Serializable  {
			private int length;
	        private int hashcode;
	        public MailHash(int length, int hashcode) {
	        	this.length = length;
	        	this.hashcode = hashcode;
	        }
	        public int getLength() {
	            return length;
	        }
	        public int getHashcode() {
	            return hashcode;
	        }
			public void setHashcode(int hashcode) {
				this.hashcode = hashcode;
			}
			public void setLength(int length) {
				this.length = length;
			}
	    }