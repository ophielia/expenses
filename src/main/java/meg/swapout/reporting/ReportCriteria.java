package meg.swapout.reporting;

import meg.swapout.expense.services.DateRangeType;

import java.io.Serializable;
import java.util.Date;

public class ReportCriteria implements Serializable {

    private Long breakoutLevel;

    private DateRangeType daterangetype;

    private Date endDate;

    private Date startDate;

    private String imageDir;

    private Boolean excludeNonExpense;

    public Long categoryId;

    private String month;

    private CompareType comparetype;

    private String year;

    private Long reporttype;
    private String imageweblink;
    private String contextpath;
    private String fullimageweblink;

    private boolean usefulllink = false;

    public Long getBreakoutLevel() {
        return breakoutLevel;
    }

    public void setBreakoutLevel(Long breakoutLevel) {
        this.breakoutLevel = breakoutLevel;
    }

    public DateRangeType getDaterangetype() {
        return daterangetype;
    }

    public void setDaterangetype(DateRangeType daterangetype) {
        this.daterangetype = daterangetype;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getImageDir() {
        return imageDir;
    }

    public void setImageDir(String imageDir) {
        this.imageDir = imageDir;
    }

    public Boolean getExcludeNonExpense() {
        return excludeNonExpense;
    }

    public void setExcludeNonExpense(Boolean excludeNonExpense) {
        this.excludeNonExpense = excludeNonExpense;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public CompareType getComparetype() {
        return comparetype;
    }

    public void setComparetype(CompareType comparetype) {
        this.comparetype = comparetype;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setReportType(Long reporttype) {
        this.reporttype = reporttype;

    }

    public Long getReportType() {
        return this.reporttype;
    }

    public void setUseFullImageLink(boolean usefulllink) {
        this.usefulllink = usefulllink;
    }

    public void setImageLink(String imageweblink) {
        this.imageweblink = imageweblink;
    }

    public String getImageLink() {
        if (!this.usefulllink) {
            return this.imageweblink;
        }
        return this.fullimageweblink;
    }

    public void setFullImageLink(String fullimageweblink) {
        this.fullimageweblink = fullimageweblink;
    }

    public String getFullImageLink() {
        return this.fullimageweblink;
    }

    public void setContextPath(String contextpath) {
        this.contextpath = contextpath;

    }

    public String getContextPath() {
        return this.contextpath;
    }


}
