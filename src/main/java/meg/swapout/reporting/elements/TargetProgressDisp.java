package meg.swapout.reporting.elements;

import java.text.DecimalFormat;


public class TargetProgressDisp {

	private String catName;

		private double amountSpent;
		
		private double amountTargeted;
		
		private Long catId;
		
		private DecimalFormat decform= new DecimalFormat("##0.##");

		public double getAmountSpent() {
			return amountSpent;
		}

		public void setAmountSpent(double amountSpent) {
			this.amountSpent = Math.abs(amountSpent);
		}

		public double getAmountTargeted() {
			return amountTargeted;
		}

		public void setAmountTargeted(double amountTargeted) {
			this.amountTargeted = amountTargeted;
		}

		public Long getCatId() {
			return catId;
		}

		public void setCatId(Long catId) {
			this.catId = catId;
		}

		public String getCatName() {
			return catName;
		}

		public void setCatName(String catName) {
			this.catName = catName;
		}

		public boolean spendingExceedsTarget() {
			return amountTargeted>0&&amountSpent>amountTargeted;
		}

		public double getExceededAmount() {
			return amountSpent-amountTargeted;
		}

		public boolean spendingEqualsTarget() {
			return amountSpent==amountTargeted;
		}

		public boolean targetDoesntExist() {
			return amountTargeted==0;
		}
		
		public String getStatusMessage() {
			if (spendingEqualsTarget()) {
				return "Target Met";
			} else if (spendingExceedsTarget()) {
				double percentage =  getAmountSpent()/getAmountTargeted() ;
				percentage = Math.round(Math.abs(1-percentage)*100);
				
				return "Target exceeded by " + decform.format(getExceededAmount()) + " Euros ("+percentage+"%)"; 
			} else if (targetDoesntExist()) {
				return "No Target";
			} else {
				double percentage =  getAmountSpent()/getAmountTargeted() ;
				percentage = Math.round(percentage*100);
				return percentage + "% of Target spent";
			}
		}



}
